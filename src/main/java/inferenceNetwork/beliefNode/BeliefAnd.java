package inferenceNetwork.beliefNode;

import inferenceNetwork.QueryNode;

public class BeliefAnd extends BeliefNode {
	public double score(int docId) {
		double s = 0.0;
		for(QueryNode q : children) {
			s += q.score(docId);
		}
		return s;
	}
}
