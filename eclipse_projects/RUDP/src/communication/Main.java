package communication;

import java.net.InetAddress;

import communication.rudp.socket.RUDPDatagram;
import communication.rudp.socket.RUDPSocket;

public class Main extends Thread {
	private RUDPSocket sock;
	public InetAddress dst;

	public byte[] data;
	public byte who = 'F';
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		//Create 2 communication end points
		/*DisseminationCore core1 = new DisseminationCore();
		DisseminationCore core2 = new DisseminationCore();
		CommunicationInterface comm1 = new RUDP(core1);
		CommunicationInterface comm2 = new RUDP(core2);*/

		data = new byte[1024];

		//Message msg;
		try {
			RUDPDatagram dgram;

			InetAddress dst = InetAddress.getByName("10.14.1.73");

			sock = new RUDPSocket(23456);
			
			//Create data packet
			for(int i = 0; i < data.length; i++) data[i] = (byte)i;
			data[0] = who;
			dgram = new RUDPDatagram(dst, 23456, data);
			
			this.start();
			Thread.sleep(1000);
			
			while(true) {
//				sock.send(dgram);
//				Thread.sleep(50);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//Shutdown
		//comm1.shutdown();
	}

	@Override
	public void run() {
		byte[] data;
		
		while(true) {
			try {
				data = sock.receive();
				
				if(data[0] != who) {
					//Return data if its not from us!
					try {
//						sock.send(new RUDPDatagram(dst, 23456, data));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				//System.out.println("Data received with length " + data.length);
			}
			catch(DestinationNotReachableException e1) {
				System.out.println("Destination not reachable");
			}
			/*catch(InterruptedException e2) {
				
			}*/
		}
	}
}
