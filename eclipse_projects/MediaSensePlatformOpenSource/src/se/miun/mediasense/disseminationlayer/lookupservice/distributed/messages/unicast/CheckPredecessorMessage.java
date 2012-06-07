package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class CheckPredecessorMessage extends Message {
	private NodeID hash;

	public CheckPredecessorMessage(String from, String to, NodeID hash) {
		super(from, to, CHECK_PREDECESSOR);
		this.hash = hash;
	}

	public NodeID getHash() {
		return hash;
	}

	public String toString() {
		// Return message info
		return super.toString("MSG-CHECK_PREDECESSOR") + " | hash: "
				+ hash.toString();
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(hash.getID());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);

			return new CheckPredecessorMessage(fromIp, toIp, new NodeID(hash));
		} catch (IOException e) {
			return null;
		}
	}
}
