package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.messages.unicast.NotifyJoinMessage;

public class NotifyJoinBroadcastMessage extends BroadcastMessage {
	private String networkAddress;
	private NodeID hash;
	
	public NotifyJoinBroadcastMessage(String networkAddress,NodeID hash) {
		super(Message.NODE_JOIN_NOTIFY);
		this.networkAddress = networkAddress;
		this.hash = hash;
	}

	@Override
	public Message extractMessage() {
		return new NotifyJoinMessage(this.fromIp,this.toIp,networkAddress,hash);
	}
}
