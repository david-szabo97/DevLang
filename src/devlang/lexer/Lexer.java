package devlang.lexer;

import devlang.lexer.tokens.Token;
import devlang.lexer.tokens.TokenFloat;
import devlang.lexer.tokens.TokenIdentifier;
import devlang.lexer.tokens.TokenKeyword;
import devlang.lexer.tokens.TokenInteger;
import devlang.lexer.tokens.TokenOperator;
import devlang.lexer.tokens.TokenString;
import devlang.lexer.tokens.TokenType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lexer {

    private final InputStream stream;
    private final Scanner scanner;

    public Token current;

    public Lexer(InputStream is) {
        this.stream = is;
        this.scanner = new Scanner(this.stream);

        this.current = null;
    }

    public Token eat() {
        Token tok = this.peek();
        this.skip();
        return tok;
    }
    
    public Token peek() {
        if (this.current == null) {
            this.current = this.next();
        }

        return this.current;
    }

    public void skip() {
        if (this.current == null) {
            this.next();
        }

        this.current = null;
    }
    
    public Token next() {
        Token tok = new Token(TokenType.END);
        
        String operators = "+-*/()=&|{}[]<>,;%!";
        String assignOps = "+= -= /= *= = &= |= ++ --";
        String conditionalOps = "&& || >= == <= !=";
        String numbers = "0123456789.";
        String identifierChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPRQSTUVWXYZ_";
        String stringChars = "\"'";
        
        String keywords = "if for while do true false else function return";
        String[] keywordsArr = keywords.split(" ");
        
        try {
            char c = ' ';
            while (tok.type != TokenType.END || this.stream.available() > 0) {
                c = (char) this.stream.read();
                // Skip whitespace
                if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                    continue;
                }

                // Operators
                if (operators.indexOf(c) != -1) {
                    this.stream.mark(64);
                    char c2 = (char) this.stream.read();
                    String op = c+"";
                    if (c2 != ' ') {
                        op = c+""+c2;
                        if (!assignOps.contains(op) && !conditionalOps.contains(op)) {
                            this.stream.reset();
                            op = c+"";
                        }
                    }
                    
                    return new TokenOperator(op);
                }

                // Numbers
                if (numbers.indexOf(c) != -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    this.stream.mark(1024);
                    int numsRead = 0;
                    while (true) {
                        char c2 = (char) this.stream.read();

                        if (numbers.indexOf(c2) != -1) {
                            numsRead++;
                            sb.append(c2);
                        } else {
                            break;
                        }
                    }
                    this.stream.reset();
                    this.stream.skip(numsRead);
                    String n = sb.toString();
                    if (n.contains(".")) {
                        double num = Double.parseDouble(n);
                        return new TokenFloat(num);
                    }
                    
                    int num = Integer.parseInt(n);
                    return new TokenInteger(num);
                }

                if (keywords.indexOf(c) != -1) {
                    String stopChars = " (￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿)";
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    this.stream.mark(1024);
                    int charsRead = 0;
                    while (this.stream.available() > 0) {
                        char c2 = (char) this.stream.read();

                        if (stopChars.indexOf(c2) == -1) {
                            charsRead++;
                            sb.append(c2);
                        } else {
                            break;
                        }
                    }
                    this.stream.reset();
                    String kw = sb.toString();
                    if (keywords.contains(kw)) {
                        for (String kww : keywordsArr) {
                            if (kww.equalsIgnoreCase(kw)) {
                                this.stream.skip(charsRead);
                                return new TokenKeyword(sb.toString());
                            }
                        }
                    }
                }
                
                // Identifiers
                if (identifierChars.indexOf(c) != -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    this.stream.mark(1024);
                    int charsRead = 0;
                    while (this.stream.available() > 0) {
                        char c2 = (char) this.stream.read();

                        if (identifierChars.indexOf(c2) != -1 || numbers.indexOf(c2) != -1) {
                            charsRead++;
                            sb.append(c2);
                        } else {
                            break;
                        }
                    }
                    this.stream.reset();
                    this.stream.skip(charsRead);
                    return new TokenIdentifier(sb.toString());
                }
                
                if (stringChars.indexOf(c) != -1) {
                    char startCharacter = c;
                    StringBuilder sb = new StringBuilder();
                    this.stream.mark(1024*1024);
                    int charsRead = 0;
                    while (true) {
                        char c2 = (char) this.stream.read();

                        if (c2 != startCharacter) {
                            charsRead++;
                            sb.append(c2);
                        } else {
                            break;
                        }
                    }
                    this.stream.reset();
                    this.stream.skip(charsRead + 1);
                    return new TokenString(sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tok;
    }

}
