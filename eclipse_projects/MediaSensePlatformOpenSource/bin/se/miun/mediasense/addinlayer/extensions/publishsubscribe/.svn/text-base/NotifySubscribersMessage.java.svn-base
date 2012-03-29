package se.miun.mediasense.addinlayer.extensions.publishsubscribe;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class NotifySubscribersMessage extends Message {
	
	public String uci;
	public String value;
	
	public NotifySubscribersMessage(String uci, String value, String toIp, String fromIp) {
		
		this.type = Message.NOTIFYSUBSCRIBERS;
		this.value = value;
		this.uci = uci;
		this.fromIp = fromIp;
		this.toIp = toIp;
		
	}
	

}
