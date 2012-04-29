package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

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

	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(joinKey.getID());
			oos.write(successor.getID());
			oos.write(predecessor.getID());
			oos.writeUTF(successorAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] joinKey = new byte[NodeID.ADDRESS_SIZE];
			byte[] successor = new byte[NodeID.ADDRESS_SIZE];
			byte[] predecessor = new byte[NodeID.ADDRESS_SIZE];
			String successorAddress;

			ois.readFully(joinKey, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(successor, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(predecessor, 0, NodeID.ADDRESS_SIZE);
			successorAddress = ois.readUTF();
			
			return new JoinResponseMessage(fromIp,toIp,new NodeID(joinKey),successorAddress,new NodeID(successor),new NodeID(predecessor));
		}
		catch (IOException e) {
			return null;
		}
	}
}
