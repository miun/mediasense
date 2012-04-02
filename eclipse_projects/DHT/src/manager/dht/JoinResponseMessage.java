package manager.dht;

import manager.Message;

public class JoinResponseMessage extends Message {
	//Maybe send a new key if that one is not free?!
	//byte[] key;
	
	//TODO Which information could be already provided in a JoinResponse
	public JoinResponseMessage(String fromIp, String toIp) {
		this.type = Message.JOIN_RESPONSE;
		this.fromIp = fromIp;
		this.toIp = toIp;
	}
}
