package manager.dht;

public class FingerEntry implements Comparable<FingerEntry> {
	private NodeID nodeID;
	private String networkAddress;
	
	//Null position for comparison with zero position on DHT circle
	private static final byte[] NULL_POSITION_CONSTANT = new byte[NodeID.ADDRESS_SIZE];
	public static final FingerEntry NULL_POSITION = new FingerEntry(new NodeID(NULL_POSITION_CONSTANT),null);
	
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
