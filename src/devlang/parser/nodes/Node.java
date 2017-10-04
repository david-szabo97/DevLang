package devlang.parser.nodes;

public class Node {
    
    public final NodeType type;
    
    public Node left;
    public Node right;
    
    public Node() {
        this(NodeType.NODE);
    }
    
    public Node(NodeType type) {
        this.type = type;
        this.left = null;
        this.right = null;
    }
    
    @Override
    public String toString() {
        return "NODE: "+this.type.name();
    }
    
}
