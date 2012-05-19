package communication.rudp.socket.listener;

import communication.rudp.socket.RUDPLink;

public interface RUDPLinkTimeoutListener {
	public void onLinkTimeout(RUDPLink link);
}
