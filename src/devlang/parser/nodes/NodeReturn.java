package devlang.parser.nodes;

public class NodeReturn extends Node {
        
    public NodeReturn() {
        super(NodeType.RETURN);
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+"";
    }
}
