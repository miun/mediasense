package manager.dht;

import manager.Message;

public class JoinMessage extends Message {
	NodeID key;
	public JoinMessage(String fromIp, String toIp, NodeID key) {
		this.type = Message.JOIN;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.key = key;
	}
	
	public String toString() {
		//Return message info
		return "MSG-JOIN | from:{" + fromIp + "} - to:{" + toIp + "} - key:{" + SHA1Generator.convertToHex(key.getID()) + "}"; 
	}
}
