package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class KeepAliveResponseMessage extends Message {
	
	
	public String child;
	public String grandChild;
	public String grandGrandChild;
	
	public String self;
	
	public String parent;
	public String grandParent;
	public String grandGrandParent;
	
	
	
	public KeepAliveResponseMessage(String grandGrandParent, String grandParent, String parent,  String self, String child, String grandChild, String grandGrandChild, String toIp, String fromIp) {
		
		this.type = Message.KEEPALIVE_RESPONSE;
		this.fromIp = fromIp;
		this.toIp = toIp;
		
		
		this.grandGrandParent = grandGrandParent;
		this.grandParent = grandParent;
		this.parent = parent;		

		this.self = self;
		
		this.child = child;		
		this.grandChild = grandChild;
		this.grandGrandChild = grandGrandChild;		
	}
	

}
