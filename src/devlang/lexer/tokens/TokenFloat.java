package devlang.lexer.tokens;

public class TokenFloat extends Token {
    
    public final double value;
    
    public TokenFloat(double value) {
        super(TokenType.FLOAT);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TOKEN_"+this.type.name()+": "+this.value;
    }
    
}
