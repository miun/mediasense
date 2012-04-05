package manager;

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

	
	public final static int REGISTER = 4;

	public final static int RESOLVE = 5;
	public final static int RESOLVE_RESPONSE = 15;
	
	public final static int JOIN = 11;
	public final static int JOIN_RESPONSE = 12;
	public final static int DUPLICATE_NODE_ID = 16;
	
	public final static int KEEPALIVE = 13;
	public final static int KEEPALIVE_RESPONSE = 14;
	
	public final static int NODE_JOIN_NOTIFY = 18;
	public final static int NODE_LEAVE_NOTIFY = 19;

	public final static int BROADCAST = 17;

	
	public int type = Message.UNKNOWN;
	public String fromIp = "";
	public String toIp = "";
	
	public Message() {
		
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
		return "MSG: type:{" + msgType + "} - from:{" + fromIp + "} - to:{" + toIp + "}"; 
	}
}
