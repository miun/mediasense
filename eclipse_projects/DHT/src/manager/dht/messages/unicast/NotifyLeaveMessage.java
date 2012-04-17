package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class NotifyLeaveMessage extends Message {
	private NodeID hash;
	private NodeID successorHash;

	private String networkAddress;
	private String successorNetworkAddress;
	
	NotifyLeaveMessage(String from,String to,NodeID hash,String networkAddress,NodeID successorHash,String successorNetworkAddress) {
		super(from,to,Message.NODE_LEAVE_NOTIFY);
		this.hash = hash;
		this.successorHash = successorHash;
		this.networkAddress = networkAddress;
		this.successorNetworkAddress = successorNetworkAddress;
	}

	public String getNetworkAddress() {
		return networkAddress;
	}

	public NodeID getSuccessorHash() {
		return successorHash;
	}

	public String getSuccessorNetworkAddress() {
		return successorNetworkAddress;
	}

	public NodeID getHash() {
		return hash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN-LEAVE") + " hash: " + hash.toString() + " - Adr: (" + networkAddress + ")"; 
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE + 2 * 4;
	}
}
