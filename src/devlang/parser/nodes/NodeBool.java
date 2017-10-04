package devlang.parser.nodes;

public class NodeBool extends Node {
    
    public final boolean value;
    
    public NodeBool(boolean value) {
        super(NodeType.BOOL);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
