package se.miun.mediasense.addinlayer.extensions.publishsubscribe;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class StartSubscribeMessage extends Message {
	
	public String uci;
	
	public StartSubscribeMessage(String uci, String toIp, String fromIp) {
		super(fromIp,toIp,STARTSUBSCRIBE);
		
		this.uci = uci;
		
	}
	

}
