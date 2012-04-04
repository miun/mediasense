package manager.dht;

public class FingerEntry implements Comparable<FingerEntry> {
	private NodeID nodeID;
	private String networkAddress;
	
	/*R.I.P
	//Static constants for max and min positions on the DHT 
	public static final FingerEntry MIN_POS_FINGER = new FingerEntry(NodeID.MIN_POSITION(),null);
	public static final FingerEntry MAX_POS_FINGER = new FingerEntry(NodeID.MAX_POSITION(),null);
	*/
	
	//TODO for later optimization and fault handling
	private int avg_delay;
	private boolean bad_connection = false;
	private int lastKeepAliveTime = -1;

	public FingerEntry(NodeID nodeID,String networkAddress) {
		this.nodeID = nodeID;
		this.networkAddress = networkAddress;
	}
	
	@Override
	public int compareTo(FingerEntry comp) {
		return nodeID.compareTo(comp.nodeID);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeID == null) ? 0 : nodeID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FingerEntry other = (FingerEntry) obj;
		if (nodeID == null) {
			if (other.nodeID != null)
				return false;
		} else if (!nodeID.equals(other.nodeID))
			return false;
		return true;
	}
	
	public void setKeepAliveTime(int time) {
		lastKeepAliveTime = time;
	}

	public NodeID getNodeID() {
		return nodeID;
	}
	
	public String getNetworkAddress() {
		return networkAddress;
	}
}
