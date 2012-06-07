package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class RegisterMessage extends Message {
	private NodeID sensor;
	private NodeID origHash;
	private String origAddress;

	public RegisterMessage(String from, String to, NodeID sensor,
			NodeID origHash, String origAddress) {
		super(from, to, REGISTER);
		this.sensor = sensor;
		this.origAddress = origAddress;
		this.origHash = origHash;
	}

	public NodeID getSensor() {
		return sensor;
	}

	public NodeID getOrigHash() {
		return origHash;
	}

	public String getOrigAddress() {
		return origAddress;
	}

	public RegisterMessage cloneWithNewAddress(String from, String to) {
		return new RegisterMessage(from, to, sensor, origHash, origAddress);
	}

	public String toString() {
		// Return message info
		return super.toString("MSG-REGISTER") + " sensor: " + sensor
				+ " - origAddr: (" + origAddress + ") - origHash: " + origHash;
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(sensor.getID());
			oos.write(origHash.getID());
			oos.writeUTF(origAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] sensor = new byte[NodeID.ADDRESS_SIZE];
			byte[] origHash = new byte[NodeID.ADDRESS_SIZE];

			ois.readFully(sensor, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(origHash, 0, NodeID.ADDRESS_SIZE);

			String origAddr = ois.readUTF();

			return new RegisterMessage(fromIp, toIp, new NodeID(sensor),
					new NodeID(origHash), origAddr);
		} catch (IOException e) {
			return null;
		}
	}
}
