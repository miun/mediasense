package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.KeepAliveMessage;

public class KeepAliveBroadcastMessage extends BroadcastMessage {
	private String advertisedNetworkAddress;
	private NodeID advertisedID;
	
	public KeepAliveBroadcastMessage(String fromIp,String toIp,NodeID startKey,NodeID endKey,NodeID advertisedID,String advertisedNetworkAddress) {
		super(fromIp,toIp,startKey,endKey,Message.KEEPALIVE);
		this.advertisedID = advertisedID;
		this.advertisedNetworkAddress = advertisedNetworkAddress;
	}

	@Override
	public Message extractMessage() {
		return new KeepAliveMessage(this.getFromIp(),this.getToIp(),advertisedID,advertisedNetworkAddress);
	}
	
	@Override
	public String toString() {
		return super.toString(extractMessage().toString());
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		return new KeepAliveBroadcastMessage(from,to,startKey,endKey,advertisedID,advertisedNetworkAddress);
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
