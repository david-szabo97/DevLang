package devlang.lexer.tokens;

public class TokenOperator extends Token {
    
    public final String value;
    
    public TokenOperator(String value) {
        super(TokenType.OPERATOR);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TOKEN_"+this.type.name()+": "+this.value;
    }
    
}
