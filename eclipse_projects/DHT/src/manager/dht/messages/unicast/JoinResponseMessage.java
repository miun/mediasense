package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class JoinResponseMessage extends Message {
	//Send the own key to prevent exploits and duplicate entries
	private NodeID joinKey;

	//NodeID and network address of the successor
	private NodeID successor;
	private String successorAddress;
	private NodeID predecessor;

	//TODO Which information could be already provided in a JoinResponse
	public JoinResponseMessage(String fromIp, String toIp,NodeID joinKey,String successorAddress,NodeID successor,NodeID predecessor) {
		super(fromIp,toIp,Message.JOIN_RESPONSE);
		this.joinKey = joinKey;
		this.successor = successor;
		this.successorAddress = successorAddress;
		this.predecessor = predecessor;
	}

	public String toString() {
		return super.toString("MSG-JOIN-RESPONSE") + " | joinKey: " + joinKey.toString() + " suc: " + successor.toString() + " pre : " + predecessor.toString();
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
	
	public NodeID getPredecessor() {
		return predecessor;
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 3 * NodeID.ADDRESS_SIZE + 4;
	}
}
