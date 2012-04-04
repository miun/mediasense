package manager.dht;

import manager.Message;

public class JoinMessage extends Message {
	private NodeID key;
	private String originatorAddress;
	
	public JoinMessage(String fromIp, String toIp, String originatorAddress, NodeID key) {
		this.type = Message.JOIN;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.originatorAddress = originatorAddress;
		this.key = key;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN") + " key:{" + SHA1Generator.convertToHex(key.getID()) + "} - origAdr:{" + originatorAddress + "}"; 
	}
	
	public NodeID getKey() {
		return key;
	}
	
	public String getOriginatorAddress() {
		return originatorAddress;
	}
}
