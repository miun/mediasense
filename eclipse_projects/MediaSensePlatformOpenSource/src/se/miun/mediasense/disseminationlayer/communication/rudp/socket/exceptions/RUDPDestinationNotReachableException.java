package se.miun.mediasense.disseminationlayer.communication.rudp.socket.exceptions;

import java.net.InetSocketAddress;

import se.miun.mediasense.disseminationlayer.communication.DestinationNotReachableException;

@SuppressWarnings("serial")
public class RUDPDestinationNotReachableException extends DestinationNotReachableException {
	private static final String ERROR_MESSAGE = "RUDP destination not reachable. Link failed";

	//Address of failed link - optional
	private InetSocketAddress socketAddress = null;
	
	public RUDPDestinationNotReachableException() {
		super(ERROR_MESSAGE);
	}

	public RUDPDestinationNotReachableException(InetSocketAddress socketAddress) {
		super(ERROR_MESSAGE);
		this.socketAddress = socketAddress;
	}
	
	public InetSocketAddress getInetSocketAddress() {
		return socketAddress;
	}
}
