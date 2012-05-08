package communication.rudp.socket;

import java.net.InetSocketAddress;

public interface RUDPLinkTimeoutListener {
	public void onLinkTimeout(InetSocketAddress sa,RUDPLink link);
}
