package devlang.vm;

import devlang.compiler.DevObject;

public class Scope {
    public DevObject[] locals;
    public Code code;
    public int pc = 0;
    
    public Scope() {}
    
    public Scope(Code code) {
        int lc = code.locals.length;
        this.locals = new DevObject[lc];
        for (int i = 0; i < lc; i++) {
            if (code.locals[i] != null) {
                this.locals[i] = code.locals[i].clone();
            }
        }
        this.code = code;
    }

    public void saveLocal(int arg, DevObject obj) {
        if (obj.constant) {
            obj = obj.clone();
        }
        locals[arg] = obj;
    }
}
