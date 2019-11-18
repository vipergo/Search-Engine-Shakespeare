package inferenceNetwork.proximityNode;

import processor.Indexes;
import processor.scorer.Diri;
import java.util.List;
import utility.PostingList;

public class OrderedWindow extends ProximityWindow {
	
	public OrderedWindow(List<ProximityNode> children, Indexes ind, Diri scorer, int windowSize) {
		super.ind = ind;
		super.scorer = scorer;
		super.pl = new PostingList();
		super.windowSize = windowSize;
		generatePL(children);
	}
	
	public OrderedWindow(Indexes ind, Diri scorer, int windowSize) {
		super.ind = ind;
		super.scorer = scorer;
		//super.pl = new PostingList();
		super.windowSize = windowSize;
	}
	
	public void generatePL(List<ProximityNode> children) {
		//boolean hasNext = true;
		super.pl = new PostingList();
		outter:
		while(true) {
			int max = -1;
			for(ProximityNode child : children) {
				if(!child.hasNext()) break outter;
				max = Math.max(max, child.nextCandidate());
			}
			if(max==-1) break;
			boolean flag = true;
			for(ProximityNode child : children) {
				flag = child.skipTo(max) && flag;
			}
			//System.out.println(flag);
			if(flag) {
				generatePos(children, windowSize, max);
				for(ProximityNode child : children) {
					child.skipTo(max+1);
				}
			}
		}
		super.pliterator = super.pl.getPLIterator();
	}
	
	private void generatePos(List<ProximityNode> children, int windowSize, int docId) {
		//System.out.println(docId);
		outter:
		while(true) {
			int prev = children.get(0).getPos(), start = prev;
			if(prev==-1) break;
			boolean find = true;
			for(ProximityNode child : children) {
				int pos = child.skipToPos(prev);
				if(pos==-1) break outter;
				if(pos<prev || pos>prev+windowSize) {
					find = false;
					break;
				}
				prev = pos;
			}
			if(find) {
				super.pl.addPos(docId, start);
				for(ProximityNode child : children) {
					child.nextPos();
				}
			}else {
				children.get(0).nextPos();
			}
		}
		
	}
}
