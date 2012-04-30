package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyLeaveMessage;

public class NotifyLeaveBroadcastMessage extends BroadcastMessage {
	private NodeID hash;
	private NodeID successorHash;
	private String successorNetworkAddress;
	
	public NotifyLeaveBroadcastMessage(String from,String to,NodeID startKey,NodeID endKey,NodeID hash,NodeID successorHash,String successorNetworkAddress) {
		super(from,to,startKey,endKey,Message.NODE_LEAVE_NOTIFY);
		this.hash = hash;
		this.successorHash = successorHash;
		this.successorNetworkAddress = successorNetworkAddress;
	}

	@Override
	public Message extractMessage() {
		return new NotifyLeaveMessage(this.getFromIp(),this.getToIp(),hash,successorHash,successorNetworkAddress);
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		return new NotifyLeaveBroadcastMessage(from,to,startKey,endKey,hash,successorHash,successorNetworkAddress);
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + 2 * NodeID.ADDRESS_SIZE + 4;
	}

	
	@Override
	public void serializeMessage(DataOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(hash.getID());
			oos.write(successorHash.getID());
			oos.writeUTF(successorNetworkAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(DataInputStream ois,String fromIp,String toIp,NodeID startKey,NodeID endKey) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			byte[] successorHash = new byte[NodeID.ADDRESS_SIZE];

			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			ois.readFully(successorHash, 0, NodeID.ADDRESS_SIZE);
			
			String sna = ois.readUTF();
			
			return new NotifyLeaveBroadcastMessage(fromIp,toIp,startKey,endKey,new NodeID(hash),new NodeID(successorHash),sna);
		}
		catch (IOException e) {
			return null;
		}
	}
}
