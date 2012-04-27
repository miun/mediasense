package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyLeaveMessage;

public class NotifyLeaveBroadcastMessage extends BroadcastMessage {
	private NodeID hash;
	private NodeID successorHash;
	private String successorNetworkAddress;
	
	public NotifyLeaveBroadcastMessage(String from,String to,NodeID startKey,NodeID endKey,NodeID hash,NodeID successorHash,String successorNetworkAddress) {
		super(from,to,startKey,endKey,Message.NODE_LEAVE_NOTIFY);
		this.hash = hash;
		this.successorHash = successorHash;
		this.successorNetworkAddress = successorNetworkAddress;
	}

	@Override
	public Message extractMessage() {
		return new NotifyLeaveMessage(this.getFromIp(),this.getToIp(),hash,successorHash,successorNetworkAddress);
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		return new NotifyLeaveBroadcastMessage(from,to,startKey,endKey,hash,successorHash,successorNetworkAddress);
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE + 4;
	}
}
