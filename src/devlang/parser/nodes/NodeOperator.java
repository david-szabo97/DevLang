package devlang.parser.nodes;

public class NodeOperator extends Node {
    
    public final String value;
    
    public NodeOperator(String value) {
        super(NodeType.OPERATOR);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
