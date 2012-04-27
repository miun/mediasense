package se.miun.mediasense.disseminationlayer.communication.rudp;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class AcknowledgementMessage extends Message {
	
	public int seqNr;
	
	public AcknowledgementMessage(String seqNr, String toIp, String fromIp) {
		super(fromIp,toIp,ACK);
		
		this.seqNr = Integer.parseInt(seqNr);
	}
	

}
