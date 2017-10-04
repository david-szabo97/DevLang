package devlang.compiler;

import devlang.parser.Parser;
import devlang.parser.nodes.Node;
import devlang.parser.nodes.NodeBranch;
import devlang.parser.nodes.NodeCall;
import devlang.parser.nodes.NodeCondition;
import devlang.parser.nodes.NodeFor;
import devlang.parser.nodes.NodeFunction;
import devlang.parser.nodes.NodeIdentifier;
import devlang.parser.nodes.NodeOperator;
import devlang.parser.nodes.NodeUnary;
import devlang.parser.nodes.NodeWhile;
import devlang.vm.BinaryOpcodes;
import devlang.vm.CompareOpcodes;
import devlang.vm.Opcodes;
import static devlang.vm.VM.eight;
import static devlang.vm.VM.ff;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

public class Compiler {

    private final Parser parser;

    CodeBuilder block = new CodeBuilder();
    ArrayList<CodeBuilder> blocks = new ArrayList<>();
    
    public Compiler(Parser parser) {
        this.parser = parser;
        this.blocks.add(block);
    }

    public byte[] compile() throws Exception {
        Node node = this.parser.parse();
        this.compileNode(node, false);
        this.autoPopCallIfNeeded();
        
        return this.block.build();
    }

    private void compileNode(Node node, boolean needVar) {
        if (node == null) {
            return;
        }
        
        if (null == node.type) {
            this.compileNode(node.left, false);
            this.compileNode(node.right, false);
        } else {
            switch (node.type) {
                case UNARY:
                    String operator = ((NodeUnary) node).value;
                    if ("++".equals(operator)) {
                        int memIndex = block.saveLocal(((NodeIdentifier) node.left).value);
                        this.addInstruction(Opcodes.INC_LOCAL, memIndex);
                    }
                    break;
                case OPERATOR:
                    operator = ((NodeOperator) node).value;
                    if (operator.equals("=")) {
                        int memIndex = block.saveLocal(((NodeIdentifier) node.left).value);
                        this.compileNode(node.right, true);
                        this.addInstruction(Opcodes.STORE_LOCAL, memIndex);
                    }
                    if (operator.equals("+=")) {
                        int memIndex = block.saveLocal(((NodeIdentifier) node.left).value);
                        this.addInstruction(Opcodes.LOAD_LOCAL, memIndex);
                        this.compileNode(node.right, true);
                        this.addInstruction(Opcodes.BIN_OP, BinaryOpcodes.PLUS);
                        this.addInstruction(Opcodes.STORE_LOCAL, memIndex);
                    }
                    if (BinaryOpcodes.operatorToCode(operator) != BinaryOpcodes.NONE) {
                        this.compileNode(node.left, true);
                        this.compileNode(node.right, true);
                        this.addInstruction(Opcodes.BIN_OP, BinaryOpcodes.operatorToCode(operator));
                    }
                    break;
                case CALL: {
                    String name = ((NodeIdentifier) node.left).value;
                    int memIndex = block.getLocalIndexByName(name);
                    if (memIndex == -1) {
                        memIndex = block.saveLocal(name);
                    }
                    for (Node n : ((NodeCall) node).args) {
                        this.compileNode(n, true);
                    }
                    this.addInstruction(Opcodes.LOAD_LOCAL, memIndex);
                    this.addInstruction(Opcodes.CALL_FUNCTION, ((NodeCall) node).args.length);
                    break;
                }
                case INTEGER:
                case FLOAT:
                case STRING:
                case BOOL: {
                    if (!needVar) break;
                    
                    int memIndex = block.saveConst(DevObject.from(node));
                    this.addInstruction(Opcodes.LOAD_CONST, memIndex);
                    break;
                }
                case IDENTIFIER: {
                    if (!needVar) break;
                    String name = ((NodeIdentifier) node).value;
                    int memIndex = block.getLocalIndexByName(name);
                    if (memIndex == -1) {
                        throw new RuntimeException("Undefined variable.");
                    }
                    this.addInstruction(Opcodes.LOAD_LOCAL, memIndex);
                    break;
                }
                case BRANCH:
                    NodeBranch nb = (NodeBranch) node;
                    this.compileNode(nb.condition, false);
                    int elseInstructionIndex = this.skipAndMarkInstruction();
                    this.compileNode(node.right, false);
                    int after = this.block.bytesWritten;
                    this.compileNode(node.left, false);
                    if (node.right == null) {
                        this.addInstruction(elseInstructionIndex, Opcodes.POP_JUMP_IF_FALSE, this.block.bytesWritten);
                    } else {
                        this.addInstruction(elseInstructionIndex, Opcodes.POP_JUMP_IF_TRUE, after);
                    }
                    
                    break;
                case WHILE: {
                    NodeWhile nw = (NodeWhile) node;
                    int conditionPc = this.block.bytesWritten;
                    this.compileNode(nw.condition, true);
                    int jumpInstructionIndex = this.skipAndMarkInstruction();
                    this.compileNode(node.left, false);
                    this.compileNode(node.right, false);
                    this.addInstruction(Opcodes.JUMP_BACKWARD, this.block.bytesWritten - conditionPc);
                    this.addInstruction(jumpInstructionIndex, Opcodes.POP_JUMP_IF_FALSE, this.block.bytesWritten);
                    break;
                }
                case FOR: {
                    NodeFor nw = (NodeFor) node;
                    this.compileNode(nw.left, false);
                    int conditionPc = this.block.bytesWritten;
                    this.compileNode(nw.condition, true);
                    int jumpInstructionIndex = this.skipAndMarkInstruction();
                    this.compileNode(node.right, false);
                    this.addInstruction(Opcodes.JUMP_BACKWARD, this.block.bytesWritten - conditionPc);
                    this.addInstruction(jumpInstructionIndex, Opcodes.POP_JUMP_IF_FALSE, this.block.bytesWritten);
                    break;
                }
                case CONDITION:
                    this.compileNode(node.left, true);
                    this.compileNode(node.right, true);
                    this.addInstruction(Opcodes.COMP_OP, CompareOpcodes.operatorToCode(((NodeCondition) node).value));
                    break;
                case RETURN:
                    this.compileNode(node.left, true);
                    this.compileNode(node.right, true);
                    this.addInstruction(Opcodes.RETURN, 0);
                    break;
                case FUNCTION: {
                    CodeBuilder old = this.block;
                    this.block = new CodeBuilder();
                    this.blocks.add(block);
                    CodeBuilder newBlock = this.block;
                    newBlock.name = ((NodeFunction) node).name;
                    for (String n : ((NodeFunction) node).args) {
                        block.saveLocal(n);
                    }
                    this.compileNode(node.left, false);
                    this.compileNode(node.right, false);
                    if (!((NodeFunction)node).hasReturn) {
                        int cm = block.saveConst(new DevObjectBool(false));
                        this.addInstruction(Opcodes.LOAD_CONST, cm);
                        this.addInstruction(Opcodes.RETURN, 0);
                    }
                    this.block = old;
                    int cmemIndex = block.saveConst(new CompilerObjectFunction(newBlock, ((NodeFunction)node).hasReturn));
                    this.addInstruction(Opcodes.LOAD_CONST, cmemIndex);
                    int memIndex = block.saveLocal(((NodeFunction) node).name);
                    this.addInstruction(Opcodes.STORE_LOCAL, memIndex);
                    break;
                }
                default:
                    this.compileNode(node.left, false);
                    this.compileNode(node.right, false);
                    break;
            }
        }
    }
    
