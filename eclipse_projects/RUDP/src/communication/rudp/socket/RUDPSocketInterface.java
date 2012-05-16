package communication.rudp.socket;

import java.net.InetSocketAddress;

public interface RUDPSocketInterface {
	public void sendDatagramPacket(RUDPDatagramPacket packet,InetSocketAddress sa);
}
