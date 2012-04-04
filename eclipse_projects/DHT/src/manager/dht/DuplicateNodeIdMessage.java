package manager.dht;

import manager.Message;

public class DuplicateNodeIdMessage extends Message{
	//Specify the duplicate key in the DHT
	private NodeID duplicateKey;
	
	public DuplicateNodeIdMessage(String fromIp, String toIp,NodeID duplicateKey) {
		this.type = Message.DUPLICATE_NODE_ID;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.duplicateKey = duplicateKey;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-DUPLICATE-NODE-ID")+ " | joinKey:{" + SHA1Generator.convertToHex(duplicateKey.getID()) + "}"; 
	}
	
	public NodeID getDuplicateKey() {
		return duplicateKey;
	}
}
