package devlang.lexer.tokens;

public class TokenIdentifier extends Token {
    
    public final String value;
    
    public TokenIdentifier(String value) {
        super(TokenType.IDENTIFIER);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TOKEN_"+this.type.name()+": "+this.value;
    }
    
}
