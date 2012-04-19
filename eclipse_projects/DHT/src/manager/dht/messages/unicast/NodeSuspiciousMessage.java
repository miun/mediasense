package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class NodeSuspiciousMessage extends Message {
	private NodeID hash;
	
	public NodeSuspiciousMessage(String from, String to, NodeID hash) {
		super(from, to, NODE_SUSPICIOUS);
		this.hash = hash;
	}
	
	public NodeID getHash() {
		return hash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-NODE-SUSPICIOUS") + " hash: " + hash.toString() ;
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}

}
