package devlang.vm;

import devlang.compiler.DevObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Code {
    
    public String name = "main";
    
    public HashMap<Integer, String> localsName;
    public HashMap<String, Integer> localsNameToIndex;
    public DevObject[] locals;
    public HashMap<String, DevObject> localsByName;
    
    public DevObject[] constants;
    public HashMap<DevObject, Integer> constantToIndex;
    
    public byte[] bytecode;
    
    public Code() {
        this.localsName = new HashMap<>(8);
        this.localsNameToIndex = new HashMap<>(8);
        this.locals = new DevObject[8];
        this.localsByName = new HashMap<>(8);
        this.constants = new DevObject[8];
        this.constantToIndex = new HashMap<>(8);
    }
    
    public DevObject getConst(int i) {
        return this.constants[i];
    }
    
    public int getConstIndexByValue(DevObject obj) {
        int i = this.constantToIndex.getOrDefault(obj, -1);
        return i;
    }
    
    public int saveConst(int index, DevObject obj) {
        int i = this.getConstIndexByValue(obj);
        if (i != -1) {
            return i;
        }
        
        constants[index] = obj;
        obj.constant = true;
        constantToIndex.put(obj, index);
        return index;
    }
    
    public DevObject getLocal(int i) {
        return locals[i];
    }
    
    public int getLocalIndexByName(String name) {
        return localsNameToIndex.getOrDefault(name, -1);
    }
   
    public void saveLocal(int arg, String name, DevObject obj) {
        if (obj.constant) {
            throw new RuntimeException("Trying to save a constant as local.");
        }
        localsName.put(arg, name);
        localsNameToIndex.put(name, arg);
        localsByName.put(name, obj);
        this.saveLocal(arg, obj);
    }
    
    public void saveLocal(int arg, DevObject obj) {
        if (obj.constant) {
            obj = obj.clone();
        }
        locals[arg] = obj;
    }
}
