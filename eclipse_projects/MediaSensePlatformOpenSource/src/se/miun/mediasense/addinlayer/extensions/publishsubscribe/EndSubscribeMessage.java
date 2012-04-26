package se.miun.mediasense.addinlayer.extensions.publishsubscribe;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class EndSubscribeMessage extends Message {
	
	public String uci;

	
	public EndSubscribeMessage(String uci, String toIp, String fromIp) {
		super(fromIp,toIp,ENDSUBSCRIBE);
		
		this.uci = uci;
		
	}
	

}
