package inferenceNetwork.beliefNode;

import java.util.List;

public class BeliefWeightedSum extends BeliefNode {
	protected List<Double> weights;

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> w){
        weights= w;
    }

    public double score(int docID) {
    	assert children.size()==weights.size();
        
    	double scores = 0;
        for(int i=0; i<children.size(); i++) {
        	scores+=weights.get(i)*children.get(i).score(docID);
        }
        return scores;
    }
}
