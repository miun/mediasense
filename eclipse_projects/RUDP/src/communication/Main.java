package communication;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import communication.rudp.socket.RUDPSocket;
import communication.rudp.socket.datagram.RUDPDatagram;

public class Main extends Thread {
	public static final int BUFFER_SIZE = 10240;
	public long dataCount = 0;
	public Date startDate;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		RUDPSocket sockSend;
		RUDPDatagram dgram;

		Timer timer;
		TimerTask refreshTask;
		
		Integer checkCounter = 0;
		
		byte buffer[] = new byte[BUFFER_SIZE];
		new Random().nextBytes(buffer);
		
		this.start();

		timer = new Timer();
		refreshTask = new RefreshTask();
		
		try {
			InetAddress dst = InetAddress.getByName("localhost");
			sockSend = new RUDPSocket(23456);
			
			Thread.sleep(1000);
			startDate = new Date();
			timer.schedule(refreshTask, 500,500);

			while(true) {
				byte[] number = ByteBuffer.allocate(4).putInt(checkCounter).array();
				System.arraycopy(number, 0, buffer, 0, 4);
				dgram = new RUDPDatagram(dst, 40000, buffer);
				checkCounter++;
				
				try {
					sockSend.send(dgram);
					//Thread.sleep(1);
				}
				catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		RUDPSocket sockRecv;
		byte[] data;
		int currentCheck;
		int oldCheck = -1;
		
		try {
			sockRecv = new RUDPSocket(40000);

			while(true) {
				data = sockRecv.receive();
				
				//Check data
				currentCheck = ByteBuffer.wrap(data,0,4).getInt();
				if(currentCheck != oldCheck + 1) {
					System.out.println("EPIC FAIL");
				}
				else {
					oldCheck = currentCheck;
				}
				
				dataCount += data.length;
				//System.out.println(dataCount);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private class RefreshTask extends TimerTask {
		@Override
		public void run() {
			long time;
			
			time = new Date().getTime() - startDate.getTime();
			System.out.println((double)dataCount / 1024 / 1024 / ((double)time / 1000) + " MB/s");
		}
	}
	
	/*	@Override
	public void run() {
		byte[] data;
		
		int number = 0;
		int newNumber;
		
		while(true) {
				
			try {
				data = sock.receive();
				newNumber = Integer.parseInt(new String(data));
				if(newNumber != number + 1) {
					System.out.println("FAIL " + newNumber);
				}
				else {
					number = newNumber;
					System.out.println(newNumber + " received");
				}
			}
			catch(DestinationNotReachableException e1) {
				System.out.println("Destination not reachable");
			}
			catch(InterruptedException e2) {
				System.out.println("Thread interrupted!");
				break;
			}
		}
	}*/
}
