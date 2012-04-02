package manager.dht;

import manager.Message;

public class JoinMessage extends Message {
	public JoinMessage(String fromIp, String toIp) {
		this.type = Message.JOIN;
		this.fromIp = fromIp;
		this.toIp = toIp;
	}
}
