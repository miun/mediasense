package se.miun.mediasense.disseminationlayer.communication.serializer;

import java.io.UnsupportedEncodingException;

import se.miun.mediasense.addinlayer.extensions.publishsubscribe.EndSubscribeMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.NotifySubscribersMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.StartSubscribeMessage;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.MessageSerializer;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.communication.tcpproxy.Base64;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.NodeID;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast.BroadcastMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast.KeepAliveBroadcastMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast.NodeSuspiciousBroadcastMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast.NotifyJoinBroadcastMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.broadcast.NotifyLeaveBroadcastMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckPredecessorMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckPredecessorResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckSuccessorMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.CheckSuccessorResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.DuplicateNodeIdMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.FindPredecessorMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.FindPredecessorResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinAckMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinBusyMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinFinalizeMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.KeepAliveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NodeSuspiciousMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyJoinMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.NotifyLeaveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveResponseMessage;

public class EnterSeparatedMessageSerializer implements MessageSerializer{

	public String serializeMessageToString(Message message) {
		String standard = "" + message.getType() + "\n" + message.getToIp() + "\n" + message.getFromIp() + "\n";
		
		switch (message.getType()) {
		case Message.GET:			
			GetMessage getMsg = (GetMessage) message;
			return "" + getMsg.getType() + "\n" + getMsg.getToIp() + "\n" + getMsg.getFromIp() + "\n" + getMsg.uci + "\n";

		case Message.ENDSUBSCRIBE:			
			EndSubscribeMessage endSubMsg = (EndSubscribeMessage) message;			
			return "" + endSubMsg.getType() + "\n" + endSubMsg.getToIp() + "\n" + endSubMsg.getFromIp() + "\n" + endSubMsg.uci + "\n";
		
		case Message.NOTIFY:			
			NotifyMessage notifyMsg = (NotifyMessage) message;			
			return "" + notifyMsg.getType() + "\n" + notifyMsg.getToIp() + "\n" + notifyMsg.getFromIp() + "\n" + notifyMsg.uci + "\n" + notifyMsg.value + "\n";
						
		case Message.SET:			
			SetMessage setMsg = (SetMessage) message;			
			return "" + setMsg.getType() + "\n" + setMsg.getToIp() + "\n" + setMsg.getFromIp() + "\n" + setMsg.uci + "\n" + setMsg.value + "\n";
			
		case Message.STARTSUBSCRIBE:			
			StartSubscribeMessage startSubMsg = (StartSubscribeMessage) message;			
			return "" + startSubMsg.getType() + "\n" + startSubMsg.getToIp() + "\n" + startSubMsg.getFromIp() + "\n" + startSubMsg.uci + "\n";
					
		case Message.NOTIFYSUBSCRIBERS:			
			NotifySubscribersMessage notifysubMsg = (NotifySubscribersMessage) message;			
			return "" + notifysubMsg.getType() + "\n" + notifysubMsg.getToIp() + "\n" + notifysubMsg.getFromIp() + "\n" + notifysubMsg.uci + "\n" + notifysubMsg.value + "\n";
		
		case Message.CHECK_PREDECESSOR:
			CheckPredecessorMessage cpMsg = (CheckPredecessorMessage) message;
			return standard + Base64.encodeBytes(cpMsg.getHash().getID()) + "\n";
			
		case Message.CHECK_PREDECESSOR_RESPONSE:
			CheckPredecessorResponseMessage cprMsg = (CheckPredecessorResponseMessage) message;
			return standard + cprMsg.getPreNetworkAddress() + "\n" + Base64.encodeBytes(cprMsg.getPreHash().getID()) + "\n";
		
		case Message.CHECK_SUCCESSOR:
			CheckSuccessorMessage csMsg = (CheckSuccessorMessage) message;
			return standard + Base64.encodeBytes(csMsg.getHash().getID()) + "\n";
			
		case Message.CHECK_SUCCESSOR_RESPONSE:
			CheckSuccessorResponseMessage csrMsg = (CheckSuccessorResponseMessage) message;
			return standard + csrMsg.getSucNetworkAddress() + "\n" + Base64.encodeBytes(csrMsg.getSucHash().getID()) + "\n";
			
		case Message.DUPLICATE_NODE_ID:
			DuplicateNodeIdMessage dMsg = (DuplicateNodeIdMessage) message;
			return standard + Base64.encodeBytes(dMsg.getDuplicateKey().getID()) + "\n";
			
		case Message.FIND_PREDECESSOR:
			FindPredecessorMessage fpMsg = (FindPredecessorMessage) message;
			return standard + Base64.encodeBytes(fpMsg.getHash().getID()) + "\n" + fpMsg.getOrigAddress() + "\n";
			
		case Message.FIND_PREDECESSOR_RESPONSE:
			FindPredecessorResponseMessage fprMsg = (FindPredecessorResponseMessage) message;
			return standard + Base64.encodeBytes(fprMsg.getPredecessorHash().getID()) + "\n" + Base64.encodeBytes(fprMsg.getOrigHash().getID()) + "\n";
			
		case Message.JOIN_ACK:
			JoinAckMessage jaMsg = (JoinAckMessage) message;
			return standard + Base64.encodeBytes(jaMsg.getJoinKey().getID()) + "\n";
			
		case Message.JOIN_BUSY:
			JoinBusyMessage jbMsg = (JoinBusyMessage) message;
			return standard;
			
		case Message.JOIN_FINALIZE:
			JoinFinalizeMessage jfMsg = (JoinFinalizeMessage) message;
			return standard + Base64.encodeBytes(jfMsg.getJoinKey().getID()) + "\n";
			
		case Message.JOIN:
			JoinMessage jMsg = (JoinMessage) message;
			return standard + jMsg.getOriginatorAddress() + "\n" + Base64.encodeBytes(jMsg.getKey().getID()) + "\n";
			
		case Message.JOIN_RESPONSE:
			JoinResponseMessage jrMsg = (JoinResponseMessage) message;
			return standard + Base64.encodeBytes(jrMsg.getJoinKey().getID()) + "\n" + jrMsg.getSuccessorAddress() + "\n" + Base64.encodeBytes(jrMsg.getSuccessor().getID()) + "\n" + Base64.encodeBytes(jrMsg.getPredecessor().getID()) + "\n";
			
		case Message.KEEPALIVE:
			KeepAliveMessage kaMsg = (KeepAliveMessage) message;
			return standard + Base64.encodeBytes(kaMsg.getAdvertisedID().getID()) + "\n" + kaMsg.getAdvertisedNetworkAddress() + "\n"; 
			
		case Message.NODE_SUSPICIOUS:
			NodeSuspiciousMessage nsMsg = (NodeSuspiciousMessage) message;
			return standard + Base64.encodeBytes(nsMsg.getHash().getID()) + "\n";
			
		case Message.NODE_JOIN_NOTIFY:
			NotifyJoinMessage njMsg = (NotifyJoinMessage) message;
			return standard + njMsg.getNetworkAddress() + "\n" + Base64.encodeBytes(njMsg.getHash().getID()) + "\n";
			
		case Message.NODE_LEAVE_NOTIFY:
			NotifyLeaveMessage nlMsg = (NotifyLeaveMessage) message;
			return standard + Base64.encodeBytes(nlMsg.getHash().getID()) + "\n" + Base64.encodeBytes(nlMsg.getSuccessorHash().getID()) + "\n" + nlMsg.getSuccessorNetworkAddress() + "\n";
			
		case Message.REGISTER:
			RegisterMessage rMsg = (RegisterMessage) message;
			return standard + Base64.encodeBytes(rMsg.getSensor().getID()) + "\n" + Base64.encodeBytes(rMsg.getOrigHash().getID()) + "\n" + rMsg.getOrigAddress() + "\n";
			
		case Message.REGISTER_RESPONSE:
			RegisterResponseMessage rrMsg = (RegisterResponseMessage) message;
			return standard + Base64.encodeBytes(rrMsg.getSensor().getID()) + "\n" + Base64.encodeBytes(rrMsg.getOrigHash().getID()) + "\n" ;
			
		case Message.RESOLVE:
			ResolveMessage reMsg = (ResolveMessage) message;
			return standard + Base64.encodeBytes(reMsg.getSensorHash().getID()) + "\n" + reMsg.getOrigAddress() + "\n";
			
		case Message.RESOLVE_RESPONSE:
			ResolveResponseMessage rerMsg = (ResolveResponseMessage) message;
			return standard + Base64.encodeBytes(rerMsg.getSensor().getID()) + "\n" + rerMsg.getSensorAddress() + "\n";
		
		case Message.BROADCAST:
			BroadcastMessage bMsg = (BroadcastMessage) message;
			standard += Base64.encodeBytes(bMsg.getStartKey().getID()) + "\n" + Base64.encodeBytes(bMsg.getEndKey().getID()) + "\n" + bMsg.getInternalType() + "\n";
			switch (bMsg.getInternalType()) {
			case Message.KEEPALIVE:
				KeepAliveMessage kabMsg = (KeepAliveMessage) bMsg.extractMessage();
				return standard + Base64.encodeBytes(kabMsg.getAdvertisedID().getID()) + "\n" + kabMsg.getAdvertisedNetworkAddress() + "\n";
			
			case Message.NODE_SUSPICIOUS:
				NodeSuspiciousMessage nsbMsg = (NodeSuspiciousMessage) bMsg.extractMessage();
				return standard + Base64.encodeBytes(nsbMsg.getHash().getID()) + "\n";
			
			case Message.NODE_JOIN_NOTIFY:
				NotifyJoinMessage njbMsg = (NotifyJoinMessage) bMsg.extractMessage();
				return standard + njbMsg.getNetworkAddress() + "\n" + Base64.encodeBytes(njbMsg.getHash().getID()) + "\n";
			
			case Message.NODE_LEAVE_NOTIFY:
				NotifyLeaveMessage nlbMsg = (NotifyLeaveMessage) bMsg.extractMessage();
				return standard + Base64.encodeBytes(nlbMsg.getHash().getID()) + "\n" + Base64.encodeBytes(nlbMsg.getSuccessorHash().getID()) + "\n" + nlbMsg.getSuccessorNetworkAddress() + "\n";
			}	
		}
		
		return "Unknown\n";
	}

