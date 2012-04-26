package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class KeepAliveMessage extends Message {
	private String advertisedNetworkAddress;
	private NodeID advertisedID;

	public KeepAliveMessage(String from,String to,NodeID advertisedID,String advertisedNetworkAddress) {
		super(from,to,Message.KEEPALIVE);
		this.advertisedID = advertisedID;
		this.advertisedNetworkAddress = advertisedNetworkAddress;
	}
	
	public String toString() {
		return super.toString("KEEP-ALIVE");
	}

	public String getAdvertisedNetworkAddress() {
		return advertisedNetworkAddress;
	}

	public NodeID getAdvertisedID() {
		return advertisedID;
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
