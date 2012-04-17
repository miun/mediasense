package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class FindPredecessorResponseMessage extends Message {
	private String networkAddress;
	private NodeID hash;
	
	public FindPredecessorResponseMessage(String from,String to,String networkAddress,NodeID hash) {
		super(from,to,Message.FIND_PREDECESSOR_RESPONSE);
		this.networkAddress = networkAddress;
		this.hash = hash;
	}

	public String getNetworkAddress() {
		return networkAddress;
	}

	public NodeID getHash() {
		return hash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-PREDECESSOR_RESPONSE")+ " | predecessorKey: " + hash.toString() + " addr: (" + networkAddress.toString() + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 4 + NodeID.ADDRESS_SIZE;
	}
}
