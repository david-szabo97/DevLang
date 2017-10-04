package devlang.parser;

import devlang.lexer.Lexer;
import devlang.lexer.tokens.Token;
import devlang.lexer.tokens.TokenFloat;
import devlang.lexer.tokens.TokenIdentifier;
import devlang.lexer.tokens.TokenKeyword;
import devlang.lexer.tokens.TokenInteger;
import devlang.lexer.tokens.TokenOperator;
import devlang.lexer.tokens.TokenString;
import devlang.lexer.tokens.TokenType;
import devlang.parser.nodes.Node;
import devlang.parser.nodes.NodeBool;
import devlang.parser.nodes.NodeBranch;
import devlang.parser.nodes.NodeCall;
import devlang.parser.nodes.NodeCondition;
import devlang.parser.nodes.NodeFloat;
import devlang.parser.nodes.NodeFor;
import devlang.parser.nodes.NodeFunction;
import devlang.parser.nodes.NodeIdentifier;
import devlang.parser.nodes.NodeInteger;
import devlang.parser.nodes.NodeOperator;
import devlang.parser.nodes.NodeReturn;
import devlang.parser.nodes.NodeString;
import devlang.parser.nodes.NodeType;
import devlang.parser.nodes.NodeUnary;
import devlang.parser.nodes.NodeWhile;
import java.util.Arrays;


/*
if (expr op expr op expr op expr op expr) {} else {}

+ -
* /
(prefix)- + -- ++
változok () függvény meghívások = += -= *= /= |= &=
(affix) ++ --
&& ||
*/

public class Parser {
    
    private final Lexer lex;
    
    private boolean foundReturn = false;
    
    public Parser(Lexer lex) {
        this.lex = lex;
    }
    
    private boolean isOperator(Token tok, String val) {
        return tok.type == TokenType.OPERATOR && val.equals(((TokenOperator)tok).value);
    }
    
    public Node parse() throws Exception {
        Node program = new Node();
        Node left = program;
        
        Token tok = this.lex.peek();
        if (tok.type == TokenType.END) {
            return null;
        }
        
        while (true) {
            tok = this.lex.peek();
            if (tok.type == TokenType.END) {
                break;
            }
        
            Node n = new Node();
            n.left = this.expression();
            
            left.left = n;
            left = left.right = new Node();
            
        }
        
        return program;
    }
    
    private Node expression() throws Exception {
        //System.out.println("expression");
        return this.statementExpression();
    }
    
