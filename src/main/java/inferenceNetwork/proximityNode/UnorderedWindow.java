package inferenceNetwork.proximityNode;

import java.util.List;

import processor.Indexes;
import processor.scorer.Diri;
import utility.PostingList;

public class UnorderedWindow extends ProximityWindow {
	
	public UnorderedWindow(List<ProximityNode> children, Indexes ind, Diri scorer, int windowSize) {
		super.ind = ind;
		super.scorer = scorer;
		super.pl = new PostingList();
		super.windowSize = windowSize;
		generatePL(children);
	}
	
	public UnorderedWindow(Indexes ind, Diri scorer, int windowSize) {
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
			int min = 0, max = 0;
			if(children.get(min).getPos()==-1) break;
			for(int i=0;i<children.size(); i++) {
				ProximityNode child = children.get(i);
				int pos = child.getPos();
				if(pos==-1) break outter;
				if(pos<children.get(min).getPos()) min = i;
				if(pos>children.get(max).getPos()) max = i;
			}
			if(children.get(max).getPos()-children.get(min).getPos()<=windowSize) {
				super.pl.addPos(docId, children.get(min).getPos());
				for(ProximityNode child : children) {
					child.nextPos();
				}
			}else {
				children.get(min).nextPos();
			}
		}
		
	}

}
