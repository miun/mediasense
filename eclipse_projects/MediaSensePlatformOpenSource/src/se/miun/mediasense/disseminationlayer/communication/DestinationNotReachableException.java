package se.miun.mediasense.disseminationlayer.communication;

@SuppressWarnings("serial")
public class DestinationNotReachableException extends Exception {
	protected String errorMsg = "DestinationNotReachableException";
	
	public DestinationNotReachableException() {
		super();
	}
	
	public DestinationNotReachableException(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String getMessage() {
		return errorMsg;
	}
}
