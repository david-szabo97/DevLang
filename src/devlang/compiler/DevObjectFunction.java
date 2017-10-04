package devlang.compiler;

import devlang.vm.Code;
import devlang.vm.UnaryOpcodes;
import java.util.Objects;

public class DevObjectFunction extends DevObject {
    
    public Code value;
    
    public DevObjectFunction(Code value) {
        super(DevObjectType.FUNCTION);
        this.value = value;
    } 
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DevObjectFunction)) return false;
        DevObjectFunction b = (DevObjectFunction) other;
        return this.value.equals(b.value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.value);
        return hash;
    }
    
    @Override
    public Object toJava() {
        return this.value;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        if (obj.type == DevObjectType.FUNCTION) {
            return DevObjectBool.from(((DevObjectFunction)obj).value == this.value);
        }
        
        return DevObjectBool.FALSE;
    }

    @Override
    public DevObject unaryOperator(int op) {
        if (op == UnaryOpcodes.NEGATE) {
            return DevObjectBool.FALSE;
        }
        
        return DevObject.NULL;
    }

    @Override
    public DevObject binaryOperator(int op, DevObject obj) {
        return DevObject.NULL;
    }
    
    @Override
    public DevObject clone() {
        return new DevObjectFunction(this.value);
    }
}
