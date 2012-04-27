package se.miun.mediasense.disseminationlayer.communication;

public class GetMessage extends Message {
	
	public String uci;
	
	public GetMessage(String uci, String toIp, String fromIp) {
		super(fromIp,toIp,GET);
		
		this.uci = uci;
		
	}
	

}
