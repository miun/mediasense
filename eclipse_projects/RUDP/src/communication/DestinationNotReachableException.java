package communication;

import java.net.InetSocketAddress;

@SuppressWarnings("serial")
public class DestinationNotReachableException extends Exception {
	private static final String ERROR_MESSAGE = "Destination not reachable. Link failed";
	
	private InetSocketAddress socketAddress;
	
	public DestinationNotReachableException(InetSocketAddress socketAddress) {
		super(ERROR_MESSAGE);
		this.socketAddress = socketAddress;
	}
	
	public InetSocketAddress getInetSocketAddress() {
		return socketAddress;
	}

}
