package communication.rudp.socket;

public interface RUDPSendTimeoutListener {
	public void onSendTimeout(RUDPDatagramPacket p);
}
