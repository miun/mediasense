package communication;

import java.net.InetAddress;

import communication.rudp.socket.RUDPSocket;
import communication.rudp.socket.datagram.RUDPDatagram;

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

			sock = new RUDPSocket(23456);
			
			//Create data packet
			//for(int i = 0; i < data.length; i++) data[i] = (byte)i;
			
			
			this.start();
			Thread.sleep(1000);
			
			int n = 0;
			while(n++ < 200000) {
				data = new Integer(n).toString().getBytes();
				dgram = new RUDPDatagram(dst, 40000, data);
				sock.send(dgram);
				//Thread.sleep(1);
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
			sock = new RUDPSocket(40000);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		int number = 0;
		int newNumber;
		
		while(true) {
				
			try {
				data = sock.receive();
				newNumber = Integer.parseInt(new String(data));
				if(newNumber != number + 1) {
					System.out.println("FAIL " + newNumber);
				}
				number = newNumber;
					
				//System.out.println("Received " + data.length + " bytes of data");
				
				Thread.sleep(0);
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
