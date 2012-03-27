package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class RegisterMessage extends Message {
	
	public String uci;

	
	public RegisterMessage(String uci, String toIp, String fromIp) {
		
		this.type = Message.REGISTER;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.uci = uci;
		
	}
	

}
