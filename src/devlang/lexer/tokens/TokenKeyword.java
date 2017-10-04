package devlang.lexer.tokens;

public class TokenKeyword extends Token {
    
    public final String value;
    
    public TokenKeyword(String value) {
        super(TokenType.KEYWORD);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TOKEN_"+this.type.name()+": "+this.value;
    }
    
}
