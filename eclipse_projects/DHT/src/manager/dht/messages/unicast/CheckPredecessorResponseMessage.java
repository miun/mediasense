package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class CheckPredecessorResponseMessage extends Message {
	private String preNetworkAddress;
	private NodeID preHash;
	
	public CheckPredecessorResponseMessage(String from,String to,String preNetworkAddress,NodeID preHash) {
		super(from,to,FIND_SUCCESSOR_RESPONSE);
		this.preNetworkAddress = preNetworkAddress;
		this.preHash = preHash;
	}

	public String getPreNetworkAddress() {
		return preNetworkAddress;
	}

	public NodeID getPreHash() {
		return preHash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-PREDECESSOR_SUCCESSOR")+ " | preKey: " + preHash.toString() + " preAddr: (" + preNetworkAddress.toString() + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 4 + NodeID.ADDRESS_SIZE;
	}}
