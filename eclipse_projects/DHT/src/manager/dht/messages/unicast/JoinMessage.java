package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;

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
}
