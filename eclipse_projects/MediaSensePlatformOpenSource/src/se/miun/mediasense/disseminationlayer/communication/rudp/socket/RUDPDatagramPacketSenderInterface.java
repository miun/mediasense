package se.miun.mediasense.disseminationlayer.communication.rudp.socket;

import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketOut;

public interface RUDPDatagramPacketSenderInterface {
	public void sendDatagramPacket(RUDPDatagramPacketOut p);
}
