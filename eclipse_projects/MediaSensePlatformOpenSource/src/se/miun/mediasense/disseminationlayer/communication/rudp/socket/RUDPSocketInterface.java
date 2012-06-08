package se.miun.mediasense.disseminationlayer.communication.rudp.socket;

import java.net.InetSocketAddress;

import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketOut;


public interface RUDPSocketInterface {
	public void sendDatagramPacket(RUDPDatagramPacketOut packet,InetSocketAddress sa);
}
