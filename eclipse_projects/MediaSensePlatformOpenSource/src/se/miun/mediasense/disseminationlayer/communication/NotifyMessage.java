package se.miun.mediasense.disseminationlayer.communication;

public class NotifyMessage extends Message {
	
	public String uci;
	public String value;
	
	public NotifyMessage(String uci, String value, String toIp, String fromIp) {
		super(fromIp,toIp,Message.NOTIFY);
		
		this.value = value;
		this.uci = uci;
	}
}
