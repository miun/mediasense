package manager.dht;

import manager.Message;

public class JoinResponseMessage extends Message {
	//Maybe send a new key if that one is not free?!
	private NodeID successor;
	
	//TODO Which information could be already provided in a JoinResponse
	public JoinResponseMessage(String fromIp, String toIp,NodeID successor) {
		this.type = Message.JOIN_RESPONSE;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.successor = successor;
	}

	public String toString() {
		//Return message info
		return "MSG-JOIN-RESPONSE | from:{" + fromIp + "} - to:{" + toIp + "} - key:{" + SHA1Generator.convertToHex(successor.getID()) + "}"; 
	}
	
	public NodeID getNodeID() {
		return this.successor;
	}
}
