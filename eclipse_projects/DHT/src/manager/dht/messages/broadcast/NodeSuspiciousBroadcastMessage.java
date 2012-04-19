package manager.dht.messages.broadcast;

import manager.Message;
import manager.dht.NodeID;

public class NodeSuspiciousBroadcastMessage extends BroadcastMessage {

	public NodeSuspiciousBroadcastMessage(String from, String to,
			NodeID startKey, NodeID endKey, int internalType) {
		super(from, to, startKey, endKey, internalType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Message extractMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,
			NodeID startKey, NodeID endKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
