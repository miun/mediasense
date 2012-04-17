package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class JoinFinalizeMessage extends Message {
	//Finalizing node hash key
	private NodeID joinKey;
	
	public NodeID getJoinKey() {
		return joinKey;
	}

	public JoinFinalizeMessage(String from, String to, NodeID joinKey) {
		super(from, to, JOIN_FINALIZE);
		this.joinKey = joinKey;
	}

}
