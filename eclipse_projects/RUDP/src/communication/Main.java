package communication;

import java.net.InetAddress;
import java.util.Random;

import communication.rudp.socket.RUDPDatagram;
import communication.rudp.socket.RUDPSocket;

public class Main extends Thread {
	private RUDPSocket sock;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		//Create 2 communication end points
		/*DisseminationCore core1 = new DisseminationCore();
		DisseminationCore core2 = new DisseminationCore();
		CommunicationInterface comm1 = new RUDP(core1);
		CommunicationInterface comm2 = new RUDP(core2);*/

		String who = "flo";
		

		//Message msg;
		try {
			RUDPDatagram dgram;
			InetAddress dst = InetAddress.getByName("10.14.1.72");
			sock = new RUDPSocket(23456);

			//Create data packet
			byte[] data = new byte[128];
			for(int i = 0; i < 128; i++) data[i] = (byte)i;
			dgram = new RUDPDatagram(dst, 23456, data);
			
			Thread.sleep(1000);
			
			while(true) {
				sock.send(dgram);
				Thread.sleep(1000);
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
	}

	@Override
	public void run() {
		byte[] data;
		
		while(true) {
			try {
				data = sock.receive();
				System.out.println("Data received with length " + data.length);
			}
			catch(DestinationNotReachableException e1) {
				System.out.println("Destination not reachable");
			}
			/*catch(InterruptedException e2) {
				
			}*/
		}
	}
}
