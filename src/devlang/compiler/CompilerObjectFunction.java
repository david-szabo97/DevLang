package devlang.compiler;

public class CompilerObjectFunction extends DevObject {
    
    public CodeBuilder builder;
    public boolean hasReturn;

    public CompilerObjectFunction(CodeBuilder builder, boolean hasReturn) {
        super(DevObjectType.FUNCTION);
        this.builder = builder;
        this.hasReturn = hasReturn;
    }

    @Override
    public DevObjectBool compare(int op, DevObject obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DevObject unaryOperator(int op) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DevObject binaryOperator(int op, DevObject obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public DevObject clone() {
        return new CompilerObjectFunction(this.builder, this.hasReturn);
    }
    
}
