package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.messages.unicast.KeepAliveMessage;

public class KeepAliveBroadcastMessage extends BroadcastMessage {
	private String advertisedNetworkAddress;
	private NodeID advertisedID;
	
	public KeepAliveBroadcastMessage(NodeID advertisedID,String advertisedNetworkAddress) {
		super(Message.KEEPALIVE);
		this.advertisedID = advertisedID;
		this.advertisedNetworkAddress = advertisedNetworkAddress;
	}

	@Override
	public Message extractMessage() {
		return new KeepAliveMessage(this.fromIp,this.toIp,advertisedID,advertisedNetworkAddress);
	}
	
	@Override
	public String toString() {
		return super.toString(extractMessage().toString());
	}
}
