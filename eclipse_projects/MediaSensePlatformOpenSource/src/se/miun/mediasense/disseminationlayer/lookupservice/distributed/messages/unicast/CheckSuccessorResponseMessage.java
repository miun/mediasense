package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class CheckSuccessorResponseMessage extends Message {
	private String sucNetworkAddress;
	private NodeID sucHash;
	
	public CheckSuccessorResponseMessage(String from,String to,String preNetworkAddress,NodeID preHash) {
		super(from,to,CHECK_SUCCESSOR_RESPONSE);
		this.sucNetworkAddress = preNetworkAddress;
		this.sucHash = preHash;
	}

	public String getSucNetworkAddress() {
		return sucNetworkAddress;
	}

	public NodeID getSucHash() {
		return sucHash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-CHECK_SUCCESSOR_RESPONSE")+ " | preHash: " + sucHash.toString() + " preAddr: (" + sucNetworkAddress.toString() + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 4 + NodeID.ADDRESS_SIZE;
	}
}
