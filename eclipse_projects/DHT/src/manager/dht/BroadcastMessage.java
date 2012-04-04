package manager.dht;

import manager.Message;

public abstract class BroadcastMessage extends Message {
	//Type is always BRAODCAST
	public final int type = Message.BROADCAST;
	
	//Type of the delivered message
	protected int internalType = Message.UNKNOWN;
	
	//Limit counter for spanning tree
	private int TTL;
	
	public int getTTL() {
		return TTL;
	}
	
	public BroadcastMessage(int TTL) {
		this.TTL = TTL;
	}
	
	protected String toString(String type) {
		return super.toString("BROADCAST-" + type);
	}
}
