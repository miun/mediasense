package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class RegisterMessage extends Message {
	private NodeID sensor;
	private NodeID origHash;
	private String origAddress;
	
	public RegisterMessage(String from, String to,NodeID sensor,NodeID origHash,String origAddress) {
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
	
	public RegisterMessage cloneWithNewAddress(String from,String to) {
		return new RegisterMessage(from,to,sensor,origHash,origAddress);
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-REGISTER") + " sensor: " + sensor + " - origAddr: (" + origAddress + ") - origHash: " + origHash; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE + 4;
	}
}
