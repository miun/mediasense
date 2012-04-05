package manager.dht.messages.unicast;

import manager.Message;

public class KeepAliveResponse extends Message {
	public KeepAliveResponse(String from,String to) {
		super(from,to,Message.KEEPALIVE_RESPONSE);
	}
	
	public String toString() {
		return super.toString("KEEP-ALIVE-RESPONSE");
	}
}
