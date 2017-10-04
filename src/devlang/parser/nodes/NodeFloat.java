
package devlang.parser.nodes;

public class NodeFloat extends Node {
    
    public final double value;
    
    public NodeFloat(double value) {
        super(NodeType.FLOAT);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "NODE_"+this.type.name()+": "+this.value;
    }
}
