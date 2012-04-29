package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class FindPredecessorResponseMessage extends Message {
	private NodeID preHash;
	private NodeID origHash;
	
	public FindPredecessorResponseMessage(String from,String to,NodeID preHash,NodeID origHash) {
		super(from,to,Message.FIND_PREDECESSOR_RESPONSE);
		this.preHash = preHash;
		this.origHash = origHash;
	}

	public NodeID getPredecessorHash() {
		return preHash;
	}
	
	public NodeID getOrigHash() {
		return origHash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-PREDECESSOR_RESPONSE") + " | predecessorKey: " + preHash.toString() + ") origHash: " + origHash.toString(); 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE;
	}

	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(preHash.getID());
			oos.write(origHash.getID());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] preHash = new byte[NodeID.ADDRESS_SIZE];
			byte[] origHash = new byte[NodeID.ADDRESS_SIZE];

			ois.readFully(preHash, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(origHash, 0, NodeID.ADDRESS_SIZE);
			
			return new FindPredecessorResponseMessage(fromIp,toIp,new NodeID(preHash),new NodeID(origHash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
