package processor;

public interface Scorer {
	public double computeScore(int tf, int queryTermFreq, int docTermFreq, 
			int docLen, int totalDoc, int numTokens, int numOfDoc);
}