    private Node statementExpression() throws Exception {
        Token tok = this.lex.peek();
        if (tok.type == TokenType.KEYWORD) {
            String kw = ((TokenKeyword)tok).value;
            if (kw.equals("if")) {
                this.lex.skip();
                tok = this.lex.eat();
                if (!isOperator(tok, "(")) {
                    throw new Exception("Left paranthesis expected.");
                }
                Node condition = this.conditionExpression();
                tok = this.lex.eat();
                if (!isOperator(tok, ")")) {
                    throw new Exception("Right paranthesis expected.");
                }
                tok = this.lex.eat();
                if (!isOperator(tok, "{")) {
                    throw new Exception("Left curly bracket expected.");
                }
                Node stmtThen = this.expression();
                Node stmtElse = null;
                tok = this.lex.eat();
                if (!isOperator(tok, "}")) {
                    throw new Exception("Right curly bracket expected.");
                }
                tok = this.lex.peek();
                if (tok.type == TokenType.KEYWORD && "else".equals(((TokenKeyword)tok).value)) {
                    this.lex.skip();
                    tok = this.lex.eat();
                    if (!isOperator(tok, "{")) {
                        throw new Exception("Left curly bracket expected.");
                    }
                    stmtElse = this.expression();
                    tok = this.lex.eat();
                    if (!isOperator(tok, "}")) {
                        throw new Exception("Right curly bracket expected.");
                    }
                }
                
                NodeBranch node = new NodeBranch();
                node.condition = condition;
                node.left = stmtThen;
                node.right = stmtElse;
                return node;
            } else if (kw.equals("function")) {
                foundReturn = false;
                this.lex.skip();
                tok = this.lex.eat();
                if (tok.type != TokenType.IDENTIFIER) {
                    throw new Exception("Function name expected.");
                }
                TokenIdentifier tokId = (TokenIdentifier)tok;
                tok = this.lex.eat();
                if (!isOperator(tok, "(")) {
                    throw new Exception("Left paranthesis expected.");
                }
                String[] args = new String[16];
                int argsRead = 0;
                tok = this.lex.peek();
                while (tok.type == TokenType.IDENTIFIER) {
                    this.lex.skip();
                    args[argsRead++] = ((TokenIdentifier)tok).value;
                    tok = this.lex.peek();
                    if (!isOperator(tok, ",")) {
                        break;
                    } else {
                        this.lex.skip();
                        tok = this.lex.peek();
                    }
                }
                tok = this.lex.eat();
                if (!isOperator(tok, ")")) {
                    throw new Exception("Right paranthesis expected.");
                }
                tok = this.lex.eat();
                if (!isOperator(tok, "{")) {
                    throw new Exception("Left curly bracket expected.");
                }
                NodeFunction nodeFunc = new NodeFunction(tokId.value, Arrays.copyOf(args, argsRead, String[].class));
                Node left = nodeFunc;
                tok = this.lex.peek();
                while (!isOperator(tok, "}")) {
                    Node n = new Node();
                    n.left = this.expression();

                    left.left = n;
                    left = left.right = new Node();
                    tok = this.lex.peek();
                }
                this.lex.skip();
                
                nodeFunc.hasReturn = foundReturn;
                return nodeFunc;
            } else if (kw.equals("return")) {
                this.lex.skip();
                NodeReturn node = new NodeReturn();
                node.left = this.expression();
                foundReturn = true;
                return node;
            } else if (kw.equals("while")) {
                this.lex.skip();
                tok = this.lex.eat();
                if (!isOperator(tok, "(")) {
                    throw new Exception("Left paranthesis expected.");
                }
                Node condition = this.conditionExpression();
                tok = this.lex.eat();
                if (!isOperator(tok, ")")) {
                    throw new Exception("Right paranthesis expected.");
                }
                tok = this.lex.eat();
                if (!isOperator(tok, "{")) {
                    throw new Exception("Left curly bracket expected.");
                }
                
                tok = this.lex.peek();
                
                NodeWhile node = new NodeWhile();
                node.condition = condition;

                Node left = node;
                while (!isOperator(tok, "}")) {
                    Node n = new Node();
                    n.left = this.expression();

                    left.left = n;
                    left = left.right = new Node();
                    tok = this.lex.peek();
                }
                this.lex.skip();
                return node;
            } else if (kw.equals("for")) {
                NodeFor node = new NodeFor();
                this.lex.skip();
                tok = this.lex.eat();
                if (!isOperator(tok, "(")) {
                    throw new Exception("Left paranthesis expected.");
                }
                
                // Initialization
                tok = this.lex.peek();
                Node left = node;
                while (!isOperator(tok, ";")) {
                    Node n = new Node();
                    n.left = this.expression();

                    left.left = n;
                    left = left.right = new Node();
                    tok = this.lex.peek();
                }
                this.lex.skip();
                
                // Condition
                Node condition = this.conditionExpression();
                node.condition = condition;
                tok = this.lex.eat();
                if (!isOperator(tok, ";")) {
                    throw new Exception("; expected.");
                }
                
                // After loop
                tok = this.lex.peek();
                Node afterLoop = new Node();
                left = afterLoop;
                while (!isOperator(tok, ")")) {
                    Node n = new Node();
                    n.left = this.expression();

                    left.left = n;
                    left = left.right = new Node();
                    tok = this.lex.peek();
                }
                this.lex.skip();
                
                tok = this.lex.eat();
                if (!isOperator(tok, "{")) {
                    throw new Exception("Left curly bracket expected.");
                }
                
                tok = this.lex.peek();
                left = node.right;
                while (!isOperator(tok, "}")) {
                    Node n = new Node();
                    n.left = this.expression();

                    left.left = n;
                    left = left.right = new Node();
                    tok = this.lex.peek();
                }
                this.lex.skip();
                left.right = afterLoop;
                
                return node;
            }
        }
        return this.conditionExpression();
    }
    
