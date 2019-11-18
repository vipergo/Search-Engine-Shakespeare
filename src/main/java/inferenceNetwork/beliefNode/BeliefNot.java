package inferenceNetwork.beliefNode;

public class BeliefNot extends BeliefNode {
	public double score(int docId) {
		double s = children.get(0).score(docId);
		return 1-s;
	}
}
