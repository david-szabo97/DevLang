package devlang.lexer.tokens;

public class TokenInteger extends Token {
    
    public final int value;
    
    public TokenInteger(int value) {
        super(TokenType.INTEGER);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TOKEN_"+this.type.name()+": "+this.value;
    }
    
}
