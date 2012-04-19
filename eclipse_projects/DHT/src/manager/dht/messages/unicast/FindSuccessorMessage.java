package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class FindSuccessorMessage extends Message {
	private NodeID hash;
	
	public FindSuccessorMessage(String from,String to,NodeID hash) {
		super(from,to,FIND_SUCCESSOR);
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
