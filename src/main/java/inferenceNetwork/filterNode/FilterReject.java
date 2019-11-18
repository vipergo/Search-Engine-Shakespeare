package inferenceNetwork.filterNode;

import inferenceNetwork.QueryNode;
import inferenceNetwork.proximityNode.ProximityNode;

public class FilterReject extends FilterNode {
	public FilterReject(ProximityNode pn, QueryNode q) {
		super(pn, q);
	}
	
	@Override
	public int nextCandidate() {
		return query.nextCandidate();
	}
	
	public double score(int docId) {
		double res = 0;
		if(filter.nextCandidate()!=docId) {
			res = query.score(docId);
			filter.skipTo(docId+1);
		}
		return res;
	}
}
