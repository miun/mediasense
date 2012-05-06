package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class DuplicateNodeIdMessage extends Message {
	// Specify the duplicate key in the DHT
	private NodeID duplicateKey;

	public DuplicateNodeIdMessage(String fromIp, String toIp,
			NodeID duplicateKey) {
		super(fromIp, toIp, Message.DUPLICATE_NODE_ID);
		this.duplicateKey = duplicateKey;
	}

	public String toString() {
		// Return message info
		return super.toString("MSG-DUPLICATE-NODE-ID") + " | joinKey: "
				+ duplicateKey.toString();
	}

	public NodeID getDuplicateKey() {
		return duplicateKey;
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(duplicateKey.getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);

			return new DuplicateNodeIdMessage(fromIp, toIp, new NodeID(hash));
		} catch (IOException e) {
			return null;
		}
	}
}
