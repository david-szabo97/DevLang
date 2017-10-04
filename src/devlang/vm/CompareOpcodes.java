package devlang.vm;

public class CompareOpcodes {
    public static final int NONE = 0;
    public static final int EQUAL = 1;
    public static final int LESSER_THAN = 2;
    public static final int GREATER_THAN = 3;
    public static final int EQUAL_LT = 4;
    public static final int EQUAL_GT = 5;
    public static final int NOT_EQUAL = 6;
    
    public static int operatorToCode(String operator) {
        if (operator.equals("==")) return EQUAL;
        if (operator.equals("<"))  return LESSER_THAN;
        if (operator.equals(">"))  return GREATER_THAN;
        if (operator.equals("<=")) return EQUAL_LT;
        if (operator.equals(">=")) return EQUAL_GT;
        if (operator.equals("!=")) return NOT_EQUAL;
        return NONE;
    }
    
    public static String opcodeToString(int op) {
        return (new String[]{"","==", "<", ">", "<=", ">=", "!="})[op];
    }
}
