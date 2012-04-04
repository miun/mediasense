package manager.dht;

import manager.Message;

public class KeepAliveResponse extends Message {
	public int type = Message.KEEPALIVE_RESPONSE;
	
	public KeepAliveResponse(String from,String to) {
		//Init addresses
		this.fromIp = from;
		this.toIp = to;
	}
	
	public String toString() {
		return super.toString("KEEP-ALIVE-RESPONSE");
	}
}
