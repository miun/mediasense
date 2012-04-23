package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class CheckPredecessorMessage extends Message {
	private NodeID hash;
	
	public CheckPredecessorMessage(String from,String to,NodeID hash) {
		super(from,to,CHECK_PREDECESSOR);
		this.hash = hash;
	}
	
	public NodeID getHash() {
		return hash;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-FIND_SUCCESSOR")+ " | findKey: " + hash.toString(); 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
}
