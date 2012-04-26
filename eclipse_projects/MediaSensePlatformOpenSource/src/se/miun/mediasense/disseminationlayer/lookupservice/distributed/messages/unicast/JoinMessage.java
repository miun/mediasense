package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class JoinMessage extends Message {
	private NodeID key;
	private String originatorAddress;
	
	public JoinMessage(String fromIp, String toIp, String originatorAddress, NodeID key) {
		super(fromIp,toIp,Message.JOIN);
		this.originatorAddress = originatorAddress;
		this.key = key;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN") + " key: " + key.toString() + " - origAdr: (" + originatorAddress + ")"; 
	}
	
	public NodeID getKey() {
		return key;
	}
	
	public String getOriginatorAddress() {
		return originatorAddress;
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
