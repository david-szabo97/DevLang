package devlang.vm;

import devlang.compiler.CodeBuilder;
import devlang.compiler.DevObject;
import devlang.compiler.DevObjectBool;
import devlang.compiler.DevObjectFunction;
import devlang.compiler.DevObjectInteger;
import devlang.compiler.DevObjectJavaBinding;
import devlang.compiler.DevObjectType;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VM {

    private Scope[] callStack = new Scope[32];
    private byte callStackPointer = -1;
    private final DevObject[] objectStack = new DevObject[128];
    private byte objectStackPointer = -1;
    private Scope currentBlock;
    

    private final HashMap<String, DevObjectJavaBinding> bindings = new HashMap<>(32);
    private Code entryPoint = null;

    public VM(byte[] bytecode) {
        try {
            this.entryPoint = CodeBuilder.fromBytes(bytecode);
        } catch (IOException ex) {
            Logger.getLogger(VM.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.bindings.put("print", new DevObjectJavaBinding(System.out, "println", 1));
    }
    int clones = 0;
    public static final int ff = 0xFF;
    public static final int eight = 8;
    byte[] bytecode;
    int bytecodeLength;
    public void run() {
        this.pushBlock(entryPoint);

        byte opcode;
        int arg;
        DevObject obj;
        while (this.callStackPointer >= 0) {
            this.currentBlock = this.callStack[this.callStackPointer];
            while (this.currentBlock.pc < bytecodeLength) {
                opcode = bytecode[this.currentBlock.pc++];
                arg = ((bytecode[this.currentBlock.pc++] & ff) >> eight) | (bytecode[this.currentBlock.pc++] & ff);
                /*
                if (this.currentBlock.pc < this.currentBlock.bytecode.length) {
                    this.nextOpcode = this.currentBlock.bytecode[this.currentBlock.pc];
                } else {
                    this.nextOpcode = -1;
                }
                */
                //this.printInstruction(opcode, arg, false);
                switch (opcode) {
                    case Opcodes.LOAD_CONST:
                        obj = currentBlock.code.getConst(arg);
                        this.pushObject(obj);
                        break;
                    case Opcodes.INC_LOCAL:
                        obj = currentBlock.locals[arg];
                        if (obj.constant) {
                            this.currentBlock.saveLocal(arg, new DevObjectInteger(((DevObjectInteger)obj).value + 1));
                        } else {
                            ++((DevObjectInteger)obj).value;
                        }
                        //this.currentBlock.saveLocal(arg, new DevObjectInteger(((DevObjectInteger)currentBlock.getLocal(arg)).value + 1));
                        break;
                    case Opcodes.STORE_LOCAL:
                        obj = this.popObject();
                        this.currentBlock.saveLocal(arg, obj.clone());
                        break;
                    case Opcodes.LOAD_LOCAL:
                        obj = currentBlock.locals[arg];
                        if (obj != null) {
                            this.objectStack[++this.objectStackPointer] = obj;
                        } else {
                            String name = currentBlock.code.localsName.get(arg);
                            DevObjectJavaBinding binding = this.bindings.get(name);
                            if (binding != null) {
                                this.pushObject(binding);
                            } else {
                                for (int i = this.callStackPointer; --i >= 0;) {
                                    Scope parent = this.callStack[i];
                                    int objIndex = parent.code.getLocalIndexByName(name);
                                    if (objIndex != -1) {
                                        obj = parent.locals[objIndex];
                                        currentBlock.saveLocal(arg, obj);
                                        this.objectStack[++this.objectStackPointer] = obj;
                                        break;
                                    }
                                }

                                // throw new RuntimeException("Undefined local!!!");
                            }
                        }
                        break;
                    case Opcodes.CALL_FUNCTION:
                        obj = this.popObject();
                        switch (obj.type) {
                            case DevObjectType.BINDING:
                                DevObject obj2 = this.popObject();
                                this.objectStack[++this.objectStackPointer] = (DevObject) (((DevObjectJavaBinding) obj).call(obj2.toJava()));
                                break;
                            case DevObjectType.FUNCTION:
                                DevObjectFunction fn = (DevObjectFunction) obj;
                                for (int i = arg; --i >= 0;) {
                                    fn.value.saveLocal(i, this.popObject());
                                }
                                this.pushBlock(fn.value);
                                break;
                            default:
                                throw new RuntimeException("Call failed: " + obj.toJava());
                        }
                        break;
                    case Opcodes.BIN_OP:
                        DevObject obj2 = this.popObject();
                        DevObject obj1 = this.popObject();

                        this.pushObject(obj1.binaryOperator(arg, obj2));
                        break;
                    case Opcodes.COMP_OP:
                        obj2 = this.popObject();
                        obj1 = this.popObject();

                        this.pushObject(obj1.compare(arg, obj2));
                        break;
                    case Opcodes.POP_JUMP_IF_FALSE:
                        obj1 = this.popObject();
                        if (!((DevObjectBool) obj1).value) {
                            this.currentBlock.pc = arg;
                        }
                        break;
                    case Opcodes.POP_JUMP_IF_TRUE:
                        obj1 = this.popObject();
                        if (((DevObjectBool) obj1).value) {
                            this.currentBlock.pc = arg;
                        }
                        break;
                    case Opcodes.JUMP_ABS:
                        this.currentBlock.pc = arg;
                        break;
                    case Opcodes.JUMP_FORWARD:
                        this.currentBlock.pc += arg + 3;
                        break;
                    case Opcodes.JUMP_BACKWARD:
                        this.currentBlock.pc -= arg + 3;
                        break;
                    case Opcodes.RETURN:
                        this.currentBlock = this.callStack[--this.callStackPointer];
                        bytecodeLength = this.currentBlock.code.bytecode.length;
                        bytecode = this.currentBlock.code.bytecode;
                        break;
                    case Opcodes.POP_OBJECT:
                        --this.objectStackPointer;
                }
            }
            --this.callStackPointer;
        }        
        //System.out.println("Clones: "+clones);
        if (this.objectStackPointer != -1) {
           // throw new RuntimeException("SOMETHING WENT WRONG");
        }
    }

    int nextOpcode = 0;
    public void dis() {
        this.pushBlock(entryPoint);
        
        while (this.callStackPointer >= 0) {
            this.currentBlock = this.callStack[this.callStackPointer];
            while (this.currentBlock.pc < this.currentBlock.code.bytecode.length) {
                byte opcode = this.currentBlock.code.bytecode[this.currentBlock.pc];
                int arg = ((this.currentBlock.code.bytecode[this.currentBlock.pc + 1] & 0xFF) >> 8) | (this.currentBlock.code.bytecode[this.currentBlock.pc + 2] & 0xFF);
                this.currentBlock.pc += 3;
                
                this.printInstruction(opcode, arg, true);
            }
            --this.callStackPointer;
            System.out.println();
        }
    }

    public void printInstruction(byte opcode, int arg, boolean recursive) {
        int indent = this.callStackPointer;
        for (int i = 0; i < indent - 1; i++) {
            System.out.print(">");
        }
        System.out.print("[" + this.currentBlock.code.name + "] " + (this.currentBlock.pc - 3) + " " + Opcodes.opcodeToString(opcode) + " " + arg+" (os: "+this.objectStackPointer+")");

        switch (opcode) {
            case Opcodes.STORE_LOCAL:
            case Opcodes.LOAD_LOCAL:
                DevObject obj = this.currentBlock.locals[arg];
                System.out.print(" (" + this.currentBlock.code.localsName.get(arg) + " = " + ((obj != null) ? obj.toJava() : null) + ") ");
                break;
            case Opcodes.LOAD_CONST:
                System.out.print(" (" + this.currentBlock.code.constants[arg].toJava() + ") ");
                
                if (this.currentBlock.code.constants[arg].type == DevObjectType.FUNCTION) {
                    if (recursive) {
                        System.out.println();
                        this.pushBlock(((DevObjectFunction) this.currentBlock.code.constants[arg]).value);
                    }
                }
                break;
            case Opcodes.COMP_OP:
                System.out.print(" (" + CompareOpcodes.opcodeToString(arg) + ") ");
                break;
            case Opcodes.BIN_OP:
                System.out.print(" (" + BinaryOpcodes.opcodeToString(arg) + ") ");
                break;
            case Opcodes.UNARY_OP:
                System.out.print(" (" + UnaryOpcodes.opcodeToString(arg) + ") ");
                break;
            default:
                break;
        }

        System.out.println();
    }

    private void pushBlock(Code value) {
        Scope s = new Scope(value);
        this.callStack[++this.callStackPointer] = s;
        this.currentBlock = s;
        bytecodeLength = this.currentBlock.code.bytecode.length;
        bytecode = this.currentBlock.code.bytecode;
    }

    private void pushObject(DevObject obj) {
        //System.out.println("Push at "+this.objectStackPointer);
        this.objectStack[++this.objectStackPointer] = obj;
    }

    private DevObject popObject() {
        return this.objectStack[this.objectStackPointer--];
    }
}
