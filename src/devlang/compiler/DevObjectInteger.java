package devlang.compiler;

import devlang.vm.BinaryOpcodes;
import devlang.vm.CompareOpcodes;
import devlang.vm.UnaryOpcodes;

public class DevObjectInteger extends DevObject implements Cloneable {
    
    public static final DevObjectInteger ZERO = new DevObjectInteger(0);
    public static final DevObjectInteger ONE = new DevObjectInteger(1);
    
    public static final DevObjectInteger CALC = new DevObjectInteger(0);
    
    public int value;
    
    public DevObjectInteger(int value) {
        super(DevObjectType.INTEGER);
        this.value = value;
        ++DevObjectInteger.n;
    } 
    public static int n = 0;
    
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof DevObjectInteger)) return false;
        DevObjectInteger b = (DevObjectInteger) other;
        return this.value == b.value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.value;
        return hash;
    }
    
    @Override
    public Object toJava() {
        return this.value;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        DevObjectInteger obj1 = this;
        if (obj.type == DevObjectType.INTEGER) {
            DevObjectInteger obj2 = (DevObjectInteger) obj;
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
                case CompareOpcodes.NOT_EQUAL:
                    return DevObjectBool.from(obj1.value != obj2.value);
                default:
                    return DevObjectBool.FALSE;
            }
        } else if (obj.type == DevObjectType.FLOAT) {
            DevObjectFloat obj2 = (DevObjectFloat) obj;
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
                case CompareOpcodes.NOT_EQUAL:
                    return DevObjectBool.from(obj1.value != obj2.value);
                default:
                    return DevObjectBool.FALSE;
            }
        }
        
        return DevObjectBool.FALSE;
    }

    @Override
    public DevObject unaryOperator(int op) {
        if (op == UnaryOpcodes.MINUS) {
            return new DevObjectInteger(-this.value);
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
        DevObjectInteger obj1 = this;
        if (obj.type == DevObjectType.INTEGER) {
            DevObjectInteger obj2 = (DevObjectInteger)obj;
            switch (op) {
                
                case BinaryOpcodes.PLUS:
                    CALC.value = obj1.value + obj2.value;
                    return CALC;
                case BinaryOpcodes.MINUS:
                    CALC.value = obj1.value - obj2.value;
                    return CALC;
                case BinaryOpcodes.DIVIDE:
                    
                    return new DevObjectFloat((float)obj1.value / obj2.value);
                case BinaryOpcodes.MULTIPLY:
                    CALC.value = obj1.value * obj2.value;
                    return CALC;
                case BinaryOpcodes.MODULO:
                    CALC.value = obj1.value % obj2.value;
                    return CALC;
                default:
                    return DevObject.NULL;

                /*case BinaryOpcodes.PLUS:
                    return new DevObjectInteger(obj1.value + obj2.value);
                case BinaryOpcodes.MINUS:
                    return new DevObjectInteger(obj1.value - obj2.value);
                case BinaryOpcodes.DIVIDE:
                    return new DevObjectFloat((float)obj1.value / obj2.value);
                case BinaryOpcodes.MULTIPLY:
                    return new DevObjectInteger(obj1.value * obj2.value);
                case BinaryOpcodes.MODULO:
                    return new DevObjectInteger(obj1.value % obj2.value);
                default:*/
            }
        } else if (obj.type == DevObjectType.FLOAT) {
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
                case BinaryOpcodes.MODULO:
                    return new DevObjectInteger(obj1.value % (int)obj2.value);
                default:
                    return DevObject.NULL;
            }
        } else if (obj.type == DevObjectType.STRING) {
            if (op == BinaryOpcodes.PLUS) {
                return new DevObjectString(this.value + ((DevObjectString)obj).value);
            }
        }
        
        return DevObject.NULL;
    }
    
    @Override
    public DevObject clone() {
        return new DevObjectInteger(this.value);
    }
    
}
