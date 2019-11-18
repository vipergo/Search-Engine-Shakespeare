package inferenceNetwork.beliefNode;

import inferenceNetwork.QueryNode;

public class BeliefOr extends BeliefNode {
	public double score(int docId) {
        double score = 0;
        for(QueryNode q: children){
            double s = q.score(docId);
            double p = Math.exp(s);
            score+=Math.log(1-p);
        }
        return Math.log(1-Math.exp(score));
    }
}
