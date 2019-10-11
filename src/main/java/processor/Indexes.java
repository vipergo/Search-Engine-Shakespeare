package processor;

import java.util.*;
import java.io.IOException;
import java.io.RandomAccessFile;

import utility.*;

public class Indexes {
	private Map<String, Term> lookupTable;
	private Map<String, List<Integer>> playId2DocId;
	private Map<Integer, DocStat> docId2DocStat;
	
	private VByte vb;
	private int vocabulary;
	private int numDoc;
	private long numTokens;
	private Scorer scorer;
	
	public Indexes(JsonParser jp, VByte vb) {
		scorer = new Scorer();
		vocabulary = 0;
		numDoc = 0;
		numTokens = 0;
		this.vb = vb;
		jp.readLookupTable(this);
		playId2DocId = jp.readPlayId2DocId();
		docId2DocStat = jp.readDocId2DocStat();
		forHW1Report();
	}
	
	public void setLookupTable(Map<String, Term> lt) {
		lookupTable = lt;
	}
	
	public Map<String, Term> getLookupTable(){
		return lookupTable;
	}
	
	public void forHW1Report() {
		long sceneLenSum = 0;
		int shortestScene = Integer.MAX_VALUE, shortestPlay = Integer.MAX_VALUE, longesPlay = 0;
		String shortScene = "", shortPlay = "", longPlay = "";
		for(Map.Entry<String, List<Integer>> entry : playId2DocId.entrySet()) {
			int curPlay = 0;
			for(int docId : entry.getValue()) {
				DocStat doc = docId2DocStat.get(docId);
				int len = doc.getLength();
				if(len<shortestScene) {
					shortestScene = len;
					shortScene = doc.getSceneId();
				}
				curPlay+=len;
			}
			sceneLenSum += curPlay;
			if(curPlay<shortestPlay) {
				shortestPlay = curPlay;
				shortPlay = entry.getKey();
			}
			if(curPlay>longesPlay) {
				longesPlay = curPlay;
				longPlay = entry.getKey();
			}
		}
		System.out.printf("average length of a scene is: %d \n", sceneLenSum/numDoc);
		System.out.printf("shortest scene is %s, with length %d \n", shortScene, shortestScene);
		System.out.printf("longest play is %s, with length %d \n", longPlay, longesPlay);
		System.out.printf("shortest play is %s, with length %d \n", shortPlay, shortestPlay);
	}
	
	public Term getTerm(String q) {
		Term res = null;
		Term t = lookupTable.get(q);
		
		boolean encode = vb.isEncode();
		
		if(t==null) {
			System.out.println("query string is not in the vocabulary");
			return null;
		}
		try {
			String filename = encode ? "data/binaryFileVByte" : "data/binaryFile";
			RandomAccessFile disk = new RandomAccessFile(filename, "r");
			
			res = new Term(t);
			disk.seek(t.getOffset());
			byte[] data = new byte[t.getBytes()];
			//System.out.println(data.length);
			disk.read(data);

			int docNum = t.getDocCount();
			int[] i = new int[1];
			int deltaDocId = 0;
			while(docNum>0) {
				int len = vb.decoder(i, data);
				//System.out.println(len);
				int docId = vb.decoder(i, data)+deltaDocId;
				if(encode) deltaDocId = docId;
				//System.out.println(docId);
				Postings p = new Postings(docId);
				int deltaX = 0;
				while(len>0) {
					int x = vb.decoder(i, data)+deltaX;
					if(encode) deltaX = x;
					p.add(x);
					len--;
				}
				res.getPList().add(p);
				docNum--;
			}
			disk.close();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public List<Integer> query(String[] queries, int k){
		//String[] queries = Q.split("\\s+");
		List<Integer> res = new ArrayList<Integer>(k);
		List<Term> terms = new ArrayList<Term>(queries.length);
		for(String s : queries) {
			terms.add(getTerm(s));
		}
		PriorityQueue<int[]> pq = new PriorityQueue<int[]>(k+1, new Comparator<int[]>() {
			public int compare(int[] a, int[] b) {
				return a[1]-b[1];
			}
		});
		int[] pointers = new int[queries.length];
		
		for(int i=1; i<numDoc; i++) {
			boolean finished = true;
			int score = 0;
			for(int j=0; j<pointers.length; j++) {
				Postings cur = terms.get(j).skipToDoc(pointers, j, i);
				if(cur!=null) {
					finished = false;
					if(cur.getDocId()==i) {
						//System.out.println(cur.getCount());
						score += scorer.g_RawCount(queries)*scorer.f_RawCount(cur);
					}
				}
			}
			if(score>0) pq.add(new int[] {i, score});
			if(pq.size()>k)
				pq.poll();
			if(finished) break;
		}
		while(!pq.isEmpty()) {
			res.add(pq.poll()[0]);
		}
		Collections.reverse(res);
		return res;
	}
	
	public int getVocabulary() {
		return vocabulary;
	}
	
	public List<String> getVocabularyList(){
		List<String> res = new ArrayList<String>(lookupTable.keySet());
		return res;
	}
	
	public void setVocabulary(int x) {
		vocabulary = x;
	}
	
	public int getNumDoc() {
		return numDoc;
	}
	
	public void setNumDoc(int x) {
		numDoc = x;
	}
	
	public long getNumTokens() {
		return numTokens;
	}
	
	public void setNumTokens(long x) {
		numTokens = x;
	}
}
