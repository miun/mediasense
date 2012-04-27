package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class ResolveResponseMessage extends Message {
	NodeID sensor;
	String sensorAddress;
	
	public ResolveResponseMessage(String from, String to, NodeID sensor,String sensorAddress) {
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
		//Return message info
		return super.toString("MSG-RESOLVE_RESPONSE") + " sensor: " + sensor + " - sensorAddr: (" + sensorAddress + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
}
