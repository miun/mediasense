package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public abstract class BroadcastMessage extends Message {
	
	//Definition of the region the broadcast is responsible for
	private NodeID startKey;
	private NodeID endKey;
	
	public BroadcastMessage(String from, String to,NodeID startKey,NodeID endKey,int internalType) {
		super(from,to,Message.BROADCAST);
		this.startKey = startKey;
		this.endKey = endKey;
	}
	
	public NodeID getStartKey() {
		return startKey;
	}

	public NodeID getEndKey() {
		return endKey;
	}

	protected String toString(String text) {
		return "BROADCAST:" + startKey.toString() + " -> " + endKey.toString() + " | " + extractMessage().toString();
	}
	
	//Extract unicast message
	public abstract Message extractMessage();
	
	//Clone with new addresses to easily forward messages
	public abstract BroadcastMessage cloneWithNewAddresses(String from,String to,NodeID startKey,NodeID endKey);

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE;
	}
}
