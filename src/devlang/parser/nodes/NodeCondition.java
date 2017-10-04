package devlang.parser.nodes;

public class NodeCondition extends Node {
    
    public final String value;
    
    public NodeCondition(String value) {
        super(NodeType.CONDITION);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
