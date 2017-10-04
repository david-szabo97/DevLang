package devlang.compiler;

import devlang.vm.BinaryOpcodes;
import devlang.vm.CompareOpcodes;
import devlang.vm.UnaryOpcodes;
import java.util.Objects;

public class DevObjectString extends DevObject implements Cloneable {
    
    public String value;
    
    public DevObjectString(String value) {
        super(DevObjectType.STRING);
        this.value = value;
    } 
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DevObjectString)) return false;
        DevObjectString b = (DevObjectString) other;
        return this.value.equals(b.value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.value);
        return hash;
    }
    
    @Override
    public Object toJava() {
        return this.value;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        if (obj.type == DevObjectType.STRING) {
            // TODO: Add more string comparison, LT, GT
            if (op == CompareOpcodes.EQUAL || op == CompareOpcodes.EQUAL_GT || op == CompareOpcodes.EQUAL_LT) {
                return DevObjectBool.from(((DevObjectString)obj).value.equalsIgnoreCase(this.value));
            }
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
        if (obj.type == DevObjectType.STRING) {
            if (op == BinaryOpcodes.PLUS) {
                return new DevObjectString(this.value + ((DevObjectString)obj).value);
            }
        }
        
        if (obj.type == DevObjectType.INTEGER) {
            if (op == BinaryOpcodes.PLUS) {
                return new DevObjectString(this.value + ((DevObjectInteger)obj).value);
            }
        }
        
        return DevObject.NULL;
    }
    
    @Override
    public DevObject clone() {
        return new DevObjectString(new String(this.value));
    }
}
