package devlang.parser.nodes;

public class NodeFor extends Node {
    
    public Node condition;
    
    public NodeFor() {
        super(NodeType.FOR);
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+"";
    }
    
}
