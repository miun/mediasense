package communication.rudp.socket;

import communication.rudp.socket.datagram.RUDPDatagramPacketOut;

public interface RUDPDatagramPacketSenderInterface {
	public void sendDatagramPacket(RUDPDatagramPacketOut p);
}