    private Node conditionExpression() throws Exception {
        //System.out.println("conditionExpression");
        Node left = this.addExpression();
        Token tok = this.lex.peek();
        //System.out.println(tok);
        while (tok.type == TokenType.OPERATOR) {
            TokenOperator tokOp = (TokenOperator)tok;
            if ("&&||><".contains(tokOp.value) || ">=".equals(tokOp.value) || "!=".equals(tokOp.value) || "<=".equals(tokOp.value) || "==".equals(tokOp.value)) {
                // Skip this operator
                this.lex.skip();
                NodeCondition node = new NodeCondition(tokOp.value);
                node.left = left;
                node.right = this.addExpression();
                left = node;
                tok = this.lex.peek();
            } else {
                break;
            }
        }
        return left;
    }

    private Node addExpression() throws Exception {
        //System.out.println("addExpression");
        Node left = this.mulExpression();
        Token tok = this.lex.peek();
        while (tok.type == TokenType.OPERATOR) {
            TokenOperator tokOp = (TokenOperator)tok;
            if ("+".equals(tokOp.value) || "-".equals(tokOp.value)) {
                // Skip this operator
                this.lex.skip();
                NodeOperator node = new NodeOperator(tokOp.value);
                node.left = left;
                node.right = this.mulExpression();
                left = node;
                tok = this.lex.peek();
            } else {
                break;
            }
        }
        return left;
    }
    
    private Node mulExpression() throws Exception {
        //System.out.println("mulExpression");
        Node left = this.minusExpression();
        Token tok = this.lex.peek();
        while (tok.type == TokenType.OPERATOR) {
            TokenOperator tokOp = (TokenOperator)tok;
            if ("*".equals(tokOp.value) || "/".equals(tokOp.value) || "%".equals(tokOp.value)) {
                // Skip this operator
                this.lex.skip();
                NodeOperator node = new NodeOperator(tokOp.value);
                node.left = left;
                node.right = this.minusExpression();
                left = node;
                tok = this.lex.peek();
            } else {
                break;
            }
        }
        return left;
    }
    
    private Node minusExpression() throws Exception {
        //System.out.println("minusExpression");
        Token tok = this.lex.peek();
        
        if (tok.type == TokenType.OPERATOR && "--++".contains(((TokenOperator)tok).value)) {
            // Skip this operator
            this.lex.skip();
            NodeUnary node = new NodeUnary(((TokenOperator)tok).value);
            node.left = this.primaryExpression();
            return node;
        }
        
        return this.primaryExpression();
    }
    
