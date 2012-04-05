package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class NotifyLeaveMessage extends Message {
	private String networkAddress;
	private NodeID hash;
	
	NotifyLeaveMessage(String from,String to,String networkAddress,NodeID hash) {
		super(from,to,Message.NODE_LEAVE_NOTIFY);
		this.networkAddress = networkAddress;
		this.hash = hash;
	}

	public String getNetworkAddress() {
		return networkAddress;
	}

	public NodeID getHash() {
		return hash;
	}
}
