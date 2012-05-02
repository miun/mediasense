package communication;


public abstract class Message {

	private int type = 0;
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

}
