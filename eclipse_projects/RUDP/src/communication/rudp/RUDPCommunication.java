package communication.rudp;

import communication.CommunicationInterface;
import communication.DestinationNotReachableException;
import communication.DisseminationCore;
import communication.Message;

public class RUDPCommunication implements CommunicationInterface {
	//Upper layer
	private DisseminationCore core;
	
	//
	
	public RUDPCommunication(DisseminationCore core) {
		this.core = core;
	}
	
	@Override
	public void sendMessage(Message message) throws DestinationNotReachableException {
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
