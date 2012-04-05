package manager.dht.messages.broadcast;

import manager.Message;

public class NotifyLeaveBroadcastMessage extends BroadcastMessage {
	public NotifyLeaveBroadcastMessage() {
		super(Message.NODE_LEAVE_NOTIFY);
	}

	@Override
	public Message extractMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
