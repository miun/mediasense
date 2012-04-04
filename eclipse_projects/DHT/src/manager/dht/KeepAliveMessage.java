package manager.dht;

import manager.Message;

public class KeepAliveMessage extends Message {
	public KeepAliveMessage(String from,String to) {
		this.fromIp = from;
		this.toIp = to;
		this.type = Message.KEEPALIVE;
	}
	
	public String toString() {
		return super.toString("KEEP-ALIVE");
	}
}
