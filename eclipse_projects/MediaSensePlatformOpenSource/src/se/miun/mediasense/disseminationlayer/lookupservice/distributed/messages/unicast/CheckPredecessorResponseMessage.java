package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class CheckPredecessorResponseMessage extends Message {
	private String preNetworkAddress;
	private NodeID preHash;
	
	public CheckPredecessorResponseMessage(String from,String to,String preNetworkAddress,NodeID preHash) {
		super(from,to,CHECK_PREDECESSOR_RESPONSE);
		this.preNetworkAddress = preNetworkAddress;
		this.preHash = preHash;
	}

	public String getPreNetworkAddress() {
		return preNetworkAddress;
	}

	public NodeID getPreHash() {
		return preHash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-CHECK_PREDECESSOR_RESPONSE")+ " | preHash: " + preHash.toString() + " preAddr: (" + preNetworkAddress.toString() + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 4 + NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(preHash.getID());
			oos.writeUTF(preNetworkAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			String pna = ois.readUTF();
			
			return new CheckPredecessorResponseMessage(fromIp,toIp,pna,new NodeID(hash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
