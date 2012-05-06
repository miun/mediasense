package communication;

import communication.rudp.RUDP;

public class Main {

	public static void main(String[] args) {
		//Create 2 communication end points
		DisseminationCore core1 = new DisseminationCore();
		DisseminationCore core2 = new DisseminationCore();
		CommunicationInterface comm1 = new RUDP(core1);
		CommunicationInterface comm2 = new RUDP(core2);
		
		Message msg;
		
		//Send data
		msg = new TestMessage(comm1.getLocalIp(),comm2.getLocalIp(),null);
		
		try {
			comm1.sendMessage(msg);
		}
		catch (DestinationNotReachableException e) {
			
		}
		
		//Shutdown
		comm1.shutdown();
		comm2.shutdown();
	}
}
