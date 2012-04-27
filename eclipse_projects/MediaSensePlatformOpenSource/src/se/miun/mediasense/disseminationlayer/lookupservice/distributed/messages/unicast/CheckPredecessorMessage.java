package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;

public class CheckPredecessorMessage extends Message {
	private NodeID hash;
	
	public CheckPredecessorMessage(String from,String to,NodeID hash) {
		super(from,to,CHECK_PREDECESSOR);
		this.hash = hash;
	}
	
	public NodeID getHash() {
		return hash;
	}

	public String toString() {
		//Return message info
		return super.toString("MSG-CHECK_PREDECESSOR")+ " | hash: " + hash.toString(); 
	}
	
	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE;
	}
	
	@Override
	public byte[] toByteArray() {
		byte[] array = super.toByteArray();
		byte[] result = new byte[array.length + NodeID.ADDRESS_SIZE];
		
		System.arraycopy(hash.getID(), 0, result, 1, NodeID.ADDRESS_SIZE);
		return result;
	}
}
