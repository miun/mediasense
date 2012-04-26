package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

@SuppressWarnings("serial")
public class DestinationNotReachableException extends Exception {
	
	public DestinationNotReachableException(String message) {
		super(message);
	}

}
