package devlang.parser.nodes;

public class NodeString extends Node {
    
    public final String value;
    
    public NodeString(String value) {
        super(NodeType.STRING);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
