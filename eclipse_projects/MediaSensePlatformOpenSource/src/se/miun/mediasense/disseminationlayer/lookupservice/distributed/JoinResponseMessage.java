package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class JoinResponseMessage extends Message {
	
	public String grandGrandParent;
	public String grandParent;
	public String parent;
	
	public String self;

	public String child;
	public String grandChild;
	public String grandGrandChild;
	
	

	
	
	
	public JoinResponseMessage(String grandGrandParent, String grandParent, String parent, String self, String child, String grandChild, String grandGrandChild,String toIp, String fromIp) {
		
		this.type = Message.JOIN_RESPONSE;
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
