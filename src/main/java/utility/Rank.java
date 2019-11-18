package utility;

public class Rank implements Comparable<Rank>{
	public double score;
	public int docId;
	
	public Rank(double s, int d){
		score = s;
		docId = d;
	}
	
	public int compareTo(Rank b) {
		return score<b.score ? -1 : 1;
	}
}