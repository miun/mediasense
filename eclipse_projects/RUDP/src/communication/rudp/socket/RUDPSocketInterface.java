package communication.rudp.socket;

import java.net.InetSocketAddress;

import communication.rudp.socket.datagram.RUDPDatagramPacketOut;

public interface RUDPSocketInterface {
	public void sendDatagramPacket(RUDPDatagramPacketOut packet,InetSocketAddress sa);
}
