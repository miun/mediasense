package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

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
}
