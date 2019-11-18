package processor.scorer;


public class Diri implements Scorer {
	int mu;
	
	public Diri(){
		mu = 1500;
	}
	
	public Diri(int mu){
		this.mu = mu;
	}
	
	public double computeScore(int tf, int queryTermFreq, int docTermFreq, 
			int docLen, int totalDoc, int numTokens, int numOfDoc)
	{
		int c_q_i = tf;
		double top = docTermFreq + (double)mu*c_q_i/numTokens,
				down = docLen+mu;
		return Math.log(top/down);
	}
	
	public double score(int tf, int dtf, int docLen, int totalCollection) {
		double top = dtf + (double)mu*tf/totalCollection,
				down = docLen+mu;
		return Math.log(top/down);
	}
	
	@Override
    public String toString() { 
        return String.format("RunZhu-ql-dir-<mu=%d>", mu); 
    }
}
