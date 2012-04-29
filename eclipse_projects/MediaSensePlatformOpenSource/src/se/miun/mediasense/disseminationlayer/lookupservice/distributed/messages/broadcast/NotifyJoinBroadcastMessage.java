package se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyJoinMessage;

public class NotifyJoinBroadcastMessage extends BroadcastMessage {
	private String networkAddress;
	private NodeID hash;
	
	public NotifyJoinBroadcastMessage(String from,String to,NodeID startKey,NodeID endKey,String networkAddress,NodeID hash) {
		super(from,to,startKey,endKey,Message.NODE_JOIN_NOTIFY);
		this.networkAddress = networkAddress;
		this.hash = hash;
	}

	@Override
	public Message extractMessage() {
		return new NotifyJoinMessage(this.getFromIp(),this.getToIp(),networkAddress,hash);
	}

	@Override
	public BroadcastMessage cloneWithNewAddresses(String from, String to,NodeID startKey,NodeID endKey) {
		return new NotifyJoinBroadcastMessage(from,to,startKey,endKey,networkAddress,hash);
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
			oos.writeUTF(networkAddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Message deserializeMessage(ObjectInputStream ois,String fromIp,String toIp,NodeID startKey,NodeID endKey) {
		try {
			byte[] hash = new byte[NodeID.ADDRESS_SIZE];
			ois.readFully(hash, 0, NodeID.ADDRESS_SIZE);
			
			String networkAddr = ois.readUTF();
			
			return new NotifyJoinBroadcastMessage(fromIp,toIp,startKey,endKey,networkAddr,new NodeID(hash));
		}
		catch (IOException e) {
			return null;
		}
	}
}
