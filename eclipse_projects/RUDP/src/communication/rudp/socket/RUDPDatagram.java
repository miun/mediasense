package communication.rudp.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RUDPDatagram {
	private InetSocketAddress address;
	private byte[] data;
	
	public RUDPDatagram(InetSocketAddress socketAddress,byte[] data) {
		//Datagram contains data
		this.address = socketAddress;
		this.data = data;
	}

	public RUDPDatagram(InetAddress address,int port,byte[] data) {
		//Datagram contains data
		this.address = new InetSocketAddress(address,port);
		this.data = data;
	}

	public InetSocketAddress getSocketAddress() {
		return address;
	}

	public int getPort() {
		return address.getPort();
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Exception getException() {
		return null;
	}
}
