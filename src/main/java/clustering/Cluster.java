package clustering;

import java.util.*;
import utility.DocVec;

public class Cluster {
	private int id;
	private List<DocVec> docVecs;
	private Linkage link;
	private CosSim sim;
	private DocVec centroid;
	
	public Cluster(int id, Linkage l, CosSim cos) {
		this.id = id;
		link = l;
		sim = cos;
		docVecs = new ArrayList<DocVec>();
		centroid = new DocVec(-1);
	}
	
	public double score(DocVec dv) {
		double res = 0;
		switch(link) {
		case MIN:
			res = 1.0;
			for(DocVec a : docVecs) {
				double cur = sim.score(a, dv);
				res = Math.min(res, cur);
			}
			break;
		case MAX:
			res = 0;
			for(DocVec a : docVecs) {
				double cur = sim.score(a, dv);
				res = Math.max(res, cur);
			}
			break;
		case AVG:
			for(DocVec a : docVecs) {
				res += sim.score(a, dv);
			}
			res /= docVecs.size();
			break;
		case MEAN:
			res = sim.score(centroid, dv);
			break;
		}
		return res;
	}
	
	public void add(DocVec dv) {
		docVecs.add(dv);
		if(link==Linkage.MEAN) {
			updateCentroid(dv);
		}
	}
	
	public void updateCentroid(DocVec dv) {
		for(String s : centroid.keySet()) {
			centroid.put(s, centroid.get(s)*(docVecs.size()-1));
		}
		for(String s : dv.keySet()) {
			centroid.add(s, dv.get(s));
		}
		for(String s : centroid.keySet()) {
			centroid.put(s, centroid.get(s)/docVecs.size());
		}
	}
	
	public int getClusterId() {
		return id;
	}
	
	public List<Integer> getDocIds(){
		List<Integer> res = new ArrayList<Integer>(docVecs.size());
		for(DocVec dv : docVecs) {
			res.add(dv.getId());
		}
		return res;
	}
	
	public int totalDoc() {
		return docVecs.size();
	}
}
