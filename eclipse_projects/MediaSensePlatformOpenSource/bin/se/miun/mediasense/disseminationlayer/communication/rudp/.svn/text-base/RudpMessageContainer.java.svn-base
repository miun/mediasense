package se.miun.mediasense.disseminationlayer.communication.rudp;

import java.util.Random;

import se.miun.mediasense.disseminationlayer.communication.Message;

public class RudpMessageContainer {
	
	public int seqNr = -1;
	
	public Message message = null;		
	public int numResends = -1;
	public long life = -1;

	public RudpMessageContainer(Message message) {
		
		this.message = message;
		numResends = 0;
		life = System.currentTimeMillis();
		
		// Generate the SeqNr
		// Only simple ones right now...
		Random r = new Random(System.currentTimeMillis());
		seqNr = r.nextInt(Integer.MAX_VALUE);
		
	}

}
