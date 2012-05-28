package communication;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Random;

import communication.rudp.socket.RUDPSocket;
import communication.rudp.socket.datagram.RUDPDatagram;

public class Main extends Thread {
	public RUDPSocket send;
	public RUDPSocket receive;
	
	byte[][] data;

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		this.start();
		
		data = new byte[1024][];
		
		for(byte[] d : data) {
			d = new byte[1024];
			new Random().nextBytes(d);
		}
		
		
		RUDPDatagram dgram;

		try {

			InetAddress dst = InetAddress.getByName("localhost");
			send = new RUDPSocket(23456);
			
			
			Thread.sleep(1000);
			
			for(byte[] d : data) {
				dgram = new RUDPDatagram(dst, 40000, d);
				
				try {
					send.send(dgram);
				}
				catch (Exception e) {
					e.printStackTrace();
					send.rehabilitateLink(new InetSocketAddress(dst,40000));
				}
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
		byte[][] receiveBuf = new byte[1024][];
		
		try {
			receive = new RUDPSocket(40000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		while(true) {
				
			try {
				receiveBuf[i] = receive.receive();
				
				if(receiveBuf[i].equals(data[i])) {
					System.out.println(i + "received and okay");
				}
				else {
					System.out.println(i + "received and NOT okay");
				}
			}
			catch(DestinationNotReachableException e1) {
				System.out.println("Destination not reachable");
			}
			catch(InterruptedException e2) {
				System.out.println("Thread interrupted!");
				break;
			}
		i++;
		}
	}
}
