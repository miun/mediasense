package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class CheckPredecessorResponseMessage extends Message {
	private String networkAddress;
	private NodeID hash;
	
	public CheckPredecessorResponseMessage(String from,String to,String networkAddress,NodeID hash) {
		super(from,to,FIND_SUCCESSOR_RESPONSE);
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
		return super.toString("MSG-PREDECESSOR_SUCCESSOR")+ " | sucKey: " + hash.toString() + " addr: (" + networkAddress.toString() + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 4 + NodeID.ADDRESS_SIZE;
	}}
