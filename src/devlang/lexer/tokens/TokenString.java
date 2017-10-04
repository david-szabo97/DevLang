package devlang.lexer.tokens;

public class TokenString extends Token {
    
    public final String value;
    
    public TokenString(String value) {
        super(TokenType.STRING);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TOKEN_"+this.type.name()+": "+this.value;
    }
    
}
