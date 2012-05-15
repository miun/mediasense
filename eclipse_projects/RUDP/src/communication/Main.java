package communication;

import java.net.InetAddress;

import communication.rudp.socket.RUDPDatagram;
import communication.rudp.socket.RUDPSocket;

public class Main extends Thread {
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		//Create 2 communication end points
		/*DisseminationCore core1 = new DisseminationCore();
		DisseminationCore core2 = new DisseminationCore();
		CommunicationInterface comm1 = new RUDP(core1);
		CommunicationInterface comm2 = new RUDP(core2);*/

		byte data[] = new byte[1024];

		//Message msg;
		try {
			InetAddress dst = InetAddress.getByName("localhost");
			RUDPSocket sock;
			RUDPDatagram dgram;

			sock = new RUDPSocket(40000);
			
			//Create data packet
			for(int i = 0; i < data.length; i++) data[i] = (byte)i;
			dgram = new RUDPDatagram(dst, 23456, data);
			
			this.start();
			Thread.sleep(1000);
			
			int n = 0;
			while(n++ < 20) {
				sock.send(dgram);
			}
			
			System.out.println("Done!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//Shutdown
		//comm1.shutdown();
	}

	@Override
	public void run() {
		RUDPSocket sock;
		byte[] data;
		
		try {
			sock = new RUDPSocket(23456);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		while(true) {
			try {
				data = sock.receive();
				System.out.println("Received " + data.length + " bytes of data");
				
				Thread.sleep(1000);
			}
			catch(DestinationNotReachableException e1) {
				System.out.println("Destination not reachable");
			}
			catch(InterruptedException e2) {
				System.out.println("Thread interrupted!");
				break;
			}
		}
	}
}
