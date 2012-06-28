package se.miun.mediasense.disseminationlayer.communication.rudp.socket.listener;

import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPAbstractDatagram;

public interface RUDPReceiveListener {
	public void onRUDPDatagramReceive(RUDPAbstractDatagram datagram);
}
