package app;

import java.util.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

import constructor.*;
import processor.*;
import utility.*;

public class Test {
	List<String> vocabularyList;
	Indexes ind;
	InvertedList il;
	
	public Test(Indexes ind, InvertedList il) {
		vocabularyList = ind.getVocabularyList();
		this.ind = ind;
		this.il = il;
		writeTermsToFile(generate700Terms(), 7);
		writeTermsToFile(generate1400Terms(readTerms(7)), 14);
	}
	
	public void doTheTest(int k) {
		System.out.println("Start to testing...");
		System.out.println("first run...");
		doTheTest(7, k);
		doTheTest(14, k);
		System.out.println("second run...");
		doTheTest(7, k);
		doTheTest(14, k);
	}
	
	public void doTheTest(int len, int k) {
		List<String[]> terms = readTerms(len);
		long start = System.currentTimeMillis();
		for(String[] queries : terms) {
			ind.query(queries, k);
		}
		long end = System.currentTimeMillis();
		System.out.printf("It took %d ms to query 100 set of %d terms queries with k=%d\n", end-start, len, k);
	}
	
	public String generate700Terms() {
		System.out.println("generating 7*100 terms into files...");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<100; i++) {
			Collections.shuffle(vocabularyList);
			for(int j=0; j<7; j++) {
				sb.append(vocabularyList.get(j));
				sb.append(' ');
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public void writeTermsToFile(String terms, int len) {
		String filename = len == 7 ? "data/term_700.txt" : "data/term_1400.txt";
		//File file = new File(filename);
		//if(file.exists()) return;
		
		try {
			FileWriter fw = new FileWriter(filename, false);
			fw.write(terms);
			fw.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<String[]> readTerms(int length){
		List<String[]> res = new ArrayList<String[]>(101);
		String filename = length==7 ? "data/term_700.txt" : "data/term_1400.txt";
		File file = new File(filename);
		if(!file.exists()) {
			System.out.println("file doesn't exit...");
			return res;
		}
		
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] s = line.split(" ");
				if(s.length>1) res.add(s);
			}
			sc.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
//		System.out.println(res.size());
//		for(int i=0; i<5; i++) {
//			for(String s : res.get(i))
//				System.out.println(s);
//		}
		return res;
	}
	
	public String generate1400Terms(List<String[]> terms700) {
		System.out.println("generating 14*100 terms into files...");
		StringBuilder sb = new StringBuilder();
		for(String[] sarry: terms700) {
			for(int i=0; i<sarry.length; i++) {
				sb.append(sarry[i]);
				sb.append(' ');
				sb.append(findClosestTerm(sarry[i]));
				sb.append(' ');
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public String findClosestTerm(String a) {
		Term x = ind.getTerm(a);
		double max = 0.0;
		String res = "";
		for(String s : vocabularyList) {
			Term y = il.getTerm(s);
			double cur = diceCoefficient(x, y);
			if(cur>max) {
				max = cur;
				res = s;
			}
		}
		//System.out.printf("%s: %f", res, max);
		return res;
	}
	
	public double diceCoefficient(Term t1, Term t2) {
		int na = t1.getCount(), nb = t2.getCount(), nba = 0;
		List<Postings> pl =  t1.getPList();
		for(Postings p : pl) {
			int[] pointer = new int[1];
			Postings p2 = t2.skipToDoc(pointer, 0, p.getDocId());
			if(p2==null) break;
			if(p2.getDocId()==p.getDocId()) {
				List<Integer> l1 = p.getPos(), l2 = p2.getPos();
				int i=0, j=0;
				while(i<l1.size() && j<l2.size()) {
					int a = l1.get(i), b = l2.get(j);
					if(a+1==b){
						nba++;
						i++;
					}else if(a<b) {
						i++;
					}else {
						j++;
					}
				}
			}
		}
		return ((double)nba)/(na+nb);
	}
}
