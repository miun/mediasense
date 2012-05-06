package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class KeepAliveMessage extends Message {
	private NodeID advertisedID;
	private String advertisedNetworkAddress;

	public KeepAliveMessage(String from, String to, NodeID advertisedID,
			String advertisedNetworkAddress) {
		super(from, to, Message.KEEPALIVE);
		this.advertisedID = advertisedID;
		this.advertisedNetworkAddress = advertisedNetworkAddress;
	}

	public String toString() {
		return super.toString("KEEP-ALIVE");
	}

	public String getAdvertisedNetworkAddress() {
		return advertisedNetworkAddress;
	}

	public NodeID getAdvertisedID() {
		return advertisedID;
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(advertisedID.getID());
			oos.writeUTF(advertisedNetworkAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);

			String advertisedAddr = ois.readUTF();

			return new KeepAliveMessage(fromIp, toIp, new NodeID(hash),
					advertisedAddr);
		} catch (IOException e) {
			return null;
		}
	}
}
