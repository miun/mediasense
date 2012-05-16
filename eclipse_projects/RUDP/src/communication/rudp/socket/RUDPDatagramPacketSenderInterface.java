package communication.rudp.socket;

import communication.rudp.socket.datagram.RUDPDatagramPacket;

public interface RUDPDatagramPacketSenderInterface {
	public void sendDatagramPacket(RUDPDatagramPacket p);
	public void eventLinkFailed();
}
