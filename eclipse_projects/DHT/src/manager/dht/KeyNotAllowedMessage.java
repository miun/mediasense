package manager.dht;

import manager.Message;

public class KeyNotAllowedMessage extends Message{
	NodeID key;
	public KeyNotAllowedMessage(String fromIp, String toIp, NodeID key) {
		this.type = Message.KEYNOTALLOWED;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.key = key;
	}
	
	public String toString() {
		//Return message info
		return "MSG-KEYNOTALLOWED| from:{" + fromIp + "} - to:{" + toIp + "} - key:{" + SHA1Generator.convertToHex(key.getID()) + "}"; 
	}
}
