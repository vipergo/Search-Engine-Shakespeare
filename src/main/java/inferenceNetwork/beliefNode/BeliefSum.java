package inferenceNetwork.beliefNode;

import inferenceNetwork.QueryNode;

public class BeliefSum extends BeliefNode {
	public double score(int docID) {
        double scores = 0;
        for(QueryNode q: children){
            scores+=Math.exp(q.score(docID));
        }
        return Math.log(scores/children.size());
    }
}
