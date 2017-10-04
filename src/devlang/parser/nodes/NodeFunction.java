package devlang.parser.nodes;

public class NodeFunction extends Node {
    
    public final String name;
    public final String[] args;
    public boolean hasReturn;
    
    public NodeFunction(String name, String[] args) {
        super(NodeType.FUNCTION);
        this.name = name;
        this.args = args;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.name+"";
    }
}
