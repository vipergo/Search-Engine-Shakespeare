package processor.scorer;

import utility.*;

public class RawCount implements Scorer {
	public double computeScore(int term, int queryTermFreq, int pos, 
			int docLen, int totalDoc, int totalVoca, int numOfDoc) {
		return pos;
	}
	
//	public int g_RawCount(int queryTermFreq) {
//		return 1;
//	}
//	
//	public int f_RawCount(Postings l) {
//		return l.getCount();
//	}
}
