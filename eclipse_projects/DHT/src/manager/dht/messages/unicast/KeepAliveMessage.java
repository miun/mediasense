package manager.dht.messages.unicast;

import manager.Message;

public class KeepAliveMessage extends Message {
	public KeepAliveMessage(String from,String to) {
		super(from,to,Message.KEEPALIVE);
	}
	
	public String toString() {
		return super.toString("KEEP-ALIVE");
	}
}
