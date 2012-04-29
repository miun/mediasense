package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class JoinFinalizeMessage extends Message {
	//Finalizing node hash key
	private NodeID joinKey;
	
	public NodeID getJoinKey() {
		return joinKey;
	}

	public JoinFinalizeMessage(String from, String to, NodeID joinKey) {
		super(from, to, JOIN_FINALIZE);
		this.joinKey = joinKey;
	}
	
	@Override
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public String toString() {
		return super.toString("MSG-JOIN_FINALIZE") + " key: " + joinKey.toString(); 
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(joinKey.getID());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] key = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(key, 0, NodeID.ADDRESS_SIZE);
			
			return new JoinFinalizeMessage(fromIp,toIp,new NodeID(key));
		}
		catch (IOException e) {
			return null;
		}
	}
}
