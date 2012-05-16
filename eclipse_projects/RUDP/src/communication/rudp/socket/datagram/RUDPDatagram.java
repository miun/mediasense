package communication.rudp.socket.datagram;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RUDPDatagram extends RUDPAbstractDatagram {
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
}
