package communication.rudp.socket;

public interface RUDPSocketInterface {
	public void triggerSend(RUDPLink link,RUDPDatagramPacket packet);
}
