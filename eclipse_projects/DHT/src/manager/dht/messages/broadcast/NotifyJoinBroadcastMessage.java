package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.messages.unicast.NotifyJoinMessage;

public class NotifyJoinBroadcastMessage extends BroadcastMessage {
	private String networkAddress;
	private NodeID hash;
	
	public NotifyJoinBroadcastMessage(String from,String to,NodeID startKey,NodeID endKey,String networkAddress,NodeID hash) {
		super(from,to,startKey,endKey,Message.NODE_JOIN_NOTIFY);
		this.networkAddress = networkAddress;
		this.hash = hash;
	}

	@Override
	public Message extractMessage() {
		return new NotifyJoinMessage(this.getFromIp(),this.getToIp(),networkAddress,hash);
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		return new NotifyJoinBroadcastMessage(from,to,startKey,endKey,networkAddress,hash);
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
