package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class FindPredecessorResponseMessage extends Message {
	private NodeID preHash;
	private NodeID origHash;
	
	public FindPredecessorResponseMessage(String from,String to,NodeID preHash,NodeID origHash) {
		super(from,to,Message.FIND_PREDECESSOR_RESPONSE);
		this.preHash = preHash;
		this.origHash = origHash;
	}

	public NodeID getPredecessorHash() {
		return preHash;
	}
	
	public NodeID getOrigHash() {
		return origHash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-PREDECESSOR_RESPONSE") + " | predecessorKey: " + preHash.toString() + ") origHash: " + origHash.toString(); 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE;
	}
}
