package communication;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import communication.rudp.socket.RUDPSocket;
import communication.rudp.socket.datagram.RUDPDatagram;

public class Main extends Thread {
	public static RUDPSocket sock;

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		this.start();
		//Create 2 communication end points
		//DisseminationCore core1 = new DisseminationCore();
		//DisseminationCore core2 = new DisseminationCore();
		//CommunicationInterface comm1 = new RUDP(core1);
		//CommunicationInterface comm2 = new RUDP(core2);

		byte data[] = new byte[1024];

		//Message msg;
		try {
			InetAddress dst = InetAddress.getByName("10.13.1.150");
			RUDPDatagram dgram;

			sock = new RUDPSocket(23456);
			
			this.start();
			Thread.sleep(1000);
			
			int n = 0;
			while(n++ < 200000) {
				data = new Integer(n).toString().getBytes();
				dgram = new RUDPDatagram(dst, 23456, data);
				
				try {
					sock.send(dgram);
					System.out.println(n + " sent");
				}
				catch (Exception e) {
					e.printStackTrace();
					sock.rehabilitateLink(new InetSocketAddress(dst,40000));
				}
				//Thread.sleep(1);
			}
			
			System.out.println("Done!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//Shutdown
		//comm1.shutdown(); */
	}

	@Override
	public void run() {
		byte[] data;
		
		int number = 0;
		int newNumber;
		
		while(true) {
				
			try {
				data = Main.sock.receive();
				newNumber = Integer.parseInt(new String(data));
				if(newNumber != number + 1) {
					System.out.println("FAIL " + newNumber);
				}
				number = newNumber;
				System.out.println(newNumber + " received");
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
