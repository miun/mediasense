package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NodeSuspiciousMessage;

public class NodeSuspiciousBroadcastMessage extends BroadcastMessage {
	private NodeID hash;
	
	public NodeSuspiciousBroadcastMessage(String from, String to,
			NodeID startKey, NodeID endKey, NodeID hash) {
		super(from, to, startKey, endKey, Message.NODE_SUSPICIOUS);
		this.hash = hash;
	}

	@Override
	public Message extractMessage() {
		return new NodeSuspiciousMessage(this.getFromIp(),this.getToIp(),hash);
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,
			NodeID startKey, NodeID endKey) {
		return new NodeSuspiciousBroadcastMessage(from, to, startKey, endKey, hash);
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}

}
