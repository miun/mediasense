package manager.dht.messages;

import manager.Message;

public class KeepAliveBroadcastMessage extends BroadcastMessage {

	public KeepAliveBroadcastMessage(int TTL) {
		super(TTL, Message.KEEPALIVE);
	}

	@Override
	public Message extractMessage() {
		return new KeepAliveMessage(this.fromIp,this.toIp);
	}
	
	@Override
	public String toString() {
		return super.toString(extractMessage().toString());
	}
}
