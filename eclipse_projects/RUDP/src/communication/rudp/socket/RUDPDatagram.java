package communication.rudp.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RUDPDatagram {
	private InetAddress dst;
	private int port;
	private byte[] data;
	
	public RUDPDatagram(InetAddress dst,int port,byte[] data) {
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
		return data;
	}
	
	public InetSocketAddress getSocketAddress() {
		return new InetSocketAddress(dst,port);
	}
}
