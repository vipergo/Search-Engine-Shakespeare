package inferenceNetwork.beliefNode;

import inferenceNetwork.QueryNode;

public abstract class BeliefNode extends QueryNode{
	public int nextCandidate() {
		int min = Integer.MAX_VALUE;
		for(QueryNode q : children) {
			if(q.hasNext()) {
				min = Math.min(min, q.nextCandidate());
			}
		}
		
		return min;
	}
	
	public boolean hasNext() {
		for(QueryNode q : children) {
			if(q.hasNext()) return true;
		}
		return false;
	}
	
    public boolean skipTo(int docID) {
    	boolean res = true;
        for(QueryNode q: super.children){
            res = q.skipTo(docID) && res;
        }
        return res;
    }
}
