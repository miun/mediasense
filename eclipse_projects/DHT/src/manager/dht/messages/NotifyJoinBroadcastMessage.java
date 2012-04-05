package manager.dht.messages;

import manager.Message;

public class NotifyJoinBroadcastMessage extends BroadcastMessage {
	public NotifyJoinBroadcastMessage(int TTL) {
		super(TTL,Message.NODE_JOIN_NOTIFY);
	}

	@Override
	public Message extractMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
