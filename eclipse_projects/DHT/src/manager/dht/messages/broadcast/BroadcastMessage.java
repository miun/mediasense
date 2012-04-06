package manager.dht.messages.broadcast;

import manager.Message;

public abstract class BroadcastMessage extends Message {
	
	//Limit counter for spanning tree
	private int TTL;

	//Type of the delivered message
	protected int internalType;
	
	public int getTTL() {
		return TTL;
	}
	
	public void setTTL(int TTL) {
		this.type = Message.BROADCAST;
		this.TTL = TTL;
	}
	
	public BroadcastMessage(int internalType) {
		this.internalType = internalType;
	}
	
	protected String toString(String text) {
		return ("BROADCAST-" + " | " + extractMessage().toString());
	}
	
	//Extract unicast message
	public abstract Message extractMessage();
}
