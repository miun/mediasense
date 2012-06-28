package se.miun.mediasense.disseminationlayer.communication.rudp.socket.listener;

import se.miun.mediasense.disseminationlayer.communication.rudp.socket.RUDPLink;

public interface RUDPLinkTimeoutListener {
	public void onLinkTimeout(RUDPLink link);
}
