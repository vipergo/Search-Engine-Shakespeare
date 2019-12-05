package processor;

import java.util.*;

import processor.scorer.RawCount;
import processor.scorer.Scorer;

import java.io.IOException;
import java.io.RandomAccessFile;

import utility.*;

public class Indexes {
	private Map<String, PostingList> lookupTable;
	private Map<String, List<Integer>> playId2DocId;
	private Map<Integer, DocStat> docId2DocStat;
	private Map<Integer, DocVec> docId2DocVec;
	
	private VByte vb;
	private int vocabulary;
	private int numDoc;
	private long numTokens;
	private Scorer scorer;
	private boolean languageModel;
	
	public Indexes(JsonParser jp, VByte vb) {
		scorer = new RawCount();
		vocabulary = 0;
		numDoc = 0;
		numTokens = 0;
		this.vb = vb;
		jp.readLookupTable(this);
		playId2DocId = jp.readPlayId2DocId();
		docId2DocStat = jp.readDocId2DocStat();
		docId2DocVec = jp.readDocId2DocVec();
		//forHW1Report();
	}
	
	public void setLookupTable(Map<String, PostingList> lt) {
		lookupTable = lt;
	}
	
	public Map<String, PostingList> getLookupTable(){
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
		System.out.printf("average length of a scene is: %f \n", (double)sceneLenSum/numDoc);
		System.out.printf("shortest scene is %s, with length %d \n", shortScene, shortestScene);
		System.out.printf("longest play is %s, with length %d \n", longPlay, longesPlay);
		System.out.printf("shortest play is %s, with length %d \n", shortPlay, shortestPlay);
	}
	
	public PostingList getTerm(String q) {
		PostingList res = null;
		PostingList t = lookupTable.get(q);
		
		boolean encode = vb.isEncode();
		
		if(t==null) {
			System.out.println("query string is not in the vocabulary");
			return null;
		}
		try {
			String filename = encode ? "data/binaryFileVByte" : "data/binaryFile";
			RandomAccessFile disk = new RandomAccessFile(filename, "r");
			
			res = new PostingList(t);
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
	
	public DocStat getDocStat(int docId) {
		return docId2DocStat.get(docId);
	}
	
	public List<RankResult> query(String queries, int k){
		return query(queries.split("\\s+"), k);
	}
	
	public Map<Integer, DocVec> getDocId2DocVec() {
		return docId2DocVec;
	}
	
	public List<RankResult> query(String[] queries, int k){
		//String[] queries = Q.split("\\s+");
		List<RankResult> res = new ArrayList<RankResult>(k);
		List<PostingList> terms = new ArrayList<PostingList>(queries.length);
		Map<String, Integer> qtf = new HashMap<String, Integer>();
		for(String s : queries) {
			terms.add(getTerm(s));
			qtf.put(s, qtf.getOrDefault(s, 0)+1);
		}

		PriorityQueue<Rank> pq = new PriorityQueue<Rank>(k+1, new Comparator<Rank>() {
			public int compare(Rank a, Rank b) {
				if(a.score<b.score) return -1;
				else if(a.score>b.score) return 1;
				return 0;
			}
		});
		int[] pointers = new int[queries.length];
		
		for(int i=1; i<numDoc; i++) {
			boolean finished = true;
			double score = 0.0;
			int docLen = docId2DocStat.get(i).getLength();
			for(int j=0; j<pointers.length; j++) {
				PostingList term = terms.get(j);
				int tf = term.getCount(), numOfDoc = term.getDocCount();
				Postings cur = term.skipToDoc(pointers, j, i);
				int queryTermFreq = qtf.get(queries[j]);
				if(cur!=null) {
					finished = false;
					if(cur.getDocId()==i) {
						//System.out.println(cur.getCount());
						score += scorer.computeScore(tf, queryTermFreq, cur.getCount(), 
								docLen, numDoc, (int)numTokens,numOfDoc);
					}else if(languageModel) {
						score += scorer.computeScore(tf, queryTermFreq, 0, 
								docLen, numDoc, (int)numTokens, numOfDoc);
					}
				}else if(languageModel) {
					score += scorer.computeScore(tf, queryTermFreq, 0, 
							docLen, numDoc, (int)numTokens, numOfDoc);
				}
			}
			if(score!=0) pq.add(new Rank(score, i));
			if(pq.size()>k)
				pq.poll();
			if(finished) break;
		}
		while(!pq.isEmpty()) {
			Rank cur = pq.poll();
			res.add(new RankResult(cur.score, docId2DocStat.get(cur.docId)));
		}
		Collections.reverse(res);
		return res;
	}
	
	public void setScorer(Scorer s, boolean lan) {
		this.scorer = s;
		this.languageModel = lan;
		System.out.printf("Set scorer as %s, %b\n", s.toString(), languageModel);
	}
	
	public int getVocabulary() {
		return vocabulary;
	}
	
	public List<String> getVocabularyList(){
		List<String> res = new ArrayList<String>(lookupTable.keySet());
		return res;
	}
	
	public int getDocLen(int docId) {
		return docId2DocStat.get(docId).getLength();
	}
	
	public void setVocabulary(int x) {
		vocabulary = x;
	}
	
	public int getNumDoc() {
		return numDoc;
	}
	
	public int getTDF(String s) {
		return lookupTable.get(s).getDocCount();
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
