package devlang.parser.nodes;

public class NodeWhile extends Node {
    
    public Node condition;
    
    public NodeWhile() {
        super(NodeType.WHILE);
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+"";
    }
    
}
