package processor;

import utility.*;

public class Scorer {
	public int g_RawCount(String[] Q) {
		return 1;
	}
	
	public int f_RawCount(Postings l) {
		return l.getCount();
	}
}
