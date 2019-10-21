package utility;

public class RankResult {
	public double score;
	public DocStat doc;
//	public int rank;
	
	public RankResult(double s, DocStat d){
		score = s;
		doc = d;
//		rank = -1;
	}
	
//	public RankResult(double s, DocStat d, int r){
//		score = s;
//		doc = d;
//		rank = r;
//	}
}
