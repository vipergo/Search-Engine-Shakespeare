package inferenceNetwork.proximityNode;

import java.util.List;

public abstract class ProximityWindow extends ProximityNode {
	public int windowSize;
	
	public abstract void generatePL(List<ProximityNode> children);
	public void setWindowSize(int ws) {
		this.windowSize = ws;
	}
}
