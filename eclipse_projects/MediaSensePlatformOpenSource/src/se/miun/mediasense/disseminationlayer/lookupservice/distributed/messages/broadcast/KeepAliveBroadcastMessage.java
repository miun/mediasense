package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.KeepAliveMessage;

public class KeepAliveBroadcastMessage extends BroadcastMessage {
	private String advertisedNetworkAddress;
	private NodeID advertisedID;
	
	public KeepAliveBroadcastMessage(String fromIp,String toIp,NodeID startKey,NodeID endKey,NodeID advertisedID,String advertisedNetworkAddress) {
		super(fromIp,toIp,startKey,endKey,Message.KEEPALIVE);
		this.advertisedID = advertisedID;
		this.advertisedNetworkAddress = advertisedNetworkAddress;
	}

	@Override
	public Message extractMessage() {
		return new KeepAliveMessage(this.getFromIp(),this.getToIp(),advertisedID,advertisedNetworkAddress);
	}
	
	@Override
	public String toString() {
		return super.toString(extractMessage().toString());
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		return new KeepAliveBroadcastMessage(from,to,startKey,endKey,advertisedID,advertisedNetworkAddress);
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount() + NodeID.ADDRESS_SIZE + 4;
	}
	
	@Override
	public void serializeMessage(ObjectOutputStream oos) {
		try {
			super.serializeMessage(oos);
			oos.write(advertisedID.getID());
			oos.writeUTF(advertisedNetworkAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp,NodeID startKey,NodeID endKey) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			String advertisedAddr = ois.readUTF();
			
			return new KeepAliveBroadcastMessage(fromIp,toIp,startKey,endKey,new NodeID(hash),advertisedAddr);
		}
		catch (IOException e) {
			return null;
		}
	}
}
