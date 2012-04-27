package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class CheckSuccessorMessage extends Message {
	private NodeID hash;
	
	public CheckSuccessorMessage(String from,String to,NodeID hash) {
		super(from,to,CHECK_SUCCESSOR);
		this.hash = hash;
	}
	
	public NodeID getHash() {
		return hash;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-CHECK_SUCCESSOR")+ " | hash: " + hash.toString(); 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
}
