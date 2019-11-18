package inferenceNetwork.filterNode;

import inferenceNetwork.QueryNode;
import inferenceNetwork.proximityNode.*;

public abstract class FilterNode extends QueryNode {
	ProximityNode filter;
	QueryNode query;
	
	public FilterNode(ProximityNode pn, QueryNode q) {
		filter = pn;
		query = q;
	}
	
	public boolean hasNext() {
		return query.hasNext();
	}
	
	public boolean skipTo(int docId) {
		return filter.skipTo(docId)&&query.skipTo(docId);
	}
}
