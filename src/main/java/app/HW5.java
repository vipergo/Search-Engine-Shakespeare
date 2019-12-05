package app;

import constructor.InvertedList;
import processor.Indexes;
import utility.DocVec;
import utility.JsonParser;
import utility.VByte;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import clustering.*;

public class HW5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean encode = true;
		System.out.printf("Compression: %b \n", encode);
		JsonParser jp = new JsonParser();
		VByte vb = new VByte(encode);
		
//		InvertedList il = new InvertedList();
//		il.construct(jp);
//		il.writeToFile(jp, vb);

		Indexes ind = new Indexes(jp, vb);
		//System.out.println(ind.getDocId2DocVec().size());
		//writeFile(test(0.5, ind, Linkage.MEAN), "../cluster_out/");
		for(double thresh=0.05; thresh<1; thresh+=0.05) {
			String filename = String.format("../cluster_out/cluster-%.2f.out", thresh);
			List<Cluster> cl = test(thresh, ind, Linkage.MEAN);
			//writeFile(cl, filename);
			stat(thresh, cl);
		}
		//test(0.5, ind, Linkage.AVG);
	}
	
	public static List<Cluster> test(double threshhold, Indexes ind, Linkage link) {
		CosSim sim = new CosSim(ind);
		Map<Integer, DocVec> docId2DocVec = ind.getDocId2DocVec();
		List<Cluster> res = new ArrayList<Cluster>();
		int idx = 1;
		for(Integer docId : docId2DocVec.keySet()) {
			double best_score = -1;
			int cluster = -1;
			DocVec dv = docId2DocVec.get(docId);
			for(int i=0; i<res.size(); i++) {
				double score = res.get(i).score(dv);
				//if(docId%10==0) System.out.println(score);
				if(score>best_score) {
					best_score = score;
					cluster = i;
				}
			}
			if(best_score>=threshhold) {
				res.get(cluster).add(dv);
			}else {
				Cluster c = new Cluster(idx++, link, sim);
				c.add(dv);
				res.add(c);
			}
		}
		return res;
	}
	
	public static void stat(double thresh, List<Cluster> clusters) {
		int n = clusters.size(), total = 0;
		for(Cluster c : clusters) {
			total += c.totalDoc();
		}
		double avg = total/n;
		System.out.printf("%.2f %d %.2f\n", thresh, n, avg);
	}
	
	public static void writeFile(List<Cluster> clusters, String filename) {
		try {
			FileWriter fw = new FileWriter(filename, false);
			
			for(Cluster c : clusters) {
				int cid = c.getClusterId(); List<Integer> docIDlist = c.getDocIds();
				for(int did : docIDlist) {
					String s = String.format("%d %d\n", cid, did);
					fw.write(s);
					//System.out.println(s);
				}
			}
			
			fw.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
	}

}
