package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class JoinAckMessage extends Message {
	private NodeID joinKey;
	
	public JoinAckMessage(String from, String to,NodeID joinKey) {
		super(from, to, Message.JOIN_ACK);
		this.joinKey = joinKey;
	}
	
	public NodeID getJoinKey() {
		return joinKey;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN_ACK") + " key: " + joinKey.toString(); 
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
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
			
			return new JoinAckMessage(fromIp,toIp,new NodeID(key));
		}
		catch (IOException e) {
			return null;
		}
	}
}
