package devlang.vm;

import static devlang.vm.CompareOpcodes.NONE;

public class UnaryOpcodes {
    public static final int NONE = 0;
    public static final int NEGATE = 1;
    public static final int MINUS = 2;
    
    public static int operatorToCode(String operator) {
        if (operator.equals("!"))  return NEGATE;
        if (operator.equals("-"))  return MINUS;
        return NONE;
    }
    
    public static String opcodeToString(int op) {
        return (new String[]{"NONE", "!", "-"})[op];
    }
}
