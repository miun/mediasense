package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class DuplicateNodeIdMessage extends Message{
	//Specify the duplicate key in the DHT
	private NodeID duplicateKey;
	
	public DuplicateNodeIdMessage(String fromIp, String toIp,NodeID duplicateKey) {
		super(fromIp,toIp,Message.DUPLICATE_NODE_ID);
		this.duplicateKey = duplicateKey;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-DUPLICATE-NODE-ID")+ " | joinKey: " + duplicateKey.toString(); 
	}
	
	public NodeID getDuplicateKey() {
		return duplicateKey;
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
}
