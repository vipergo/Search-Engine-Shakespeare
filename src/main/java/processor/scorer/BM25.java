package processor.scorer;

public class BM25 implements Scorer {
	public double k1 = 1.2;
	public double k2 = 200.0; //k2 (0,1000)
	public double avdl = 1199.55615;
	public double b = 0.75;

	public double computeScore(int tf, int queryTermFreq, int f_i, int docLen, int totalDoc, int numTokens, int numOfDoc) {
		double K = k1*((1-b)+b*docLen/avdl);
		//System.out.printf(" K: %.2f \n", K);
		double idf = computeIdf(numOfDoc, totalDoc);
		double docPart = docPartScore(k1, K, f_i);
		double queryPart = queryPartScore(k2, queryTermFreq);
		double res = idf*docPart*queryPart;
		//System.out.printf("idf: %.3f, docPart: %.3f, queryPart: %.3f, res=%.3f\n", idf, docPart, queryPart, res);
		return res;
	}
	
	public double computeIdf(int tf, int totalDoc) {
		int n_i = tf;
		//System.out.printf(" n_i: %d \n", n_i);
		return Math.log((totalDoc-n_i+0.5)/(n_i+0.5));
	}
	
	public double docPartScore(double k1, double K, int f_i) {
		//int f_i = pos.getCount();
		//System.out.printf(" f_i: %d \n", f_i);
		return (k1+1)*f_i/(K+f_i);
	}
	
	public double queryPartScore(double k2, int queryTermFreq) {
		return (k2+1)*queryTermFreq/(k2+queryTermFreq);
	}
	
	@Override
    public String toString() { 
        return String.format("RunZhu-bm25-<k1=%.2f>-<k2=%.2f>-<b=%.2f>", k1, k2, b); 
    }
}
