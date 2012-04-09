package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;

public abstract class BroadcastMessage extends Message {
	
	//Limit counter for spanning tree
	//private int TTL;
	
	//Definition of the region the broadcast is responsible for
	private NodeID startKey;
	private NodeID endKey;

	//Type of the delivered message
	protected int internalType;
	
/*	public int getTTL() {
		return TTL;
	}*/
	
	/*public void setTTL(int TTL) {
		this.TTL = TTL;
	}*/
	
	public BroadcastMessage(int internalType) {
		this.type = Message.BROADCAST;
		this.internalType = internalType;
	}
	
	public NodeID getStartKey() {
		return startKey;
	}

	public void setStartKey(NodeID startKey) {
		this.startKey = startKey;
	}

	public NodeID getEndKey() {
		return endKey;
	}

	public void setEndKey(NodeID endKey) {
		this.endKey = endKey;
	}

	protected String toString(String text) {
		return "BROADCAST: ({" + startKey.toString() + "}->{" + endKey.toString() + "}) | " + extractMessage().toString();
	}
	
	//Extract unicast message
	public abstract Message extractMessage();
}
