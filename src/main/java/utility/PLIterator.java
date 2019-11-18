package utility;

import java.util.List;

public class PLIterator {
	public int docIndex = 0;
	public int posIndex = 0;
	private List<Postings> listOfPostings;
	
	PLIterator(PostingList pl){
		this.listOfPostings = pl.getPList();
	}
	
	public boolean hasNextDoc() {
		if(docIndex<listOfPostings.size()) return true;
		return false;
	}
	
	public boolean hasNextPos() {
		if(docIndex>=listOfPostings.size()) return false;
		if(posIndex>=listOfPostings.get(docIndex).getCount()) return false;
		return true;
	}
	
	public boolean skipToDoc(int docId) {
		int curDocId = getDocId();
		while(curDocId<docId) {
			if(curDocId==-1) break;
			docIndex++;
			curDocId = getDocId();
		}
		posIndex = 0;
		return curDocId==docId;
	}
	
	public int getCurTDF() {
		if(docIndex>=listOfPostings.size()) return 0;
		return listOfPostings.get(docIndex).getCount();
	}
	
	public int getDocId() {
		if(docIndex>=listOfPostings.size()) return -1;
		return listOfPostings.get(docIndex).getDocId();
	}
	
	public int getPos() {
		if(docIndex>=listOfPostings.size() || posIndex>=listOfPostings.get(docIndex).getCount()) return -1;
		return listOfPostings.get(docIndex).getPosition(posIndex);
	}
	
	public int skipToPos(int pos) {
		int curPos = getPos();
		while(curPos<pos) {
			if(curPos==-1) break;
			posIndex++;
			curPos = getPos();
		}
		return curPos;
	}
	
	public int nextDocId() {
		int res = getDocId();
		if(res==-1) return res;
		docIndex++;
		return res;
	}
	
	public int nextPos() {
		int res = getPos();
		posIndex++;
		return res;
	}
}
