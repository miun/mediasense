package se.miun.mediasense.disseminationlayer.communication.rudp;

import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.exceptions.RUDPDestinationNotReachableException;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;

public class RUDP implements CommunicationInterface {
	//Upper layer
	private DisseminationCore core;
	
	//
	
	public RUDP(DisseminationCore core) {
		this.core = core;
	}
	
	@Override
	public void sendMessage(Message message) throws RUDPDestinationNotReachableException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLocalIp() {
		return "127.0.0.1";
	}

	@Override
	public void shutdown() {
		//TODO finish all packets that are still in queue
		//do not take new ones
	}
	
	private void handleIncomingPacket(Message msg) {
		core.handleMessage(msg);
	}
}
