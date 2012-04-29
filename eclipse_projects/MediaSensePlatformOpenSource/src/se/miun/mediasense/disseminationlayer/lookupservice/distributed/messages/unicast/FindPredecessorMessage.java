package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class FindPredecessorMessage extends Message {
	private String origAddress;
	private NodeID hash;
	
	public FindPredecessorMessage(String from,String to,NodeID hash,String origAddress) {
		super(from,to,Message.FIND_PREDECESSOR);
		this.hash = hash;
		this.origAddress = origAddress;
	}
	
	public NodeID getHash() {
		return hash;
	}
	
	public String getOrigAddress() {
		return origAddress;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-FIND_PREDECESSOR")+ " | findKey: " + hash.toString() + " origAddr: (" + origAddress + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}

	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(hash.getID());
			oos.writeUTF(origAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			String addr = ois.readUTF();
			
			return new FindPredecessorMessage(fromIp,toIp,new NodeID(hash),addr);
		}
		catch (IOException e) {
			return null;
		}
	}
}
