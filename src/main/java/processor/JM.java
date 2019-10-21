package processor;

import utility.PostingList;
import utility.Postings;

public class JM implements Scorer {
	/*the Jelinek-Mercer smoothing*/
	double lambda = 0.15;

	public double computeScore(int tf, int queryTermFreq, int tdf, 
			int docLen, int totalDoc, int numTokens, int numOfDoc) {
		//|D| = docLen
		int docTermFreq = tdf, c_q_i = tf;
		double docScore = (1-lambda)*docTermFreq/docLen,
				colScore = lambda*c_q_i/numTokens;
		return Math.log(docScore+colScore);
	}
	
	@Override
    public String toString() { 
        return String.format("RunZhu-ql-jm-<lambda=%.2f>", lambda); 
    }

}
