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
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.JoinMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.JoinResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.KeepAliveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.KeepAliveResponseMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.RegisterMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.ResolveMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.ResolveResponseMessage;

public class EnterSeparatedMessageSerializer implements MessageSerializer{

	@Override
	public String serializeMessage(Message message) {
						
		switch (message.type) {
		case Message.GET:			
			GetMessage getMsg = (GetMessage) message;
			return "" + getMsg.type + "\n" + getMsg.toIp + "\n" + getMsg.fromIp + "\n" + getMsg.uci + "\n";

		case Message.ENDSUBSCRIBE:			
			EndSubscribeMessage endSubMsg = (EndSubscribeMessage) message;			
			return "" + endSubMsg.type + "\n" + endSubMsg.toIp + "\n" + endSubMsg.fromIp + "\n" + endSubMsg.uci + "\n";
		
		case Message.NOTIFY:			
			NotifyMessage notifyMsg = (NotifyMessage) message;			
			return "" + notifyMsg.type + "\n" + notifyMsg.toIp + "\n" + notifyMsg.fromIp + "\n" + notifyMsg.uci + "\n" + notifyMsg.value + "\n";
		
		case Message.REGISTER:			
			RegisterMessage regMsg = (RegisterMessage) message;
			return "" + regMsg.type + "\n" + regMsg.toIp + "\n" + regMsg.fromIp + "\n" + regMsg.uci + "\n";
			
		case Message.RESOLVE:			
			ResolveMessage resMsg = (ResolveMessage) message;			
			return "" + resMsg.type + "\n" + resMsg.toIp + "\n" + resMsg.fromIp + "\n" + resMsg.uci + "\n" + resMsg.ttl + "\n";
			
		case Message.RESOLVE_RESPONSE:		
		ResolveResponseMessage resRespMsg = (ResolveResponseMessage) message;			
			return "" + resRespMsg.type + "\n" + resRespMsg.toIp + "\n" + resRespMsg.fromIp + "\n" + resRespMsg.uci + "\n" + resRespMsg.resolvedIp + "\n";
				
		case Message.SET:			
			SetMessage setMsg = (SetMessage) message;			
			return "" + setMsg.type + "\n" + setMsg.toIp + "\n" + setMsg.fromIp + "\n" + setMsg.uci + "\n" + setMsg.value + "\n";
			
		case Message.STARTSUBSCRIBE:			
			StartSubscribeMessage startSubMsg = (StartSubscribeMessage) message;			
			return "" + startSubMsg.type + "\n" + startSubMsg.toIp + "\n" + startSubMsg.fromIp + "\n" + startSubMsg.uci + "\n";
					
		case Message.NOTIFYSUBSCRIBERS:			
			NotifySubscribersMessage notifysubMsg = (NotifySubscribersMessage) message;			
			return "" + notifysubMsg.type + "\n" + notifysubMsg.toIp + "\n" + notifysubMsg.fromIp + "\n" + notifysubMsg.uci + "\n" + notifysubMsg.value + "\n";
		
		case Message.ACK:			
			AcknowledgementMessage ackMsg = (AcknowledgementMessage) message;			
			return "" + ackMsg.type + "\n" + ackMsg.toIp + "\n" + ackMsg.fromIp + "\n" + ackMsg.seqNr + "\n";
		
		case Message.JOIN:
			JoinMessage joinMsg = (JoinMessage) message;
			return "" + joinMsg.type + "\n" + joinMsg.toIp + "\n" + joinMsg.fromIp + "\n";
			
		case Message.JOIN_RESPONSE: 
			JoinResponseMessage joinRespMsg = (JoinResponseMessage) message;
			return "" + joinRespMsg.type + "\n" + joinRespMsg.toIp + "\n" + joinRespMsg.fromIp + "\n" + joinRespMsg.grandGrandParent + "\n" + joinRespMsg.grandParent + "\n" + joinRespMsg.parent + "\n" + joinRespMsg.self + "\n" + joinRespMsg.child + "\n" + joinRespMsg.grandChild + "\n" + joinRespMsg.grandGrandChild + "\n";
			
		case Message.KEEPALIVE:
			KeepAliveMessage keepAliceMsg = (KeepAliveMessage) message;
			return "" + keepAliceMsg.type + "\n" + keepAliceMsg.toIp + "\n" + keepAliceMsg.fromIp + "\n";
			
		case Message.KEEPALIVE_RESPONSE: 
			KeepAliveResponseMessage keepAliveRespMsg = (KeepAliveResponseMessage) message;
			return "" + keepAliveRespMsg.type + "\n" + keepAliveRespMsg.toIp + "\n" + keepAliveRespMsg.fromIp + "\n" + keepAliveRespMsg.grandGrandParent + "\n" + keepAliveRespMsg.grandParent + "\n" + keepAliveRespMsg.parent + "\n" + keepAliveRespMsg.self + "\n" + keepAliveRespMsg.child + "\n" + keepAliveRespMsg.grandChild + "\n" + keepAliveRespMsg.grandGrandChild + "\n";
			
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
				RegisterMessage registerMsg = new RegisterMessage(split[3], split[1], split[2]);			
				return registerMsg;

			case Message.RESOLVE:
				ResolveMessage resolveMsg = new ResolveMessage(split[3], split[4], split[1], split[2]);			
				return resolveMsg;
				
			case Message.RESOLVE_RESPONSE:
				ResolveResponseMessage resRespMsg = new ResolveResponseMessage(split[3], split[4], split[1], split[2]);
				return resRespMsg;

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
				JoinMessage joinMsg = new JoinMessage(split[1], split[2]);
				return joinMsg;
				
			case Message.JOIN_RESPONSE:
				JoinResponseMessage joinRspMsg = new JoinResponseMessage(split[3], split[4], split[5], split[5], split[6], split[7], split[8], split[1], split[2]);
				return joinRspMsg;
			
			case Message.KEEPALIVE:
				KeepAliveMessage keepAliveMsg = new KeepAliveMessage(split[1], split[2]);
				return keepAliveMsg;
				
			case Message.KEEPALIVE_RESPONSE:
				KeepAliveResponseMessage keepAliveRespMsg = new KeepAliveResponseMessage(split[3], split[4], split[5], split[6], split[7], split[8], split[9], split[1], split[2]);
				return keepAliveRespMsg;
			

				
				
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
