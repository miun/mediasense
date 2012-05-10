package communication;

import java.net.InetAddress;
import java.util.Random;

import communication.rudp.socket.RUDPDatagram;
import communication.rudp.socket.RUDPSocket;

public class Main {

	public static void main(String[] args) {
		//Create 2 communication end points
		/*DisseminationCore core1 = new DisseminationCore();
		DisseminationCore core2 = new DisseminationCore();
		CommunicationInterface comm1 = new RUDP(core1);
		CommunicationInterface comm2 = new RUDP(core2);*/

		String who = "timo";
		
		//Message msg;
		try {

			if(who.equals("timo")) {
				RUDPDatagram dgram;
				InetAddress dst = InetAddress.getByName("10.13.1.122");
				RUDPSocket sock = new RUDPSocket();

				byte[] data = new byte[128];
				for(int i = 0; i < 128; i++) data[i] = (byte)i;
	
				while(true) {
					dgram = new RUDPDatagram(dst, 23456, data);
					sock.send(dgram);
					sock.send(dgram);
					
					Thread.sleep(1000);
				}
			}
			else if(who.equals("flo")) {
				RUDPSocket sock = new RUDPSocket(23456);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		//Send data
/*		msg = new TestMessage(comm1.getLocalIp(),comm2.getLocalIp(),null);
		
		try {
			comm1.sendMessage(msg);
		}
		catch (DestinationNotReachableException e) {
			
		}*/
		
		//Shutdown
		//comm1.shutdown();
		//comm2.shutdown();
	}
}
