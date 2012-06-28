package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class JoinMessage extends Message {
	private NodeID key;
	private String originatorAddress;

	public JoinMessage(String fromIp, String toIp, String originatorAddress,
			NodeID key) {
		super(fromIp, toIp, Message.JOIN);
		this.originatorAddress = originatorAddress;
		this.key = key;
	}

	public String toString() {
		// Return message info
		return super.toString("MSG-JOIN") + " key: " + key.toString()
				+ " - origAdr: (" + originatorAddress + ")";
	}

	public NodeID getKey() {
		return key;
	}

	public String getOriginatorAddress() {
		return originatorAddress;
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(key.getID());
			oos.writeUTF(originatorAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);

			String origAddr = ois.readUTF();

			return new JoinMessage(fromIp, toIp, origAddr, new NodeID(hash));
		} catch (IOException e) {
			return null;
		}
	}
}
