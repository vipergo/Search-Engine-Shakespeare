package app;

import processor.*;
import inferenceNetwork.*;
import inferenceNetwork.proximityNode.*;
import inferenceNetwork.beliefNode.*;
import processor.scorer.Diri;
import processor.scorer.Scorer;
import utility.*;
import constructor.InvertedList;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class HW4 {

	public static void main(String[] args) {
		boolean encode = true;
		System.out.printf("Compression: %b \n", encode);
		JsonParser jp = new JsonParser();
		VByte vb = new VByte(encode);
		
		InvertedList il = new InvertedList();
		il.construct(jp);
		il.writeToFile(jp, vb);

		Indexes ind = new Indexes(jp, vb);
		Diri scorer = new Diri(1500);
		int k =10;
		
		String[] queries = new String[] {
				"the king queen royalty",
				"servant guard soldier",
				"hope dream sleep",
				"ghost spirit",
				"fool jester player",
				"to be or not to be",
				"alas",
				"alas poor",
				"alas poor yorick",
				"antony strumpet"
		};
		
		InferenceNetwork in = new InferenceNetwork();
		
		ProximityWindow parent = new OrderedWindow(ind, scorer, 1);
		writeFile(runTestWindow(in, ind, scorer, queries, k, parent, 1), "../od1.trecrun");
		parent = new UnorderedWindow(ind, scorer, 1);
		writeFile(runTestWindow(in, ind, scorer, queries, k, parent, -1), "../ud.trecrun");
		QueryNode target = new BeliefAnd();
		writeFile(runTestBelief(in, ind, scorer, queries, k, target), "../and.trecrun");
		target = new BeliefSum();
		writeFile(runTestBelief(in, ind, scorer, queries, k, target), "../sum.trecrun");
		target = new BeliefOr();
		writeFile(runTestBelief(in, ind, scorer, queries, k, target), "../or.trecrun");
		target = new BeliefMax();
		writeFile(runTestBelief(in, ind, scorer, queries, k, target), "../max.trecrun");
		
	}
	
	public static List<QueryNode> parseQuery(String query, Indexes ind, Diri scorer){
		String[] terms = query.split("\\s+");
		List<QueryNode> res = new ArrayList<QueryNode>(terms.length);
		for(String t : terms) {
			res.add(new TermNode(t, ind, scorer));
		}
		return res;
	}
	
	public static List<ProximityNode> parseQueryProx(String query, Indexes ind, Diri scorer){
		String[] terms = query.split("\\s+");
		List<ProximityNode> res = new ArrayList<ProximityNode>(terms.length);
		for(String t : terms) {
			res.add(new TermNode(t, ind, scorer));
		}
		return res;
	}
	
	public static List<List<String>> runTestBelief(InferenceNetwork in, Indexes ind, 
		Diri scorer, String[] queries, int k, QueryNode target) {
		String scorerStr = scorer.toString();
		List<List<String>> result = new ArrayList<List<String>>(queries.length);
		for(int j=0; j<queries.length; j++) {
			List<QueryNode> lqn = parseQuery(queries[j], ind, scorer);
			target.setChildren(lqn);
			List<Rank> res = in.runQuery(target, k);
			List<String> mid = new ArrayList<String>(k);
			result.add(mid);
			for(int i=0; i<res.size(); i++) {
				Rank rank = res.get(i);
				RankResult x = new RankResult(rank.score, ind.getDocStat(rank.docId));
				String output = String.format("Q%d %s %-35.35s \t %d %.3f %s\n", j+1, "skip", 
						x.doc.getSceneId(), i+1, x.score, scorerStr);
				mid.add(output);
				System.out.print(output);
			}
			System.out.println();
		}
		return result;
	}
	
	public static List<List<String>> runTestWindow(InferenceNetwork in, Indexes ind, 
			Diri scorer, String[] queries, int k, ProximityWindow target, int ws) {
			String scorerStr = scorer.toString();
			List<List<String>> result = new ArrayList<List<String>>(queries.length);
			for(int j=0; j<queries.length; j++) {
				List<ProximityNode> lqn = parseQueryProx(queries[j], ind, scorer);
				target.generatePL(lqn);
				if(ws==-1) target.setWindowSize(lqn.size()*3);
				List<Rank> res = in.runQuery(target, k);
				List<String> mid = new ArrayList<String>(k);
				result.add(mid);
				for(int i=0; i<res.size(); i++) {
					Rank rank = res.get(i);
					RankResult x = new RankResult(rank.score, ind.getDocStat(rank.docId));
					String output = String.format("Q%d %s %-35.35s \t %d %.3f %s\n", j+1, "skip", 
							x.doc.getSceneId(), i+1, x.score, scorerStr);
					mid.add(output);
					System.out.print(output);
				}
				System.out.println();
			}
			return result;
		}
	
	public static void writeFile(List<List<String>> result, String filename) {
		try {
			FileWriter fw = new FileWriter(filename, false);
			for(List<String> sarr : result) {
				for(String s : sarr) {
					fw.write(s);
				}
				fw.write('\n');
			}
			fw.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
}
