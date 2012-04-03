package manager.dht;

import manager.Message;

public class KeyNotAllowedMessage extends Message{

	public KeyNotAllowedMessage(String fromIp, String toIp) {
		this.type = Message.KEYNOTALLOWED;
		this.fromIp = fromIp;
		this.toIp = toIp;
	}
	
	public String toString() {
		//Return message info
		return "MSG-KEYNOTALLOWED| from:{" + fromIp + "} - to:{" + toIp + "}"; 
	}
}
