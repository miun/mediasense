package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class JoinMessage extends Message {

	public JoinMessage(String toIp, String fromIp) {
		
		this.type = Message.JOIN;
		this.fromIp = fromIp;
		this.toIp = toIp;
		
	}
	

}
