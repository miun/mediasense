package se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram;

import java.net.InetSocketAddress;

import se.miun.mediasense.disseminationlayer.communication.rudp.socket.exceptions.RUDPDestinationNotReachableException;

public class RUDPExceptionDatagram extends RUDPAbstractDatagram {
	private InetSocketAddress socketAddress;
	
	public RUDPExceptionDatagram(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	public RUDPDestinationNotReachableException getException() {
		return new RUDPDestinationNotReachableException(socketAddress);
	}
}
