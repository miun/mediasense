package communication.rudp.socket;

import java.net.InetSocketAddress;

import communication.rudp.socket.datagram.RUDPDatagramPacket;

public interface RUDPSocketInterface {
	public void sendDatagramPacket(RUDPDatagramPacket packet,InetSocketAddress sa);
}
