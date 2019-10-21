package app;

import processor.*;
import utility.*;
import constructor.InvertedList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class HW2 {

	public static void main(String[] args) {
		boolean encode = true;
		System.out.printf("Compression: %b \n", encode);
		JsonParser jp = new JsonParser();
		VByte vb = new VByte(encode);
		
//		InvertedList il = new InvertedList();
//		il.construct(jp);
//		il.writeToFile(jp, vb);

		Indexes ind = new Indexes(jp, vb);
		int k = 10;
		
		Scorer bm25 = new BM25(), JM = new JM(), Dir = new Diri();
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
		//test(ind, bm25, queries, k, false);
		//test(ind, JM, queries, k, true);
		///test(ind, Dir, queries, k, true);
		writeFile(test(ind, bm25, queries, k, false), "../bm25.trecrun");
		writeFile(test(ind, JM, queries, k, true), "../ql-jm.trecrun");
		writeFile(test(ind, Dir, queries, k, true), "../ql-dir.trecrun");
	}
	
	public static String[][] test(Indexes ind, Scorer scorer, String[] queries, int k, boolean lm) {
		ind.setScorer(scorer, lm);
		String scorerStr = scorer.toString();
		String[][] result = new String[queries.length][k];
		for(int j=0; j<queries.length; j++) {
			String s = queries[j];
			List<RankResult> res = ind.query(s, k);
			//System.out.printf("%s: \n", s);
			for(int i=0; i<res.size(); i++) {
				RankResult x = res.get(i);
				String output = String.format("Q%d %s %-35.35s \t %d %.3f %s\n", j+1, "skip", 
						x.doc.getSceneId(), i+1, x.score, scorerStr);
				result[j][i] = output;
				System.out.print(output);
			}
			System.out.println();
		}
		return result;
	}
	
	public static void writeFile(String[][] result, String filename) {
		try {
			FileWriter fw = new FileWriter(filename, false);
			for(String[] sarr : result) {
				for(String s : sarr) {
					fw.write(s);
				}
			}
			fw.close();
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
}
