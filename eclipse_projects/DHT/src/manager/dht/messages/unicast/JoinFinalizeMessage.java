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
	
	@Override
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public String toString() {
		return super.toString("MSG-JOIN_FINALIZE") + " key: " + joinKey.toString(); 
	}
}
