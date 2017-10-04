package devlang.parser.nodes;

public class NodeInteger extends Node {
    
    public final int value;
    
    public NodeInteger(int value) {
        super(NodeType.INTEGER);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
