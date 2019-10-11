package utility;

import java.util.List;
import java.util.ArrayList;

public class Postings {
	private int docId;
	private List<Integer> pos;
	
	public Postings(int d) {
		docId = d;
		pos = new ArrayList<Integer>();
	}
	
//	public void deltaEncodePos() {
//		int last = 0;
//		for(int i=0; i<pos.size(); i++) {
//			int temp = pos.get(i);
//			pos.set(i, temp-last);
//			last = temp;
//		}
//	}
	
	public List<Integer> getPos(){
		return pos;
	}
	
	public void add(int p) {
		pos.add(p);
	}
	
	public int getDocId() {
		return docId;
	}
	
	public void setDocId(int d) {
		docId = d;
	}
	
	public int getCount() {
		return pos.size();
	}
	
	public boolean isEqual(Postings p) {
		if(docId!=p.getDocId())
			return false;
		List<Integer> pp = p.getPos();
		if(pos.size()!=pp.size())
			return false;
		for(int i=0; i<pp.size(); i++) {
			int x = pos.get(i), y = pp.get(i);
			if(x!=y){
				System.out.printf("%d: %d vs %d\n", docId, x, y);
				return false;
			}
		}
		return true;
	}
	
	public void print() {
		System.out.printf("docId/seneNum: %d\n", docId);
		System.out.println(pos.toString());
	}
}

