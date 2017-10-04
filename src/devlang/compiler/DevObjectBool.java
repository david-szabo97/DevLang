package devlang.compiler;

import devlang.vm.CompareOpcodes;
import devlang.vm.UnaryOpcodes;

public class DevObjectBool extends DevObject implements Cloneable {
    
    public static DevObjectBool TRUE = new DevObjectBool(true);
    public static DevObjectBool FALSE = new DevObjectBool(false);
    
    public boolean value;
    
    public DevObjectBool(boolean value) {
        super(DevObjectType.BOOL);
        this.value = value;
    } 
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DevObjectBool)) return false;
        DevObjectBool b = (DevObjectBool) other;
        return this.value == b.value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.value ? 1 : 0);
        return hash;
    }
    
    @Override
    public Object toJava() {
        return this.value;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        DevObjectBool obj1 = this;
        switch (obj.type) {
            case DevObjectType.BOOL:
            {
                DevObjectBool obj2 = (DevObjectBool)obj;
                switch (op) {
                    case CompareOpcodes.EQUAL:
                    case CompareOpcodes.EQUAL_GT:
                    case CompareOpcodes.EQUAL_LT:
                        return from(obj1.value == obj2.value);
                    default:
                        return DevObjectBool.FALSE;
                }
            }
            case DevObjectType.INTEGER:
            {
                DevObjectInteger obj11 = this.asInteger();
                DevObjectInteger obj2 = (DevObjectInteger)obj;
                return obj11.compare(op, obj2);
            }
            case DevObjectType.FLOAT:
            {
                DevObjectInteger obj11 = this.asInteger();
                DevObjectFloat obj2 = (DevObjectFloat)obj;
                return obj11.compare(op, obj2);
            }
            default:
                break;
        }
        return DevObjectBool.FALSE;
    }

    @Override
    public DevObject unaryOperator(int op) {
        if (op == UnaryOpcodes.NEGATE) {
            return from(!this.value);
        }
        
        return this;
    }

    @Override
    public DevObject binaryOperator(int op, DevObject obj) {
        DevObjectBool obj1 = this;
        switch (obj.type) {
            case DevObjectType.INTEGER:
            {
                DevObjectInteger obj11 = this.asInteger();
                DevObjectInteger obj2 = (DevObjectInteger)obj;
                return obj2.binaryOperator(op, obj11);
            }
            case DevObjectType.FLOAT:
            {
                DevObjectInteger obj11 = this.asInteger();
                DevObjectFloat obj2 = (DevObjectFloat)obj;
                return obj2.binaryOperator(op, obj11);
            }
            case DevObjectType.BOOL:
            {
                DevObjectInteger obj11 = this.asInteger();
                DevObjectInteger obj2 = ((DevObjectBool)obj).asInteger();
                return obj11.binaryOperator(op, obj2);
            }
            default:
                break;
        }
        return DevObject.NULL;
    }
    
    public DevObjectInteger asInteger() {
        return (this.value) ? DevObjectInteger.ONE : DevObjectInteger.ZERO;
    }
    
    public static DevObjectBool from(boolean bool) {
        return (bool) ? DevObjectBool.TRUE : DevObjectBool.FALSE;
    }

    @Override
    public DevObject clone() {
        return new DevObjectBool(this.value);
    }
}
