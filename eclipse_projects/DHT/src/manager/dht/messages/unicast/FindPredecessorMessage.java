package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class FindPredecessorMessage extends Message {
	private NodeID hash;
	
	public FindPredecessorMessage(String from,String to,String networkAddress,NodeID hash) {
		super(from,to,Message.FIND_PREDECESSOR);
		this.hash = hash;
	}
}