    private Node primaryExpression() throws Exception {
        //System.out.println("primaryExpression");
        Token tok = this.lex.peek();
        //System.out.println(tok);
        if (tok.type == TokenType.STRING) {
            // Skip this number
            this.lex.skip();
            TokenString tokStr = (TokenString)tok;
            NodeString node = new NodeString(tokStr.value);
            return node;
        } else if (tok.type == TokenType.INTEGER) {
            // Skip this number
            this.lex.skip();
            TokenInteger tokNum = (TokenInteger)tok;
            NodeInteger node = new NodeInteger(tokNum.value);
            return node;
        } else if (tok.type == TokenType.FLOAT) {
            // Skip this number
            this.lex.skip();
            TokenFloat tokNum = (TokenFloat)tok;
            NodeFloat node = new NodeFloat(tokNum.value);
            return node;
        } else if (tok.type == TokenType.KEYWORD) {
            // Skip this number
            this.lex.skip();
            TokenKeyword tokKw = (TokenKeyword)tok;
            if (tokKw.value.equals("true")) {
                NodeBool node = new NodeBool(true);
                return node;
            }
            if (tokKw.value.equals("false")) {
                NodeBool node = new NodeBool(false);
                return node;
            }
            
        } else if (tok.type == TokenType.OPERATOR) {
            TokenOperator tokOp = (TokenOperator)tok;
            if ("(".equals(tokOp.value)) {
                // Skip this operator
                this.lex.skip();
                Node node = this.expression();
                // Eat closing paranthesis
                tok = this.lex.eat();
                if (tok.type != TokenType.OPERATOR || !")".equals(((TokenOperator)tok).value)) {
                    throw new Exception("Expecting paranthesis.");
                }
                return node;
            }
        } else if (tok.type == TokenType.IDENTIFIER) {
            //System.out.println("Found identifier");
            // Skip this identifier
            this.lex.skip();
            TokenIdentifier tokId = (TokenIdentifier)tok;
            NodeIdentifier nodeId = new NodeIdentifier(tokId.value);
            
            tok = this.lex.peek();
            // Assign value
            String assignOps = "+= -= /= *= = &= |=";
            if (tok.type == TokenType.OPERATOR && assignOps.contains(((TokenOperator)tok).value)) {
                //System.out.println("Found a variable assignment");
                Node node = nodeId;
                boolean recvValue = false;
                
                while (tok.type == TokenType.OPERATOR && assignOps.contains(((TokenOperator)tok).value)) {
                    // //System.out.println("Left hand assignemnt");
                    // Skip this operator
                    this.lex.skip();
                    NodeOperator nodeOp = new NodeOperator(((TokenOperator)tok).value);
                    nodeOp.left = node;
                    nodeOp.right = this.expression();
                    node = nodeOp;
                    if (node.right.type != NodeType.IDENTIFIER) {
                        if (recvValue) {
                            throw new Exception("Expected literal.");
                        }
                        recvValue = true;
                    }
                    tok = this.lex.peek();
                }
                
                return node;
            }
            
            // Call
            if (tok.type == TokenType.OPERATOR && "(".equals(((TokenOperator)tok).value)) {
                // Skip this operator
                this.lex.skip();
                NodeCall nodeCall = new NodeCall();
                nodeCall.left = nodeId;
                nodeCall.right = null;
                tok = this.lex.peek();
                if (!isOperator(tok, ")")) {
                    int argsRead = 0;
                    nodeCall.args = new Node[16];
                    while (true) {
                        nodeCall.args[argsRead++] = this.expression();
                        
                        tok = this.lex.peek();
                        if (!isOperator(tok, ",")) {
                            break;
                        } else {
                            this.lex.skip();
                        }
                    }
                    Node[] args = nodeCall.args;
                    nodeCall.args = new Node[argsRead];
                    for (int i = 0; i < argsRead;i++) {
                        nodeCall.args[i] = args[i];
                    }
                }
                // Eat closing paranthesis
                tok = this.lex.eat();
                if (tok.type != TokenType.OPERATOR || !")".equals(((TokenOperator)tok).value)) {
                    throw new Exception("Expecting paranthesis.");
                }
                return nodeCall;
            }
            
            String unaryOps = "++ --";
            if (tok.type == TokenType.OPERATOR && unaryOps.contains(((TokenOperator)tok).value)) {
                // Skip this operator
                this.lex.skip();
                NodeUnary node = new NodeUnary(((TokenOperator)tok).value);
                node.left = nodeId;
                return node;
            }
            return nodeId;
        }
        
        throw new Exception("Unexpected token: "+tok);
    }
    
}