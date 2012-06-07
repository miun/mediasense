package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class NotifyLeaveMessage extends Message {
	private NodeID hash;
	private NodeID successorHash;
	private String successorNetworkAddress;

	public NotifyLeaveMessage(String from, String to, NodeID hash,
			NodeID successorHash, String successorNetworkAddress) {
		super(from, to, Message.NODE_LEAVE_NOTIFY);
		this.hash = hash;
		this.successorHash = successorHash;
		this.successorNetworkAddress = successorNetworkAddress;
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
		// Return message info
		return super.toString("MSG-JOIN-LEAVE") + " hash: " + hash.toString()
				+ " - sucHash: " + successorHash.toString() + " - SucAddr: ("
				+ successorNetworkAddress + ")";
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(hash.getID());
			oos.write(successorHash.getID());
			oos.writeUTF(successorNetworkAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			byte[] successorHash = new byte[NodeID.ADDRESS_SIZE];

			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(successorHash, 0, NodeID.ADDRESS_SIZE);

			String sna = ois.readUTF();

			return new NotifyLeaveMessage(fromIp, toIp, new NodeID(hash),
					new NodeID(successorHash), sna);
		} catch (IOException e) {
			return null;
		}
	}
}
