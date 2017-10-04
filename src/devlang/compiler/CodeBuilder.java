package devlang.compiler;

import devlang.vm.Code;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Format:
/*
locals count (1 byte)
locals....
consts count (1 byte)
consts....
bytecode length (4 byte)
bytecode
*/

public class CodeBuilder extends Code {
    
    int lastLocal = 0;
    int lastConst = 0;
    
    public int bytesWritten = 0;
    
    public CodeBuilder() {
        super();
        this.bytecode = new byte[8192];
    }
        
    public byte[] build() throws IOException {
        byte[] bytecode = new byte[this.bytesWritten];
        System.arraycopy(this.bytecode, 0, bytecode, 0, bytecode.length);
        this.bytecode = bytecode;
        return CodeBuilder.build(this);
    }
    
    public static byte[] build(CodeBuilder code) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream w = new DataOutputStream(baos);
        
        w.writeUTF(code.name);
        w.writeByte(code.localsName.size());
        for (int i = 0; i < code.localsName.size(); i++) {
            byte[] bytes = code.localsName.get(i).getBytes("ascii");
            w.writeByte(bytes.length);
            w.write(bytes);
        }
        w.writeByte(code.lastConst);
        for (int i = 0; i < code.lastConst; i++) {
            DevObject obj = code.constants[i];
            w.writeByte(obj.type);
            switch (obj.type) {
                case DevObjectType.INTEGER:
                    w.writeInt(((DevObjectInteger)obj).value);
                    break;
                case DevObjectType.FLOAT:
                    w.writeDouble(((DevObjectFloat)obj).value);
                    break;
                case DevObjectType.STRING:
                    w.writeUTF(((DevObjectString)obj).value);
                    break;
                case DevObjectType.BOOL:
                    w.writeByte((((DevObjectBool)obj).value) ? 1 : 0);
                    break;
                case DevObjectType.FUNCTION:
                    w.write(((CompilerObjectFunction)obj).builder.build());
                    break;
                default:
                    break;
            }
        }
        w.writeInt(code.bytecode.length);
        w.write(code.bytecode, 0, code.bytecode.length);
        
        return baos.toByteArray();
    }
    
    public static Code fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream r = new DataInputStream(bais);
        
        return fromBytes(bais, r);
    }
    
    public static Code fromBytes(ByteArrayInputStream bais, DataInputStream r) throws IOException {
        //System.out.println("---- Reading block");
        
        Code b = new Code();
        b.name = r.readUTF();
        int locals = r.readByte();
        //System.out.println("\tReading "+locals+" local names: ");
        for (int i = 0; i < locals; i++) {
            byte[] bbb = new byte[r.readByte()];
            r.read(bbb, 0, bbb.length);
            b.localsName.put(i, new String(bbb, "ascii"));
            b.localsNameToIndex.put(new String(bbb, "ascii"), i);
            //System.out.println("\t\t"+b.localsName.get(i));
        }
        int consts = r.readByte();
        //System.out.println("\tReading "+consts+" constant values: ");
        for (int i = 0; i < consts; i++) {
            int t = r.readByte();
            switch (t) {
                case DevObjectType.INTEGER:
                    int a = r.readInt();
                    //System.out.println("\t\t"+i+" = number = "+a);
                    b.saveConst(i, new DevObjectInteger(a));
                    break;
                case DevObjectType.FLOAT:
                    //System.out.println("\t\t"+i+" = float");
                    b.saveConst(i, new DevObjectFloat(r.readDouble()));
                    break;
                case DevObjectType.STRING:
                    //System.out.println("\t\t"+i+" = string");
                    b.saveConst(i, new DevObjectString(r.readUTF()));
                    break;
                case DevObjectType.BOOL:
                    //System.out.println("\t\t"+i+" = string");
                    b.saveConst(i, DevObjectBool.from(r.readByte() == 1));
                    break;
                case DevObjectType.FUNCTION:
                    //System.out.println("\t\t"+i+" = function");
                    b.saveConst(i, new DevObjectFunction(CodeBuilder.fromBytes(bais, r)));
                    break;
                default:
                    break;
            }
        }
        
        int bytecodeLength = r.readInt();
        //System.out.println("\tbytecode length: "+bytecodeLength);
        byte[] bytecode = new byte[bytecodeLength];
        r.read(bytecode, 0, bytecodeLength);
        b.bytecode = bytecode;
        //System.out.println("\tbytecode: "+Arrays.toString(bytecode));
        
        return b;
    }
    
    public int saveConst(DevObject obj) {
        int i = this.getConstIndexByValue(obj);
        if (i != -1) {
            return i;
        }
        
        constants[lastConst] = obj;
        constantToIndex.put(obj, lastConst);
        return lastConst++;
    }
    
    public int saveLocal(String name) {
        int i = this.getLocalIndexByName(name);
        if (i != -1) {
            return i;
        }
        
        localsName.put(lastLocal, name);
        localsNameToIndex.put(name, lastLocal);
        return lastLocal++;
    }
    
    public int saveLocal(String name, DevObject obj) {
        int i = this.saveLocal(name);
        localsByName.put(name, obj);
        locals[i] = obj;
        return i;
    }
    
}
