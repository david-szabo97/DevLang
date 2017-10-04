package devlang.vm;

public class BinaryOpcodes {
    public static final int NONE = 0;
    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int DIVIDE = 3;
    public static final int MULTIPLY = 4;
    public static final int MODULO = 5;
    
    public static int operatorToCode(String operator) {
        if (operator.equals("+"))  return PLUS;
        if (operator.equals("-"))  return MINUS;
        if (operator.equals("/"))  return DIVIDE;
        if (operator.equals("*"))  return MULTIPLY;
        if (operator.equals("%"))  return MODULO;
        return NONE;
    }
    
    public static String opcodeToString(int op) {
        return (new String[]{"","+", "-", "/", "*", "%"})[op];
    }
}
