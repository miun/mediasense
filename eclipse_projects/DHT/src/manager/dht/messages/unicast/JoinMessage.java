package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class JoinMessage extends Message {
	private NodeID key;
	private String originatorAddress;
	
	public JoinMessage(String fromIp, String toIp, String originatorAddress, NodeID key) {
		super(fromIp,toIp,Message.JOIN);
		this.originatorAddress = originatorAddress;
		this.key = key;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN") + " key: " + key.toString() + " - origAdr: (" + originatorAddress + ")"; 
	}
	
	public NodeID getKey() {
		return key;
	}
	
	public String getOriginatorAddress() {
		return originatorAddress;
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
