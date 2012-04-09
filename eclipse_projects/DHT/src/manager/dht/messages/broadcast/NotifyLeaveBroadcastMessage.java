package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;

public class NotifyLeaveBroadcastMessage extends BroadcastMessage {
	public NotifyLeaveBroadcastMessage(String from,String to,NodeID startKey,NodeID endKey) {
		super(from,to,startKey,endKey,Message.NODE_LEAVE_NOTIFY);
	}

	@Override
	public Message extractMessage() {
		//TODO return new NotifyLeaveMessage(from,to);
		return null;
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
