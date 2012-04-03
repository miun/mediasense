package manager.dht;

public class FingerEntry implements Comparable<FingerEntry> {
	private NodeID nodeID;
	private String networkAddress;
	
	//Static constants for max and min positions on the DHT 
	public static final FingerEntry MIN_POS_FINGER = new FingerEntry(NodeID.MIN_POSITION(),null);
	public static final FingerEntry MAX_POS_FINGER = new FingerEntry(NodeID.MAX_POSITION(),null);
	
	//TODO for later optimization
	private int avg_delay;

	public FingerEntry(NodeID nodeID,String networkAddress) {
		this.nodeID = nodeID;
		this.networkAddress = networkAddress;
	}
	
	@Override
	public int compareTo(FingerEntry comp) {
		return nodeID.compareTo(comp.nodeID);
	}
	
	public NodeID getNodeID() {
		return nodeID;
	}
	
	public String getNetworkAddress() {
		return networkAddress;
	}
}
