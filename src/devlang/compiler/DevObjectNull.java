package devlang.compiler;

import devlang.vm.UnaryOpcodes;

public class DevObjectNull extends DevObject {
        
    public DevObjectNull() {
        super(DevObjectType.NULL);
    } 
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        return other instanceof DevObjectNull;
    }
    
    @Override
    public Object toJava() {
        return null;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        if (this.type == DevObjectType.NULL) {
            return DevObjectBool.TRUE;
        }
        
        return DevObjectBool.FALSE;
    }

    @Override
    public DevObject unaryOperator(int op) {
        if (op == UnaryOpcodes.NEGATE) {
            return DevObjectBool.TRUE;
        }
        
        return this;
    }

    @Override
    public DevObject binaryOperator(int op, DevObject obj) { 
        return obj;
    }
    
    public DevObjectInteger asInteger() {
        return DevObjectInteger.ZERO;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    
    @Override
    public DevObject clone() {
        return DevObject.NULL;
    }
}
