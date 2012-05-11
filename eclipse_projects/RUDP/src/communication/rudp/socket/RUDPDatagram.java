package communication.rudp.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RUDPDatagram {
	private Exception exception;
	private InetAddress dst;
	private int port;
	private byte[] data;
	
	public RUDPDatagram(InetAddress dst,int port,Exception e) {
		//Datagram contains an exception
		this.dst = dst;
		this.port = port;
		exception = e;
	}
	
	public RUDPDatagram(InetAddress dst,int port,byte[] data) {
		//Datagram contains data
		this.dst = dst;
		this.port = port;
		this.data = data;
	}
	
	public InetAddress getDst() {
		return dst;
	}

	public int getPort() {
		return port;
	}

	public byte[] getData() {
		//Return received data
		return data;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public InetSocketAddress getSocketAddress() {
		return new InetSocketAddress(dst,port);
	}
}
