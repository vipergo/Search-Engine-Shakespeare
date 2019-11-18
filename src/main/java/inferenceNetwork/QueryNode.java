package inferenceNetwork;

import java.util.*;

public abstract class QueryNode {
	protected List<QueryNode> children = new ArrayList<QueryNode>();

    public void setChildren(List<QueryNode> children) {
        this.children = children;
    }

    public List<QueryNode> getChildren() {
        return children;
    }

    public void addChild(QueryNode q){
        children.add(q);
    }

    public abstract double score(int docID);
    public abstract boolean hasNext();
    public abstract int nextCandidate();
    public abstract boolean skipTo(int docID);
}