    private class CBPair {
        int pc;
        CodeBuilder cb;
        
        CBPair(int pc, CodeBuilder cb) {
            this.pc = pc;
            this.cb = cb;
        }
    }
    
    public void autoPopCallIfNeeded() {
        int stack = 0;
        ArrayDeque<CBPair> callsAt = new ArrayDeque<>();
        
        for (CodeBuilder block : blocks) {
            int pc = 0;

            while (pc < block.bytesWritten) {
                int opcode = block.bytecode[pc++];
                int arg = ((block.bytecode[pc++] & 0xff) >> 8) | (block.bytecode[pc++] & 0xff);

                switch(opcode) {
                    case Opcodes.BIN_OP:
                    case Opcodes.COMP_OP:
                        stack -= 1;
                        break;
                    case Opcodes.CALL_FUNCTION:
                        stack -= arg + 1;
                        stack++;
                        callsAt.push(new CBPair(pc-3, block));
                        break;
                    case Opcodes.LOAD_LOCAL:
                    case Opcodes.LOAD_CONST:
                        stack++;
                        break;

                    case Opcodes.POP_JUMP_IF_FALSE:
                    case Opcodes.POP_JUMP_IF_TRUE:
                    case Opcodes.STORE_LOCAL:
                        stack--;
                        break;
                }
            }
        }
        
        System.out.println(Arrays.toString(callsAt.toArray()));
        System.out.println("Stack after analyze: "+stack);
        /*while (--stack >= 0) {
            CBPair p = callsAt.pop();
            byte[] b = new byte[8192];
            System.arraycopy(p.cb.bytecode, 0, b, 0, p.pc + 3);
            b[p.pc+3] = Opcodes.POP_OBJECT;
            p.cb.bytesWritten += 3;
            System.arraycopy(p.cb.bytecode, p.pc + 3, b, p.pc + 6, p.cb.bytesWritten + p.pc);
            p.cb.bytecode = b;
        }*/
    }

    public void addInstruction(int instruction, int arg) {
        this.addInstruction(this.block.bytesWritten, instruction, arg);
        this.block.bytesWritten += 3;
    }

    public void addInstruction(int index, int instruction, int arg) {
        this.block.bytecode[index] = (byte) instruction;
        int hi = ((arg & 0xFF) >> 8);
        int lo = (arg & 0xFF);
        this.block.bytecode[index + 1] = (byte) hi;
        this.block.bytecode[index + 2] = (byte) lo;
    }

    public int skipAndMarkInstruction() {
        int pc = this.block.bytesWritten;
        this.block.bytesWritten += 3;
        return pc;
    }

}
