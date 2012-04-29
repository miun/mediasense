package se.miun.mediasense.disseminationlayer.communication.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import se.miun.mediasense.addinlayer.extensions.publishsubscribe.EndSubscribeMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.NotifySubscribersMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.StartSubscribeMessage;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.MessageSerializer;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast.BroadcastMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckPredecessorMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckPredecessorResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckSuccessorMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckSuccessorResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.FindPredecessorMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.FindPredecessorResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinAckMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinBusyMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NodeSuspiciousMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyJoinMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyLeaveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveResponseMessage;

public class BBinaryMessageSerializer implements MessageSerializer {
	
	@Override
	public byte[] serializeMessage(Message message) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(message);
			oos.flush(); 
			oos.close(); 
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		byte [] data = bos.toByteArray();
		return data;
	}

	@Override
	public Message deserializeMessage(byte[] data) {
		Object obj = null;
		try {
		    ByteArrayInputStream bis = new ByteArrayInputStream (data);
		    ObjectInputStream ois = new ObjectInputStream (bis);
		    obj = ois.readObject();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		if(obj.getClass().equals(GetMessage.class)) {
			return (GetMessage) obj;
		} else if(obj.getClass().equals(SetMessage.class)) {
			return (SetMessage) obj;
		} else if(obj.getClass().equals(NotifyMessage.class)) {
			return (NotifyMessage) obj;
		} else if(obj.getClass().equals(StartSubscribeMessage.class)) {
			return (StartSubscribeMessage) obj;
		} else if(obj.getClass().equals(EndSubscribeMessage.class)) {
			return (EndSubscribeMessage) obj;
		} else if(obj.getClass().equals(NotifySubscribersMessage.class)) {
			return (NotifySubscribersMessage) obj;
		} else if(obj.getClass().equals(RegisterMessage.class)) {
			return (RegisterMessage) obj;
		} else if(obj.getClass().equals(RegisterResponseMessage.class)) {
			return (RegisterResponseMessage) obj;
		} else if(obj.getClass().equals(ResolveMessage.class)) {
			return (ResolveMessage) obj;
		} else if(obj.getClass().equals(ResolveResponseMessage.class)) {
			return (ResolveResponseMessage) obj;
		} else if(obj.getClass().equals(JoinMessage.class)) {
			return (JoinMessage) obj;
		} else if(obj.getClass().equals(JoinResponseMessage.class)) {
			return (JoinResponseMessage) obj;
		} else if(obj.getClass().equals(JoinBusyMessage.class)) {
			return (JoinBusyMessage) obj;
		} else if(obj.getClass().equals(JoinAckMessage.class)) {
			return (JoinAckMessage) obj;
		} else if(obj.getClass().equals(NotifyJoinMessage.class)) {
			return (NotifyJoinMessage) obj;
		} else if(obj.getClass().equals(NotifyLeaveMessage.class)) {
			return (NotifyLeaveMessage) obj;
		} else if(obj.getClass().equals(NodeSuspiciousMessage.class)) {
			return (NodeSuspiciousMessage) obj;
		} else if(obj.getClass().equals(FindPredecessorMessage.class)) {
			return (FindPredecessorMessage) obj;
		} else if(obj.getClass().equals(FindPredecessorResponseMessage.class)) {
			return (FindPredecessorResponseMessage) obj;
		} else if(obj.getClass().equals(CheckPredecessorMessage.class)) {
			return (CheckPredecessorMessage) obj;
		} else if(obj.getClass().equals(CheckPredecessorResponseMessage.class)) {
			return (CheckPredecessorResponseMessage) obj;
		} else if(obj.getClass().equals(CheckSuccessorMessage.class)) {
			return (CheckSuccessorMessage) obj;
		} else if(obj.getClass().equals(CheckSuccessorResponseMessage.class)) {
			return (CheckSuccessorResponseMessage) obj;
		} else if(obj.getClass().equals(BroadcastMessage.class)) {
			return (BroadcastMessage) obj;
		}
		return null;
}

}
