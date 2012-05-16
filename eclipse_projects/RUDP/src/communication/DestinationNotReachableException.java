package communication;

import java.net.InetSocketAddress;

@SuppressWarnings("serial")
public class DestinationNotReachableException extends Exception {
	private InetSocketAddress socketAddress;
	
	public DestinationNotReachableException(String message,InetSocketAddress socketAddress) {
		super(message);
		this.socketAddress = socketAddress;
	}
	
	public InetSocketAddress getInetSocketAddress() {
		return socketAddress;
	}

}
