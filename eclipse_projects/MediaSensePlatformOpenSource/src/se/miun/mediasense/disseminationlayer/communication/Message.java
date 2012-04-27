package se.miun.mediasense.disseminationlayer.communication;

import se.miun.mediasense.addinlayer.extensions.publishsubscribe.EndSubscribeMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.NotifySubscribersMessage;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.StartSubscribeMessage;
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

public abstract class Message {

	public final static int UNKNOWN = 0;
	public final static int GET = 1;
	public final static int SET = 2;
	public final static int NOTIFY = 3;
	public final static int STARTSUBSCRIBE = 6;
	public final static int ENDSUBSCRIBE = 7;
	//public final static int TRANSFER = 8;
	public final static int NOTIFYSUBSCRIBERS = 9;
	public final static int ACK = 10;

	
	//DHT messages
	public final static int BROADCAST = 17;

	public final static int REGISTER = 4;
	public final static int REGISTER_RESPONSE = 30;

	public final static int RESOLVE = 5;
	public final static int RESOLVE_RESPONSE = 15;
	
	public final static int JOIN = 11;
	public final static int JOIN_RESPONSE = 12;
	public final static int JOIN_ACK = 22;
	public final static int JOIN_BUSY = 23;
	public final static int JOIN_FINALIZE = 24;
	public final static int DUPLICATE_NODE_ID = 16;
	
	public final static int KEEPALIVE = 13;
	//public final static int KEEPALIVE_RESPONSE = 14; DOES NOT EXIST ANY LONGER
	
	public final static int NODE_JOIN_NOTIFY = 18;
	public final static int NODE_LEAVE_NOTIFY = 19;
	
	public final static int NODE_SUSPICIOUS = 29;
	
	public final static int FIND_PREDECESSOR = 20;
	public final static int FIND_PREDECESSOR_RESPONSE = 21;

	public final static int CHECK_PREDECESSOR = 25;
	public final static int CHECK_PREDECESSOR_RESPONSE = 26;

	public final static int CHECK_SUCCESSOR = 27;
	public final static int CHECK_SUCCESSOR_RESPONSE = 28;

	//These were public first, the so called I-LIKE-TO-SOLVE-TRICKY-PROBLEMS-IN-THE-MIDDLE-OF-THE-NIGHT design pattern
	//Changed to something better (someone owns me a beer for that)
	private int type = Message.UNKNOWN;
	private String fromIp = "";
	private String toIp = "";
	
	public String getFromIp() {
		return fromIp;
	}
	
	public String getToIp() {
		return toIp;
	}
	
	public int getType() {
		return type;
	}
	
	public Message(String from,String to,int type) {
		this.fromIp = from;
		this.toIp = to;
		this.type = type;
	}
	
	public String toString() {
		//Return type as number
		return toString(new Integer(type).toString());
	}
	
	protected String toString(String msgType) {
		//Return message info
		return "MSG: type: " + msgType + " - from: (" + fromIp + ") - to: (" + toIp + ")"; 
	}
	
	public int getDataAmount() {
		//2 x ip-address + 1 type
		return 4 + 4 + 1;
	}
	
	public byte[] toByteArray() {
		//Return byte representation of this class
		byte[] array =  new byte[1];
		array[0] = (byte)type;
		return array;
	}
	
	public static Message fromByteArray(byte[] data) {
		//Check type
		if(data == null || data.length == 0) return null;
		int t = (int)data[0];
		byte[] subdata = new byte[data.length - 1];
		System.arraycopy(data, 1, subdata, 0, data.length - 1);
		
		switch(t) {
		case GET: return GetMessage.fromByteArray(subdata);
		case SET: return SetMessage.fromByteArray(subdata);
		case NOTIFY: return NotifyMessage.fromByteArray(subdata);
		case STARTSUBSCRIBE: return StartSubscribeMessage.fromByteArray(subdata);
		case ENDSUBSCRIBE: return EndSubscribeMessage.fromByteArray(subdata);
		case NOTIFYSUBSCRIBERS: return NotifySubscribersMessage.fromByteArray(subdata);
		//case ACK: return Ack.fromByteArray(subdata);
		case REGISTER: return RegisterMessage.fromByteArray(subdata);
		case REGISTER_RESPONSE: return RegisterResponseMessage.fromByteArray(subdata);
		case RESOLVE: return ResolveMessage.fromByteArray(subdata);
		case RESOLVE_RESPONSE: return ResolveResponseMessage.fromByteArray(subdata);
		case JOIN: return JoinMessage.fromByteArray(subdata);
		case JOIN_RESPONSE: return JoinResponseMessage.fromByteArray(subdata);
		case JOIN_BUSY: return JoinBusyMessage.fromByteArray(subdata);
		case JOIN_ACK: return JoinAckMessage.fromByteArray(subdata);
		case NODE_JOIN_NOTIFY: return NotifyJoinMessage.fromByteArray(subdata);
		case NODE_LEAVE_NOTIFY: return NotifyLeaveMessage.fromByteArray(subdata);
		case NODE_SUSPICIOUS: return NodeSuspiciousMessage.fromByteArray(subdata);
		case FIND_PREDECESSOR: return FindPredecessorMessage.fromByteArray(subdata);
		case FIND_PREDECESSOR_RESPONSE: return FindPredecessorResponseMessage.fromByteArray(subdata);
		case CHECK_PREDECESSOR: return CheckPredecessorMessage.fromByteArray(subdata);
		case CHECK_PREDECESSOR_RESPONSE: return CheckPredecessorResponseMessage.fromByteArray(subdata);
		case CHECK_SUCCESSOR: return CheckSuccessorMessage.fromByteArray(subdata);
		case CHECK_SUCCESSOR_RESPONSE: return CheckSuccessorResponseMessage.fromByteArray(subdata);
		default: return null;
		}
	}
}
