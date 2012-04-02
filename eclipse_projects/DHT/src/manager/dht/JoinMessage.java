package manager.dht;

import manager.Message;

public class JoinMessage extends Message {
	byte[] key;
	public JoinMessage(String fromIp, String toIp, byte[] key) {
		this.type = Message.JOIN;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.key = key;
	}
}
