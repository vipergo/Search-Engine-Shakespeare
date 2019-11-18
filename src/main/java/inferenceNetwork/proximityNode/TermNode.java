package inferenceNetwork.proximityNode;

import java.util.List;

import processor.Indexes;
import processor.scorer.Diri;

public class TermNode extends ProximityNode {
	public TermNode(String query, Indexes indexer, Diri diri) {
		super.ind = indexer;
		super.pl = indexer.getTerm(query);
		super.scorer = diri;
		super.pliterator = pl.getPLIterator();
	}
	
	public void generatePL(List<ProximityNode> children) {
		;
	}
}
