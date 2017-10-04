package devlang.parser.nodes;

public class NodeIdentifier extends Node {
    
    public final String value;
    
    public NodeIdentifier(String value) {
        super(NodeType.IDENTIFIER);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
    
}
