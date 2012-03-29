package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class ResolveResponseMessage extends Message {
	
	public String uci;
	public String resolvedIp;

	
	public ResolveResponseMessage(String uci, String resolvedIp, String toIp, String fromIp) {
		
		this.type = Message.RESOLVE_RESPONSE;
		this.uci = uci;
		this.fromIp = fromIp;
		this.toIp = toIp;
		this.resolvedIp = resolvedIp;
		
	}
	

}
