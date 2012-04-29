package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NodeSuspiciousMessage;

public class NodeSuspiciousBroadcastMessage extends BroadcastMessage {
	private NodeID hash;
	
	public NodeSuspiciousBroadcastMessage(String from, String to,
			NodeID startKey, NodeID endKey, NodeID hash) {
		super(from, to, startKey, endKey, Message.NODE_SUSPICIOUS);
		this.hash = hash;
	}

	@Override
	public Message extractMessage() {
		return new NodeSuspiciousMessage(this.getFromIp(),this.getToIp(),hash);
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,
			NodeID startKey, NodeID endKey) {
		return new NodeSuspiciousBroadcastMessage(from, to, startKey, endKey, hash);
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

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp,NodeID startKey,NodeID endKey) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			return new NodeSuspiciousBroadcastMessage(fromIp,toIp,startKey,endKey,new NodeID(hash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
