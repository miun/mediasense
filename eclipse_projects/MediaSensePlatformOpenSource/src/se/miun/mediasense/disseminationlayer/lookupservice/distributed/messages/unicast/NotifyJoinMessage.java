package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class NotifyJoinMessage extends Message {
	private String networkAddress;
	private NodeID hash;

	public NotifyJoinMessage(String from, String to, String networkAddress,
			NodeID hash) {
		super(from, to, Message.NODE_JOIN_NOTIFY);
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
		// Return message info
		return super.toString("MSG-JOIN-NOTIFY") + " hash: " + hash.toString()
				+ " - Adr: (" + networkAddress + ")";
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(hash.getID());
			oos.writeUTF(networkAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);

			String networkAddr = ois.readUTF();

			return new NotifyJoinMessage(fromIp, toIp, networkAddr, new NodeID(
					hash));
		} catch (IOException e) {
			return null;
		}
	}
}
