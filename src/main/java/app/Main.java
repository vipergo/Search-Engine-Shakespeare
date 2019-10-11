package app;

import java.util.Map;
import constructor.*;
import processor.*;
import utility.*;

public class Main {

	public static void main(String[] args) {
		boolean encode = false;
		if(args.length>0 && args[0].equals("true")) {
			encode = true;
		}
		System.out.printf("Compression: %b \n", encode);
		JsonParser jp = new JsonParser();
		InvertedList il = new InvertedList();
		il.construct(jp);
		VByte vb = new VByte(encode);
		il.writeToFile(jp, vb);

		Indexes ind = new Indexes(jp, vb);
		
		Test test = new Test(ind, il);
		int k = 10;
		if(args.length>1) {
			k = Integer.parseInt(args[1]);
		}
		test.doTheTest(k);
		
		if(args.length>2) {
			isLookUpTableWork(il, ind);
			isQueryWork(il, ind);
			//test.findClosestTerm("pullet");
		}
		
	}
	
	public static void isQueryWork(InvertedList il, Indexes ind) {
		System.out.println("testing is retrived term is correct...");
		String testTerm = "street";
		Term t1 = ind.getTerm(testTerm);
		Term t2 = il.getTerms().get(testTerm);
		System.out.printf("test term: %s \n", testTerm);
		System.out.printf("offset: %d | %d\n", t1.getOffset(), t2.getOffset());
		System.out.printf("docCount: %d | %d\n", t1.getDocCount(), t2.getDocCount());
		System.out.printf("count: %d | %d\n", t1.getCount(), t2.getCount());
		System.out.println("t1 vs t2");
		System.out.println(t1.isEqual(t2));
		t1.print();
		String txt = "before the wall";
		System.out.printf("testing query: %s \n", txt);
		System.out.println(ind.query(txt.split("\\s+"), 10).toString());
	}

	public static void isLookUpTableWork(InvertedList il, Indexes ind) {
		System.out.println("testing is look up table implement correctly...");
		System.out.println(il.getTerms().size()==il.getVocabulary());
		System.out.println(ind.getLookupTable().size()==il.getVocabulary());
		boolean match = true;
		if(ind.getLookupTable().size()!=il.getTerms().size())
			match = false;
		for(Map.Entry<String, Term> entry : ind.getLookupTable().entrySet()){
			Term x = entry.getValue();
			Term y = il.getTerms().get(entry.getKey());
			if(x.getOffset()!=y.getOffset() || x.getCount()!=y.getCount() || x.getDocCount()!=y.getDocCount()) {
				match = false;
				break;
			}
		}
		System.out.println(match);
	}
}
