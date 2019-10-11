package constructor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

import utility.*;

public class InvertedList {
	private List<FullDoc> docs;
	private Map<String, List<Integer>> playId2DocId;
	private Map<Integer, DocStat> docId2DocStat;
	
	private Map<String, Term> terms;
	private int vocabulary = 0;
	private int numDoc = 0;
	private long numTokens = 0;
	
	public InvertedList(){
		terms = new HashMap<String, Term>(20000);
		playId2DocId = new HashMap<String, List<Integer>>(800);
		docId2DocStat = new HashMap<Integer, DocStat>(1000);
		//Need to separate construct() and object constructor
		//construct();
		//writeLookupTable();
	}
	
	public void construct(JsonParser jp) {
		try {
			docs = jp.read("./src/main/resources/shakespeare-scenes.json");
		}catch(IOException e) {
			e.printStackTrace();
		}
		numDoc = docs.size();
		for(FullDoc d : docs) {
			String[] tokens = d.getText().split("\\s+");
			int count = 0;
			for(String t : tokens) {
				if(t.length()==0) continue;
				count++;
				if(!terms.containsKey(t))
					terms.put(t, new Term());
				terms.get(t).addPos(d.getDocId(), count);
				numTokens++;
			}
			
			String pid = d.getPlayId(); int docId = d.getDocId();
			if(!playId2DocId.containsKey(pid)) {
				playId2DocId.put(pid, new ArrayList<Integer>());
			}
			playId2DocId.get(pid).add(docId);
			docId2DocStat.put(docId, new DocStat(d));
		}
		vocabulary = terms.size();
		printStat();
	}
	
	public void printStat() {
		System.out.printf("Inverted List Construct Complete! \n"
				+ "voca=%d \n"
				+ "numDoc=%d \n"
				+ "numTokens=%d \n", vocabulary, numDoc, numTokens);
	}
	
	public List<String> getVocabularyList(){
		List<String> res = new ArrayList<String>(terms.keySet());
		return res;
	}
	
	public int getVocabulary() {
		return vocabulary;
	}
	
	public int getNumDoc() {
		return numDoc;
	}
	
	public long getNumTokens() {
		return numTokens;
	}
	
	public Map<String, Term> getTerms(){
		return terms;
	}
	
	public Term getTerm(String s) {
		return terms.get(s);
	}
	
	public void writeToFile(JsonParser jp, VByte vb) {
		if(vb.isEncode()) {
			writeToFileVByte(vb);
		}else {
			writeToFile();
		}
		writeLookupTable(jp);
	}
	
	public void writeLookupTable(JsonParser jp) {
		//JsonParser jp = new JsonParser();
		jp.write(this);
		jp.writeDocId2DocStat(docId2DocStat);
		jp.writePlayId2DocId(playId2DocId);
	}
	
	public void writeToFile() {
		try {
			RandomAccessFile disk = new RandomAccessFile("data/binaryFile", "rw");
			//long offset = 0;
			for(Map.Entry<String,Term> entry : terms.entrySet()) {
				Term pl = entry.getValue();
				
				pl.setOffset(disk.getFilePointer()); //for lookup table
				List<Postings> postings = pl.getPList();
				int bytes = 0;
				for(Postings p : postings) {
					List<Integer> pos = p.getPos();
					int len = pos.size();
					int[] data = new int[len+2];
					data[0] = len;
					data[1] = p.getDocId();
					for(int i=0; i<len; i++) {
						data[i+2] = pos.get(i);
					}
					bytes += data.length*4;
					ByteBuffer bb = ByteBuffer.allocate(data.length*4);
					bb.asIntBuffer().put(data);
					disk.write(bb.array());
				}
				pl.setBytes(bytes);
				//offset = disk.getFilePointer();
			}
			disk.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToFileVByte(VByte vb) {
		try {
			RandomAccessFile disk = new RandomAccessFile("data/binaryFileVByte", "rw");
			//long offset = 0;
			for(Map.Entry<String,Term> entry : terms.entrySet()) {
				Term pl = entry.getValue();
				
				pl.setOffset(disk.getFilePointer()); //for lookup table
				List<Postings> postings = pl.getPList();
				int deltaDocId = 0, bytes = 0;
				for(Postings p : postings) {
					List<Integer> pos = p.getPos();
					List<Byte> posB = new ArrayList<Byte>((pos.size()+2)*5);
					vb.encoder(posB, pos.size());
					vb.encoder(posB, p.getDocId()-deltaDocId);
					deltaDocId = p.getDocId();
					int deltaX = 0;
					for(int x : pos) {
						 vb.encoder(posB, x-deltaX);
						 deltaX = x;
					}
					bytes += posB.size();
					ByteBuffer bb = ByteBuffer.allocate(posB.size());
					for(byte by : posB) {
						bb.put(by);
					}
					disk.write(bb.array());
				}
				
				pl.setBytes(bytes);
			}
			disk.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}

