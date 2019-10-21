package utility;

import java.util.List;
import java.util.ArrayList;

public class PostingList {
	private long offset = 0;
	private int numBytes = 0;
	private int docCount = 0;
	private int count = 0;
	private List<Postings> postings;
	
	public PostingList() {
		postings = new ArrayList<Postings>();
	}
	
	public PostingList(long off, int b, int dc, int c) {
		postings = new ArrayList<Postings>();
		offset = off;
		numBytes = b;
		docCount = dc;
		count = c;
	}
	
	public PostingList(PostingList t2) {
		offset = t2.getOffset();
		docCount = t2.getDocCount();
		count = t2.getCount();
		postings = t2.getPList();
	}
	
	public void addPos(int docId, int p){
		
		if(postings.size()==0 
				|| postings.get(postings.size()-1).getDocId()!=docId
				){
			postings.add(new Postings(docId));
			docCount++;
		}
		postings.get(postings.size()-1).add(p);
		//Postings p = postingList.get(postingList.size()-1);
		count++;
	}
	
	public int getDocCount() {
		return docCount;
	}
	
	public int getCount() {
		return count;
	}
	
	public List<Postings> getPList(){
		return postings;
	}
	
	public Postings skipToDoc(int[] pointer, int j, int docId) {
		for(;pointer[j]<postings.size(); pointer[j]++) {
			Postings p = postings.get(pointer[j]);
			if(p.getDocId()>=docId) break;
		}
		Postings res = pointer[j]>=postings.size() ? null : postings.get(pointer[j]);
		return res;
	}
	
	public void setOffset(long os) {
		offset = os;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public int getBytes() {
		return numBytes;
	}
	
	public void setBytes(int x) {
		numBytes = x;
	}
	
	public boolean isEqual(PostingList t) {
		if(offset!=t.getOffset() || docCount!=t.getDocCount() || count!=t.getCount())
			return false;
		List<Postings> tpl = t.getPList();
		if(postings.size()!=tpl.size())
			return false;
		for(int i=0; i<tpl.size(); i++) {
			if(!postings.get(i).isEqual(tpl.get(i)))
				return false;
		}
		return true;
	}
	
	public void print() {
		System.out.printf("offset: %d, docCount: %d, count: %d\n", offset, docCount, count);
		System.out.println("Pos below: ");
		for(Postings p : postings) {
			p.print();
		}
	}
}

