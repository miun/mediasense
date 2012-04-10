package manager;

import manager.dht.NodeID;

public interface CommunicationInterface {
	
	public final static int TCP = 1;
	public final static int UDP = 2;
	public final static int RUDP = 3;
	public final static int SCTP = 4;	
	public final static int TCP_PROXY = 5;
	
	public void sendMessage(Message message);
	
	public String getLocalIp();
	
	/*public void shutdown();
	*/
	
	//TODO remove DEBUG stuff
	public void fireFingerChangeEvent(int eventType,NodeID node,NodeID finger);
	public void fireKeepAliveEvent(NodeID key,String networkAddress);
}
