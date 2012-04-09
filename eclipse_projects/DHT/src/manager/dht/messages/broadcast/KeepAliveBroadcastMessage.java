package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.messages.unicast.KeepAliveMessage;

public class KeepAliveBroadcastMessage extends BroadcastMessage {

	public KeepAliveBroadcastMessage() {
		super(Message.KEEPALIVE);
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
