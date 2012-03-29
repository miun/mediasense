package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class ResolveMessage extends Message {
	
	public String uci;
	public int ttl;

	
	public ResolveMessage(String uci, String ttl, String toIp, String fromIp) {
		
		this.type = Message.RESOLVE;
		this.uci = uci;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.ttl = Integer.parseInt(ttl);
		
	}
	

}
