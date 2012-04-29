package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class CheckSuccessorResponseMessage extends Message {
	private String sucNetworkAddress;
	private NodeID sucHash;
	
	public CheckSuccessorResponseMessage(String from,String to,String preNetworkAddress,NodeID preHash) {
		super(from,to,CHECK_SUCCESSOR_RESPONSE);
		this.sucNetworkAddress = preNetworkAddress;
		this.sucHash = preHash;
	}

	public String getSucNetworkAddress() {
		return sucNetworkAddress;
	}

	public NodeID getSucHash() {
		return sucHash;
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-CHECK_SUCCESSOR_RESPONSE")+ " | preHash: " + sucHash.toString() + " preAddr: (" + sucNetworkAddress.toString() + ")"; 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 4 + NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(sucHash.getID());
			oos.writeUTF(sucNetworkAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			String sna = ois.readUTF();
			
			return new CheckSuccessorResponseMessage(fromIp,toIp,sna,new NodeID(hash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
