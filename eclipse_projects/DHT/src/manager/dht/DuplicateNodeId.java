package manager.dht;

import manager.Message;

public class DuplicateNodeId extends Message{

	public DuplicateNodeId(String fromIp, String toIp) {
		this.type = Message.DUPLICATE_NODE_ID;
		this.fromIp = fromIp;
		this.toIp = toIp;
	}
	
	public String toString() {
		//Return message info
		return "MSG-DUPLICATE_NODE_ID | from:{" + fromIp + "} - to:{" + toIp + "}"; 
	}
}
