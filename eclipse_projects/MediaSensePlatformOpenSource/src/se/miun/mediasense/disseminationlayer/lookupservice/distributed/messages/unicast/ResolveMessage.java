package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

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
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(sensorHash.getID());
			oos.writeUTF(origAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] sensorHash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(sensorHash, 0, NodeID.ADDRESS_SIZE);
			
			String origAddress = ois.readUTF();
			
			return new ResolveMessage(fromIp,toIp,new NodeID(sensorHash),origAddress);
		}
		catch (IOException e) {
			return null;
		}
	}
}
