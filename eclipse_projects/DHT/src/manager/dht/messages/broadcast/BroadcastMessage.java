package manager.dht.messages.broadcast;

import manager.Message;

public abstract class BroadcastMessage extends Message {
	//Type is always BRAODCAST
	public final int type = Message.BROADCAST;
	
	//Limit counter for spanning tree
	private int TTL;

	//Type of the delivered message
	protected int internalType;
	
	public int getTTL() {
		return TTL;
	}
	
	public void setTTL(int TTL) {
		this.TTL = TTL;
	}
	
	public BroadcastMessage(int TTL,int internalType) {
		this.TTL = TTL;
		this.internalType = internalType;
	}
	
	protected String toString(String text) {
		return super.toString("BROADCAST-" + " | " + text);
	}
	
	//Extract unicast message
	public abstract Message extractMessage();
}
