package clustering;

import java.util.Map;

import processor.Indexes;
import utility.DocVec;

public class CosSim {
	private Indexes ind;
	
	public CosSim(Indexes ind) {
		this.ind = ind;
	}
	
	public double score(DocVec a, DocVec b) {
		double top = 0;
		for(String s : a.getVec().keySet()) {
			if(b.containsKey(s)) {
				//double idf = getIDF(s);
				top += a.get(s) * b.get(s);// * idf * idf;
			}
		}
		double bot = Math.sqrt(selfProduct(a)*selfProduct(b));
		return top/bot;
	}
	
	public double getIDF(String s) {
		int n = ind.getNumDoc();
		return Math.log((n+1)/(ind.getTDF(s)+0.5));
	}
	
	private double selfProduct(DocVec x) {
		Map<String, Double> vector = x.getVec();
		double res = 0;
		for(String s : vector.keySet()) {
			//double tfidf = vector.get(s) * getIDF(s);
			double tf = vector.get(s);
			res += tf *tf;
		}
		return res;
	}
}
