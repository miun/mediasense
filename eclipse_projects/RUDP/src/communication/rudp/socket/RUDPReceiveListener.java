package communication.rudp.socket;

public interface RUDPReceiveListener {
	public void onRUDPDatagramReceive(RUDPDatagram datagram);
}
