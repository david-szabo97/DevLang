package devlang.parser.nodes;

public class NodeUnary extends Node {
    
    public final String value;
    
    public NodeUnary(String value) {
        super(NodeType.UNARY);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
