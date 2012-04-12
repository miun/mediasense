package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;

public class NotifyJoinMessage extends Message {
	private String networkAddress;
	private NodeID hash;
	
	public NotifyJoinMessage(String from,String to,String networkAddress,NodeID hash) {
		super(from,to,Message.NODE_JOIN_NOTIFY);
		this.networkAddress = networkAddress;
		this.hash = hash;
	}

	public String getNetworkAddress() {
		return networkAddress;
	}

	public NodeID getHash() {
		return hash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN-NOTIFY") + " hash: " + hash.toString() + " - Adr: (" + networkAddress + ")"; 
	}
}
