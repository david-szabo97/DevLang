package devlang.compiler;

import devlang.vm.BinaryOpcodes;
import devlang.vm.CompareOpcodes;
import devlang.vm.UnaryOpcodes;

public class DevObjectFloat extends DevObject implements Cloneable {
    
    public double value;
    
    public DevObjectFloat(double value) {
        super(DevObjectType.FLOAT);
        this.value = value;
    } 
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DevObjectFloat)) return false;
        DevObjectFloat b = (DevObjectFloat) other;
        return this.value == b.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }
    
    @Override
    public Object toJava() {
        return this.value;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        DevObjectFloat obj1 = this;
        switch (obj.type) {
            case DevObjectType.FLOAT:
            {
                DevObjectFloat obj2 = (DevObjectFloat)obj;
                switch (op) {
                    case CompareOpcodes.EQUAL:
                        return DevObjectBool.from(obj1.value == obj2.value);
                    case CompareOpcodes.EQUAL_LT:
                        return DevObjectBool.from(obj1.value <= obj2.value);
                    case CompareOpcodes.EQUAL_GT:
                        return DevObjectBool.from(obj1.value >= obj2.value);
                    case CompareOpcodes.LESSER_THAN:
                        return DevObjectBool.from(obj1.value < obj2.value);
                    case CompareOpcodes.GREATER_THAN:
                        return DevObjectBool.from(obj1.value > obj2.value);
                    default:
                        return DevObjectBool.FALSE;
                }
            }
            case DevObjectType.INTEGER:
            {
                DevObjectInteger obj2 = (DevObjectInteger)obj;
                return obj2.compare(op, obj1);
                
            }
            case DevObjectType.BOOL:
            {
                DevObjectBool obj2 = (DevObjectBool)obj;
                return DevObjectBool.from((obj2.value && this.value == 1) || (!obj2.value && this.value == 0));
            }
            default:
                break;
        }
        
        return DevObjectBool.FALSE;
    }

    @Override
    public DevObject unaryOperator(int op) {
        if (op == UnaryOpcodes.MINUS) {
            return new DevObjectFloat(-this.value);
        } else if (op == UnaryOpcodes.NEGATE) {
            if (this.value == 0) {
                return DevObjectBool.TRUE;
            } 
            
            return DevObjectBool.FALSE;
        }
        
        return DevObject.NULL;
    }

    @Override
    public DevObject binaryOperator(int op, DevObject obj) {
        DevObjectFloat obj1 = this;
        if (obj.type == DevObjectType.FLOAT) {
            DevObjectFloat obj2 = (DevObjectFloat)obj;
            switch (op) {
                case BinaryOpcodes.PLUS:
                    return new DevObjectFloat(obj1.value + obj2.value);
                case BinaryOpcodes.MINUS:
                    return new DevObjectFloat(obj1.value - obj2.value);
                case BinaryOpcodes.DIVIDE:
                    return new DevObjectFloat(obj1.value / obj2.value);
                case BinaryOpcodes.MULTIPLY:
                    return new DevObjectFloat(obj1.value * obj2.value);
                    
                default:
                    return DevObject.NULL;
            }
        } else if (obj.type == DevObjectType.INTEGER) {
            DevObjectInteger obj2 = (DevObjectInteger)obj;
            switch (op) {
                case BinaryOpcodes.PLUS:
                    return new DevObjectFloat(obj1.value + obj2.value);
                case BinaryOpcodes.MINUS:
                    return new DevObjectFloat(obj1.value - obj2.value);
                case BinaryOpcodes.DIVIDE:
                    return new DevObjectFloat(obj1.value / obj2.value);
                case BinaryOpcodes.MULTIPLY:
                    return new DevObjectFloat(obj1.value * obj2.value);
                    
                default:
                    return DevObject.NULL;
            }
        }
        
        return DevObject.NULL;
    }

    @Override
    public DevObject clone() {
        return new DevObjectFloat(this.value);
    }
}
