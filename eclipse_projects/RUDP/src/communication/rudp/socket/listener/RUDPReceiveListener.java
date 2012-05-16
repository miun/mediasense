package communication.rudp.socket.listener;

import communication.rudp.socket.datagram.RUDPAbstractDatagram;

public interface RUDPReceiveListener {
	public void onRUDPDatagramReceive(RUDPAbstractDatagram datagram);
}
