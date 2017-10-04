package devlang.vm;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Opcodes {
    
    public static final byte LOAD_CONST        = 1;
    
    public static final byte STORE_LOCAL       = 2;
    public static final byte LOAD_LOCAL        = 3;
    
    public static final byte BIN_OP            = 4;
    
    public static final byte UNARY_OP          = 5;
    
    public static final byte CALL_FUNCTION     = 6;
    
    public static final byte COMP_OP           = 7;
    
    public static final byte POP_JUMP_IF_FALSE = 8;
    public static final byte POP_JUMP_IF_TRUE  = 9;
    public static final byte JUMP_ABS          = 10;
    public static final byte JUMP_FORWARD      = 12;
    public static final byte JUMP_BACKWARD     = 13;
    public static final byte RETURN            = 14;
    
    public static final byte INC_LOCAL         = 15;
    public static final byte DECR_LOCAL        = 16;
    public static final byte POP_OBJECT        = 17;
    
    public static String opcodeToString(int opcode) {
        Field[] fields = Opcodes.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                if (f.getInt(Opcodes.class) == opcode) {
                    return f.getName();
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Opcodes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Opcodes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
    }
}
