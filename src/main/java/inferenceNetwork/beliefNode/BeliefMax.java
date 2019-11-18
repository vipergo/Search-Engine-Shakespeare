package inferenceNetwork.beliefNode;

import inferenceNetwork.QueryNode;

public class BeliefMax extends BeliefNode {
	public double score(int docId) {
		double max = -999999;
		for(QueryNode q : children) {
			max = Math.max(max, q.score(docId));
		}
		return max;
	}
}
