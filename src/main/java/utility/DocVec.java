package utility;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class DocVec {
	private int docId;
	private Map<String, Double> vector;
//	private double selfProduct = Double.MIN_VALUE;
	
	public DocVec(int id) {
		docId = id;
		vector = new HashMap<String, Double>();
	}
	
	public int getId() {
		return docId;
	}
	
	public Map<String, Double> getVec(){
		return vector;
	}
	
	public void add(String s) {
		if(vector.containsKey(s)) {
			vector.put(s, vector.get(s)+1);
		}else {
			vector.put(s, 1.0);
		}
	}
	
	public void add(String s, double tf) {
		vector.put(s, vector.getOrDefault(s, 0.0)+tf);
	}
	
	public void put(String s, double d) {
		vector.put(s, d);
	}
	
	public Double get(String s) {
		return vector.get(s);
	}
	
	public boolean containsKey(String s) {
		return vector.containsKey(s);
	}
	
	public Double getOrDefault(String s, double d) {
		return vector.getOrDefault(s, d);
	}
	
	public Set<String> keySet(){
		return vector.keySet();
	}
	
//	public double getSelfProduct() {
//		if(selfProduct==Double.MIN_VALUE) {
//			selfProduct = 0;
//			for(String s : vector.keySet()) {
//				double tf = vector.get(s);
//				selfProduct += tf*tf;
//			}
//		}
//		return selfProduct;
//	}
}
