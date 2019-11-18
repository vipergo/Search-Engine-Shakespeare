package inferenceNetwork.proximityNode;

import java.util.List;

import processor.Indexes;
import processor.scorer.Diri;
import utility.PostingList;

public class BooleanAnd extends ProximityWindow {
	public BooleanAnd(List<ProximityNode> children, Indexes ind, Diri scorer, int windowSize) {
		super.ind = ind;
		super.scorer = scorer;
		super.pl = new PostingList();
		super.windowSize = windowSize;
		generatePL(children);
	}
	
	public BooleanAnd(Indexes ind, Diri scorer, int windowSize) {
		super.ind = ind;
		super.scorer = scorer;
		super.pl = new PostingList();
		super.windowSize = windowSize;
	}
	
	public void generatePL(List<ProximityNode> children) {
		//boolean hasNext = true;
		super.pl = new PostingList();
		outter:
		while(true) {
			int max = 0;
			for(ProximityNode child : children) {
				if(!child.hasNext()) break outter;
				max = Math.max(max, child.nextCandidate());
			}
			if(max==0) break;
			boolean flag = true;
			for(ProximityNode child : children) {
				flag = flag && child.skipTo(max);
			}
			//System.out.println(flag);
			if(flag) {
				generatePos(children, max);
				for(ProximityNode child : children) {
					child.skipTo(max+1);
				}
			}
		}
		super.pliterator = super.pl.getPLIterator();
	}
	
	private void generatePos(List<ProximityNode> children, int docId) {
		//System.out.println(docId);
		outter:
		while(true) {
			int min = 0;
			if(children.get(0).getPos()==-1) break;
			for(int i=0;i<children.size(); i++) {
				ProximityNode child = children.get(i);
				int pos = child.getPos();
				if(pos==-1) break outter;
				if(pos<min) min = pos;
			}
			super.pl.addPos(docId, min);
			for(ProximityNode child : children) {
				child.nextPos();
			}
		}
		
	}
}
