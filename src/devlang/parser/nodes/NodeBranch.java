package devlang.parser.nodes;

public class NodeBranch extends Node {
    
    public Node condition;
    
    public NodeBranch() {
        super(NodeType.BRANCH);
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+"";
    }
    
}
