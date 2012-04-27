package se.miun.mediasense.disseminationlayer.communication;

public class SetMessage extends Message {
	
	public String uci;
	public String value;

	
	public SetMessage(String uci, String value, String toIp, String fromIp) {
		super(fromIp,toIp,SET);
		
		this.uci = uci;
		this.value = value;
		
	}
	

}
