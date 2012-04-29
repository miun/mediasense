package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class RegisterResponseMessage extends Message {
	private NodeID sensor;
	private NodeID origHash;
	
	public RegisterResponseMessage(String from, String to,NodeID sensor,NodeID origHash) {
		super(from, to, REGISTER_RESPONSE);
		this.sensor = sensor;
		this.origHash = origHash;
	}

	public NodeID getSensor() {
		return sensor;
	}

	public NodeID getOrigHash() {
		return origHash;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-REGISTER_RESPONSE") + " sensor: " + sensor + " - origHash: " + origHash; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(sensor.getID());
			oos.write(origHash.getID());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] sensor = new byte[NodeID.ADDRESS_SIZE];
			byte[] origHash = new byte[NodeID.ADDRESS_SIZE];
			
			ois.readFully(sensor, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(origHash, 0, NodeID.ADDRESS_SIZE);
			
			return new RegisterResponseMessage(fromIp,toIp,new NodeID(sensor),new NodeID(origHash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
