package inferenceNetwork.proximityNode;

import java.util.List;

import inferenceNetwork.QueryNode;
import utility.PostingList;
import utility.PLIterator;
import processor.Indexes;
import processor.scorer.Diri;

public abstract class ProximityNode extends QueryNode {
	protected PostingList pl;
	public Indexes ind;
	public Diri scorer;
	protected PLIterator pliterator;
	
//	public ProximityNode(ProximityNode old) {
//		ind = old.ind;
//		scorer = old.scorer;
//	}
//	
//	public ProximityNode(Indexes indexer, Diri diri, String queryTerm) {
//		ind = indexer;
//		scorer = diri;
//		pl = indexer.getTerm(queryTerm);
//	}
	
	public double score(int docID) {
		int tf = pl.getCount(), dtf = nextCandidate()==docID ? pliterator.getCurTDF() : 0,
				docLen = ind.getDocLen(docID), totalColl = (int)ind.getNumTokens();
		return scorer.score(tf, dtf, docLen, totalColl);
	}
    public int nextCandidate(){
    	return pliterator.getDocId();
    }
    
    public boolean hasNext() {
    	return pliterator.hasNextDoc();
    }
    
    public boolean skipTo(int docID) {
    	
    	return pliterator.skipToDoc(docID);
    }
    
    public int getPos() {
    	return pliterator.getPos();
    }
    
    public int skipToPos(int pos) {
    	return pliterator.skipToPos(pos);
    }
    
    public int nextPos() {
    	return pliterator.nextPos();
    }
}
