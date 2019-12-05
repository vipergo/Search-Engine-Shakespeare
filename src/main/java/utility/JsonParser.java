package utility;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import constructor.InvertedList;
import processor.Indexes;

public class JsonParser {
	
	
	public List<FullDoc> read(String filename) throws IOException{
		List<FullDoc> res = new ArrayList<FullDoc>(1500);
		try {
			JsonReader jsonReader = new JsonReader(new FileReader(filename));
			jsonReader.beginObject();
	
		    while (jsonReader.hasNext()) {
		    	String name = jsonReader.nextName();
		        if (name.equals("corpus")) {
		        	jsonReader.beginArray();
	                while(jsonReader.hasNext()) {
	                	jsonReader.beginObject();
	                	jsonReader.nextName();
	                	String pid = jsonReader.nextString();
	                	jsonReader.nextName();
	                	String sid = jsonReader.nextString();
	                	jsonReader.nextName();
	                	int snum = jsonReader.nextInt()+1;
	                	jsonReader.nextName();
	                	String txt = jsonReader.nextString();
	                	FullDoc d = new FullDoc(pid, sid, snum, txt);
	                    res.add(d);
	                    jsonReader.endObject();
	                }
	                jsonReader.endArray();
		        }
		    }
	
		   jsonReader.endObject();
		   jsonReader.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
		return res;
	}
	
	public void write(InvertedList il) {
		Map<String, PostingList> terms = il.getTerms();
		try{
			JsonWriter writer = new JsonWriter(new FileWriter("data/lookupTable.json", false));
            
			writer.beginObject();
			writer.name("vocabulary").value(il.getVocabulary());
			writer.name("numDoc").value(il.getNumDoc());
			writer.name("numTokens").value(il.getNumTokens());
			
			for(Map.Entry<String, PostingList> entry : terms.entrySet()) {
				writer.name(entry.getKey());
				writer.beginObject();
				
				PostingList pl = entry.getValue();
				//System.out.println(pl.getOffset());
				writer.name("offset").value(pl.getOffset());
				writer.name("bytes").value(pl.getBytes());
				//System.out.println(pl.getBytes());
				writer.name("docCount").value(pl.getDocCount());
				writer.name("count").value(pl.getCount());
				
				writer.endObject();
			}

            writer.endObject();
            
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void readLookupTable(Indexes ind){
		System.out.println("Building index lookup table...");
		Map<String, PostingList> res = new HashMap<String, PostingList>();
		try {
			JsonReader jsonReader = new JsonReader(new FileReader("data/lookupTable.json"));
			jsonReader.beginObject();
			
			jsonReader.nextName();
			ind.setVocabulary(jsonReader.nextInt());
			jsonReader.nextName();
			ind.setNumDoc(jsonReader.nextInt());
			jsonReader.nextName();
			ind.setNumTokens(jsonReader.nextLong());
			
			while (jsonReader.hasNext()) {
		    	String t = jsonReader.nextName();
		    	
		    	jsonReader.beginObject();
		    	jsonReader.nextName();
		    	long offset = jsonReader.nextLong();
		    	jsonReader.nextName();
		    	int bytes = jsonReader.nextInt();
		    	jsonReader.nextName();
		    	int docCount = jsonReader.nextInt();
		    	jsonReader.nextName();
		    	int count = jsonReader.nextInt();
		    	jsonReader.endObject();
		    	
		    	res.put(t, new PostingList(offset, bytes, docCount, count));
			}
			
			jsonReader.endObject();
			jsonReader.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
		ind.setLookupTable(res);
	}
	
	public void writePlayId2DocId(Map<String, List<Integer>> playId2DocId) {
		try {
			JsonWriter writer = new JsonWriter(new FileWriter("data/playId2DocId.json", false));
			writer.beginObject();
			for(Map.Entry<String, List<Integer>> entry : playId2DocId.entrySet()) {
				writer.name(entry.getKey());
				writer.beginArray();
				for(int x : entry.getValue()) {
					writer.value(x);
				}
				writer.endArray();
			}
			writer.endObject();
			writer.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public Map<String, List<Integer>> readPlayId2DocId() {
		Map<String, List<Integer>> res = new HashMap<String, List<Integer>>();
		try {
			JsonReader reader = new JsonReader(new FileReader("data/playId2DocId.json"));
			reader.beginObject();
			while(reader.hasNext()) {
				String pid = reader.nextName();
				List<Integer> al = new ArrayList<Integer>();
				reader.beginArray();
				while(reader.hasNext()) {
					al.add(reader.nextInt());
				}
				reader.endArray();
				res.put(pid, al);
			}
			reader.endObject();
			reader.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
		return res;
	}
	
	public void writeDocId2DocStat(Map<Integer, DocStat> docId2DocStat) {
		try {
			JsonWriter writer = new JsonWriter(new FileWriter("data/docId2DocStat.json", false));
			writer.beginObject();
			for(Map.Entry<Integer, DocStat> entry : docId2DocStat.entrySet()) {
				writer.name(entry.getKey().toString());
				DocStat x = entry.getValue();
				writer.beginObject();
				writer.name("playId").value(x.getPlayId());
				writer.name("sceneId").value(x.getSceneId());
				writer.name("sceneNum").value(x.getDocId());
				writer.name("length").value(x.getLength());
				writer.endObject();
			}
			writer.endObject();
			writer.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public Map<Integer, DocStat> readDocId2DocStat() {
		Map<Integer, DocStat> res = new HashMap<Integer, DocStat>();
		try {
			JsonReader reader = new JsonReader(new FileReader("data/docId2DocStat.json"));
			reader.beginObject();
			while(reader.hasNext()) {
				int docId = Integer.parseInt(reader.nextName());
				reader.beginObject();
				reader.nextName();
				String pid = reader.nextString();
				reader.nextName();
				String sid = reader.nextString();
				reader.nextName();
				int sceneNum = reader.nextInt();
				reader.nextName();
				int len = reader.nextInt();
				reader.endObject();
				res.put(docId, new DocStat(pid, sid, sceneNum, len));
			}
			reader.endObject();
			reader.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
		return res;
	}
	
	public void writeDocId2DocVec(Map<Integer, DocVec> docId2DocVec) {
		try {
			JsonWriter writer = new JsonWriter(new FileWriter("data/docId2DocVec.json", false));
			writer.beginObject();
			for(Map.Entry<Integer, DocVec> entry : docId2DocVec.entrySet()) {
				writer.name(entry.getKey().toString());
				DocVec x = entry.getValue();
				writer.beginObject();
				Map<String, Double> vec = x.getVec();
				for(Map.Entry<String, Double> item : vec.entrySet()) {
					writer.name(item.getKey()).value(item.getValue());
				}
				writer.endObject();
			}
			writer.endObject();
			writer.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public Map<Integer, DocVec> readDocId2DocVec() {
		Map<Integer, DocVec> res = new HashMap<Integer, DocVec>();
		try {
			JsonReader reader = new JsonReader(new FileReader("data/docId2DocVec.json"));
			reader.beginObject();
			while(reader.hasNext()) {
				int docId = Integer.parseInt(reader.nextName());
				DocVec dv = new DocVec(docId);
				reader.beginObject();
				while(reader.hasNext()) {
					String s = reader.nextName();
					Double doub = reader.nextDouble();
					dv.put(s, doub);
				}
				reader.endObject();
				res.put(docId, dv);
			}
			reader.endObject();
			reader.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
		return res;
	}
}

