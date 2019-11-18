package inferenceNetwork;

import utility.Rank;
import java.util.*;

public class InferenceNetwork {
//	public InferenceNetwork() {
//		
//	}
	public List<Rank> runQuery(QueryNode q , int k){
        PriorityQueue<Rank> R = new PriorityQueue<Rank>(k+1);
        while(q.hasNext()){
            int d = q.nextCandidate();
            q.skipTo(d);
            Double score = q.score(d);
            q.skipTo(d+1);
            R.add(new Rank(score, d));
            if(R.size()>k){
                R.poll();
            }
        }

        List<Rank> output = new ArrayList<Rank>(k);
        while (!R.isEmpty()){
            output.add(R.poll());
        }
        Collections.reverse(output);
        return output;
    }
}
