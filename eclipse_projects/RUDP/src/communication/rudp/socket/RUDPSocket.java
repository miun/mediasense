package communication.rudp.socket;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class RUDPSocket  {
	private DatagramSocket sock;
	private boolean failed = false;

	public RUDPSocket() throws SocketException {
		this.sock = new DatagramSocket();
	}

	public RUDPSocket(int port)  throws SocketException {
		this.sock = new DatagramSocket(port);
	}
	
	public RUDPSocket(SocketAddress bindaddr) throws SocketException {
		this.sock = new DatagramSocket(bindaddr);
	}

	public RUDPSocket(int port,InetAddress inetaddr) throws SocketException {
		this.sock = new DatagramSocket(port,inetaddr);
	}
	
	public void send(RUDPDatagram datagram) throws SocketException {
	}
	
	public void receive(RUDPDatagram datagram) throws SocketException {
	}
	
	public boolean isFailed() {
		return failed;
	}
	
	public void restore() {
		failed = false;
	}
	

}
