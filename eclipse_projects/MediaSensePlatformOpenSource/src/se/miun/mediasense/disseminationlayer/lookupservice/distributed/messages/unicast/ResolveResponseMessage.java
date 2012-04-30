package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class ResolveResponseMessage extends Message {
	NodeID sensor;
	String sensorAddress;

	public ResolveResponseMessage(String from, String to, NodeID sensor,
			String sensorAddress) {
		super(from, to, RESOLVE_RESPONSE);
		this.sensor = sensor;
		this.sensorAddress = sensorAddress;
	}

	public NodeID getSensor() {
		return sensor;
	}

	public String getSensorAddress() {
		return sensorAddress;
	}

	public String toString() {
		// Return message info
		return super.toString("MSG-RESOLVE_RESPONSE") + " sensor: " + sensor
				+ " - sensorAddr: (" + sensorAddress + ")";
	}

	// Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(sensor.getID());
			oos.writeUTF(sensorAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,
			String fromIp, String toIp) {
		try {
			byte[] sensor = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(sensor, 0, NodeID.ADDRESS_SIZE);

			String sensorAddress = ois.readUTF();

			return new ResolveResponseMessage(fromIp, toIp, new NodeID(sensor),
					sensorAddress);
		} catch (IOException e) {
			return null;
		}
	}
}