	public Message deserializeMessageFromString(String stringRepresentation,String fromIp,String toIp) {
		try {
			//Split on token
			String split[] = stringRepresentation.split("\n");
			
			//Get the Type
			int type = Integer.parseInt(split[0]);
			
			//Switch the type and recreate message
			switch (type) {
			case Message.GET:
				GetMessage getMsg = new GetMessage(split[3], split[1], split[2]);			
				return getMsg;

			case Message.ENDSUBSCRIBE:
				EndSubscribeMessage endSubMsg = new EndSubscribeMessage(split[3], split[1], split[2]);				
				return endSubMsg;

			case Message.NOTIFY:
				NotifyMessage notifyMsg = new NotifyMessage(split[3], split[4], split[1], split[2]);		
				return notifyMsg;

			case Message.SET:
				SetMessage setMsg = new SetMessage(split[3], split[4], split[1], split[2]);			
				return setMsg;
				
			case Message.STARTSUBSCRIBE:
				StartSubscribeMessage startSubMsg = new StartSubscribeMessage(split[3], split[1], split[2]);			
				return startSubMsg;
						
			case Message.NOTIFYSUBSCRIBERS:
				NotifySubscribersMessage notifysubMsg = new NotifySubscribersMessage(split[3], split[4], split[1], split[2]);		
				return notifysubMsg;	
				
			case Message.CHECK_PREDECESSOR:
				CheckPredecessorMessage cpMsg = new CheckPredecessorMessage(split[2], split[1], new NodeID(Base64.decode(split[3])));
				return cpMsg;
				
			case Message.CHECK_PREDECESSOR_RESPONSE:
				CheckPredecessorResponseMessage cprMsg = new CheckPredecessorResponseMessage(split[2], split[1], split[3], new NodeID(Base64.decode(split[4])));
				return cprMsg;
			
			case Message.CHECK_SUCCESSOR:
				CheckSuccessorMessage csMsg = new CheckSuccessorMessage(split[2], split[1], new NodeID(Base64.decode(split[3])));
				return csMsg;
				
			case Message.CHECK_SUCCESSOR_RESPONSE:
				CheckSuccessorResponseMessage csrMsg = new CheckSuccessorResponseMessage(split[2], split[1], split[3], new NodeID(Base64.decode(split[4])));
				return csrMsg;
				
			case Message.DUPLICATE_NODE_ID:
				DuplicateNodeIdMessage dMsg = new DuplicateNodeIdMessage(split[2], split[1],  new NodeID(Base64.decode(split[3])));
				return dMsg;
				
			case Message.FIND_PREDECESSOR:
				FindPredecessorMessage fpMsg = new FindPredecessorMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), split[4]);
				return fpMsg;
				
			case Message.FIND_PREDECESSOR_RESPONSE:
				FindPredecessorResponseMessage fprMsg = new FindPredecessorResponseMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])));
				return fprMsg;
				
			case Message.JOIN_ACK:
				JoinAckMessage jaMsg = new JoinAckMessage(split[2], split[1], new NodeID(Base64.decode(split[3])));
				return jaMsg;
				
			case Message.JOIN_BUSY:
				JoinBusyMessage jbMsg = new JoinBusyMessage(split[2], split[1]);
				return jbMsg;
				
			case Message.JOIN_FINALIZE:
				JoinFinalizeMessage jfMsg = new JoinFinalizeMessage(split[2], split[1], new NodeID(Base64.decode(split[3])));
				return jfMsg;
				
			case Message.JOIN:
				JoinMessage jMsg = new JoinMessage(split[2], split[1], split[3], new NodeID(Base64.decode(split[4])));
				return jMsg;
				
			case Message.JOIN_RESPONSE:
				JoinResponseMessage jrMsg = new JoinResponseMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), split[4], new NodeID(Base64.decode(split[5])), new NodeID(Base64.decode(split[6])));
				return jrMsg;
				
			case Message.KEEPALIVE:
				KeepAliveMessage kaMsg = new KeepAliveMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), split[4]);
				return kaMsg;
				
			case Message.NODE_SUSPICIOUS:
				NodeSuspiciousMessage nsMsg = new NodeSuspiciousMessage(split[2], split[1], new NodeID(Base64.decode(split[3])));
				return nsMsg;
				
			case Message.NODE_JOIN_NOTIFY:
				NotifyJoinMessage njMsg = new NotifyJoinMessage(split[2], split[1], split[3], new NodeID(Base64.decode(split[4])));
				return njMsg;
				
			case Message.NODE_LEAVE_NOTIFY:
				NotifyLeaveMessage nlMsg = new NotifyLeaveMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])), split[5]);
				return nlMsg;
				
			case Message.REGISTER:
				RegisterMessage rMsg = new RegisterMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])), split[5]);
				return rMsg;
				
			case Message.REGISTER_RESPONSE:
				RegisterResponseMessage rrMsg = new RegisterResponseMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])));
				return rrMsg;
				
			case Message.RESOLVE:
				ResolveMessage reMsg = new ResolveMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), split[4]);
				return reMsg;
				
			case Message.RESOLVE_RESPONSE:
				ResolveResponseMessage rerMsg =new ResolveResponseMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), split[4]);
				return rerMsg;
			
			case Message.BROADCAST:
				//Get the internal type
				int internalType = Integer.parseInt(split[5]);
				switch (internalType) {
				case Message.KEEPALIVE:
					KeepAliveBroadcastMessage kabMsg = new KeepAliveBroadcastMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])), new NodeID(Base64.decode(split[6])), split[7]);
					return kabMsg;
				
				case Message.NODE_SUSPICIOUS:
					NodeSuspiciousBroadcastMessage nsbMsg = new NodeSuspiciousBroadcastMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])), new NodeID(Base64.decode(split[6])));
					return nsbMsg;
				
				case Message.NODE_JOIN_NOTIFY:
					NotifyJoinBroadcastMessage njbMsg = new NotifyJoinBroadcastMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])), split[6], new NodeID(Base64.decode(split[7])));
					return njbMsg;
				
				case Message.NODE_LEAVE_NOTIFY:
					NotifyLeaveBroadcastMessage nlbMsg = new NotifyLeaveBroadcastMessage(split[2], split[1], new NodeID(Base64.decode(split[3])), new NodeID(Base64.decode(split[4])), new NodeID(Base64.decode(split[6])), new NodeID(Base64.decode(split[7])), split[8]);
					return nlbMsg;
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] serializeMessage(Message message) {
		try {
			return serializeMessageToString(message).getBytes("iso-8859-1");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Message deserializeMessage(byte[] stringRepresentation,String fromIp,String toIp) {
		try {
			return deserializeMessageFromString(new String(stringRepresentation,"iso-8859-1"),fromIp,toIp);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
