package manager.dht;

import manager.Message;

public class KeepAliveBroadCastMessage extends BroadcastMessage {
	public KeepAliveBroadCastMessage(int TTL) {
		super(TTL);
	}

	//Does not contain any additional information
	protected int internalType = Message.KEEPALIVE;
	
	public String toString() {
		return super.toString("KEEP-ALIVE");
	}
}
