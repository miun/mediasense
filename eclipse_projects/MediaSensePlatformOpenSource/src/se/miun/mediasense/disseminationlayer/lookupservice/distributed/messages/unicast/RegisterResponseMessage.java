package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

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
}
