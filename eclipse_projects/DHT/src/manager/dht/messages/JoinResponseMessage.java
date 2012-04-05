package manager.dht.messages;

import manager.Message;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;

public class JoinResponseMessage extends Message {
	//Send the own key to prevent exploits and duplicate entries
	private NodeID joinKey;

	//NodeID and network address of the successor
	private NodeID successor;
	private String successorAddress;
	
	//TODO Which information could be already provided in a JoinResponse
	public JoinResponseMessage(String fromIp, String toIp,NodeID joinKey,String successorAddress,NodeID successor) {
		super(fromIp,toIp,Message.JOIN_RESPONSE);
		this.joinKey = joinKey;
		this.successor = successor;
		this.successorAddress = successorAddress;
	}

	public String toString() {
		return super.toString("MSG-JOIN-RESPONSE") + " | joinKey:{" + SHA1Generator.convertToHex(joinKey.getID()) + "} successor:{" + SHA1Generator.convertToHex(successor.getID()) + "}";
	}
	
	public NodeID getJoinKey() {
		return this.joinKey;
	}

	public NodeID getSuccessor() {
		return this.successor;
	}
	
	public String getSuccessorAddress() {
		return successorAddress;
	}
}
