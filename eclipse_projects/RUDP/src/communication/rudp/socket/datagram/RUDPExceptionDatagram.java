package communication.rudp.socket.datagram;

import java.net.InetSocketAddress;

import communication.DestinationNotReachableException;

public class RUDPExceptionDatagram extends RUDPAbstractDatagram {
	private InetSocketAddress socketAddress;
	
	public RUDPExceptionDatagram(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	public DestinationNotReachableException getException() {
		return new DestinationNotReachableException("Link failed",socketAddress);
	}
}
