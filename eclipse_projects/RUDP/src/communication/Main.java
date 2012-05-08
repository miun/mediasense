package communication;

import communication.rudp.RUDP;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class Main {

	public static void main(String[] args) {
		//Create 2 communication end points
		DisseminationCore core1 = new DisseminationCore();
		DisseminationCore core2 = new DisseminationCore();
		CommunicationInterface comm1 = new RUDP(core1);
		CommunicationInterface comm2 = new RUDP(core2);
		
		Message msg;
		
		DeltaRangeList drl = new DeltaRangeList();
		
		drl.add(1);
		drl.add(2);
		drl.add(3);
		drl.add(5);
		drl.add(6);
		drl.add(7);

		Integer[] array = drl.toDifferentialArray();
		
		for(Integer i: array) {
			System.out.println(i);
		}
		
		drl.add(4);
		
		array = drl.toDifferentialArray();
		
		for(Integer i: array) {
			System.out.println(i);
		}

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
