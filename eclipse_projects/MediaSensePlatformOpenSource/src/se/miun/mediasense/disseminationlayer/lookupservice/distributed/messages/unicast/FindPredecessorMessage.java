package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class FindPredecessorMessage extends Message {
	private String origAddress;
	private NodeID hash;
	
	public FindPredecessorMessage(String from,String to,NodeID hash,String origAddress) {
		super(from,to,Message.FIND_PREDECESSOR);
		this.hash = hash;
		this.origAddress = origAddress;
	}
	
	public NodeID getHash() {
		return hash;
	}
	
	public String getOrigAddress() {
		return origAddress;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-FIND_PREDECESSOR")+ " | findKey: " + hash.toString() + " origAddr: (" + origAddress + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
