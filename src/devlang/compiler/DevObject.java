package devlang.compiler;

import devlang.parser.nodes.Node;
import devlang.parser.nodes.NodeBool;
import devlang.parser.nodes.NodeFloat;
import devlang.parser.nodes.NodeInteger;
import devlang.parser.nodes.NodeString;

public abstract class DevObject {
    
    public static DevObjectNull NULL = DevObjectNull.NULL;
    
    public int type;
    public boolean constant;
    
    public DevObject(int type) {
        this.type = type;
    }
    
    public static DevObject from(int value) {
        return new DevObjectInteger(value);
    } 
    
    public static DevObject from(String value) {
        return new DevObjectString(value);
    } 
    
    public static DevObject from(boolean value) {
        return DevObjectBool.from(value);
    } 
    
    public static DevObjectFloat from(double value) {
        return new DevObjectFloat(value);
    } 
    
    public static DevObject from(Node node) {
        if (null != node.type) switch (node.type) {
            case INTEGER:
                return from(((NodeInteger)node).value);
            case STRING:
                return from(((NodeString)node).value);
            case BOOL:
                return from(((NodeBool)node).value);
            case FLOAT:
                return from(((NodeFloat)node).value);
            default:
                break;
        }
        
        return null;
    }
    
    public Object toJava() {
        return "Object";
    }
    
    public abstract DevObject clone();
    
    public abstract DevObjectBool compare(int op, DevObject obj);
    public abstract DevObject unaryOperator(int op);
    public abstract DevObject binaryOperator(int op, DevObject obj);
}
