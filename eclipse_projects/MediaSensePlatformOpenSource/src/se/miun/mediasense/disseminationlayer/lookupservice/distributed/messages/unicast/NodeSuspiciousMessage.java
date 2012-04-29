package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class NodeSuspiciousMessage extends Message {
	private NodeID hash;
	
	public NodeSuspiciousMessage(String from, String to, NodeID hash) {
		super(from, to, NODE_SUSPICIOUS);
		this.hash = hash;
	}
	
	public NodeID getHash() {
		return hash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-NODE-SUSPICIOUS") + " hash: " + hash.toString() ;
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(hash.getID());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			return new NodeSuspiciousMessage(fromIp,toIp,new NodeID(hash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
