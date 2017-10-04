package devlang.parser.nodes;

public class NodeCall extends Node {
    
    public Node[] args = null;
    
    public NodeCall() {
        super(NodeType.CALL);
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+"";
    }
    
}
