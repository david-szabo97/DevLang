package devlang.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DevObjectJavaBinding extends DevObject {

    private final Object obj;
    private final String methodName;
    private final int argsCount;
   
    private Method method = null;
    
    public DevObjectJavaBinding(Object obj, String methodName, int args) {
        super(DevObjectType.BINDING);
        this.obj = obj;
        this.methodName = methodName;
        this.argsCount = args;
        
        try {
            method = obj.getClass().getMethod(methodName, Object.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(DevObjectJavaBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public Object call(Object... args) {
        Object ret = null;

        try {
            ret = method.invoke(obj, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DevObjectJavaBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (ret instanceof Integer) {
            return new DevObjectInteger((Integer)ret);
        }
        
        return DevObject.NULL;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        return DevObjectBool.FALSE;
    }

    @Override
    public DevObject unaryOperator(int op) {
        return DevObject.NULL;
    }

    @Override
    public DevObject binaryOperator(int op, DevObject obj) {
        return DevObject.NULL;
    }
    
    @Override
    public DevObject clone() {
        return new DevObjectJavaBinding(this.obj, this.methodName, this.argsCount);
    }
}
