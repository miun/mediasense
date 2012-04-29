package se.miun.mediasense.disseminationlayer.communication.serializer;

import se.miun.mediasense.addinlayer.extensions.publishsubscribe.EndSubscribeMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.NotifySubscribersMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.StartSubscribeMessage;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.MessageSerializer;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.communication.rudp.AcknowledgementMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.JoinResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.KeepAliveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.RegisterMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.messages.unicast.ResolveResponseMessage;

public class EnterSeparatedMessageSerializer implements MessageSerializer{

	@Override
	public String serializeMessage(Message message) {
						
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
		
		case Message.ACK:			
			AcknowledgementMessage ackMsg = (AcknowledgementMessage) message;			
			return "" + ackMsg.getType() + "\n" + ackMsg.getToIp() + "\n" + ackMsg.getFromIp() + "\n" + ackMsg.seqNr + "\n";
		
		case Message.CHECK_PREDECESSOR:
			
			return "";
			
		case Message.CHECK_PREDECESSOR_RESPONSE:
			
			return "";
		
		case Message.CHECK_SUCCESSOR:
			
			return "";
			
		case Message.CHECK_SUCCESSOR_RESPONSE:
			
			return "";
			
		case Message.DUPLICATE_NODE_ID:
			
			return "";
			
		case Message.FIND_PREDECESSOR:
			
			return "";
			
		case Message.FIND_PREDECESSOR_RESPONSE:
			
			return "";
			
		case Message.JOIN_ACK:
			
			return "";
			
		case Message.JOIN_BUSY:
			
			return "";
			
		case Message.JOIN_FINALIZE:
			
			return "";
			
		case Message.JOIN:
			
			return "";
			
		case Message.JOIN_RESPONSE:
			
			return "";
			
		case Message.KEEPALIVE:
			
			return "";
			
		case Message.NODE_SUSPICIOUS:
			
			return "";
			
		case Message.NODE_JOIN_NOTIFY:
			
			return "";
			
		case Message.NODE_LEAVE_NOTIFY:
			
			return "";
			
		case Message.REGISTER:
			
			return "";
			
		case Message.REGISTER_RESPONSE:
			
			return "";
			
		case Message.RESOLVE:
			
			return "";
			
		case Message.RESOLVE_RESPONSE:
			
			return "";
			
		}
		
		return "Unknown\n";
	}

	@Override
	public Message deserializeMessage(String stringRepresentation) {
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

			case Message.REGISTER:
				//RegisterMessage registerMsg = new RegisterMessage(split[3], split[1], split[2]);			
				//return registerMsg;

			case Message.RESOLVE:
				//ResolveMessage resolveMsg = new ResolveMessage(split[3], split[4], split[1], split[2]);			
				//return resolveMsg;
				
			case Message.RESOLVE_RESPONSE:
				//ResolveResponseMessage resRespMsg = new ResolveResponseMessage(split[3], split[4], split[1], split[2]);
				//return resRespMsg;

			case Message.SET:
				SetMessage setMsg = new SetMessage(split[3], split[4], split[1], split[2]);			
				return setMsg;
				
			case Message.STARTSUBSCRIBE:
				StartSubscribeMessage startSubMsg = new StartSubscribeMessage(split[3], split[1], split[2]);			
				return startSubMsg;
						
			case Message.NOTIFYSUBSCRIBERS:
				NotifySubscribersMessage notifysubMsg = new NotifySubscribersMessage(split[3], split[4], split[1], split[2]);		
				return notifysubMsg;	
				
			case Message.ACK:
				AcknowledgementMessage ackMsg = new AcknowledgementMessage(split[3], split[1], split[2]);		
				return ackMsg;	
				
			case Message.JOIN:
				//JoinMessage joinMsg = new JoinMessage(split[1], split[2]);
				//return joinMsg;
				
			case Message.JOIN_RESPONSE:
				//JoinResponseMessage joinRspMsg = new JoinResponseMessage(split[3], split[4], split[5], split[5], split[6], split[7], split[8], split[1], split[2]);
				//return joinRspMsg;
			
			case Message.KEEPALIVE:
				//KeepAliveMessage keepAliveMsg = new KeepAliveMessage(split[1], split[2]);
				//return keepAliveMsg;
				
			case Message.KEEPALIVE_RESPONSE:
				//KeepAliveResponseMessage keepAliveRespMsg = new KeepAliveResponseMessage(split[3], split[4], split[5], split[6], split[7], split[8], split[9], split[1], split[2]);
				//return keepAliveRespMsg;
			

				
				
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
