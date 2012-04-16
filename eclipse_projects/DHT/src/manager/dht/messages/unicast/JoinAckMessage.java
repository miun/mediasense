package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class JoinAckMessage extends Message {
	private NodeID joinKey;
	
	public JoinAckMessage(String from, String to,NodeID joinKey) {
		super(from, to, Message.JOIN_ACK);
		this.joinKey = joinKey;
	}
	
	public NodeID getJoinKey() {
		return joinKey;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN_ACK") + " key: " + joinKey.toString(); 
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
}
