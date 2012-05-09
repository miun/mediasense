package communication.rudp.socket.listener;

import communication.rudp.socket.RUDPDatagram;

public interface RUDPReceiveListener {
	public void onRUDPDatagramReceive(RUDPDatagram datagram);
}
