package manager.dht.messages.broadcast;

import manager.Message;

public class NotifyJoinBroadcastMessage extends BroadcastMessage {
	public NotifyJoinBroadcastMessage() {
		super(Message.NODE_JOIN_NOTIFY);
	}

	@Override
	public Message extractMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
