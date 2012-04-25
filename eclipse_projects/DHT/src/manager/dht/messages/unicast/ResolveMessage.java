package manager.dht.messages.unicast;

import manager.Message;
import manager.dht.NodeID;

public class ResolveMessage extends Message {
	private NodeID sensorHash;
	private String origAddress;
	
	public ResolveMessage(String from, String to, NodeID sensorHash,String origAddress) {
		super(from, to, RESOLVE);
		this.sensorHash = sensorHash;
		this.origAddress = origAddress;
	}

	public NodeID getSensorHash() {
		return sensorHash;
	}
	
	public ResolveMessage cloneWithNewAddress(String from,String to) {
		return new ResolveMessage(from, to, sensorHash,origAddress);
	}

	public String getOrigAddress() {
		return origAddress;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-RESOLVE") + " sensor: " + sensorHash; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
}
