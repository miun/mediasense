package communication;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import communication.rudp.socket.RUDPSocket;
import communication.rudp.socket.datagram.RUDPDatagram;

public class Main extends Thread {
	public static final int BUFFER_SIZE = 1024;
	public static final int PORT_SRC = 23456;
	public static final int PORT_DST = 40000;
	public static final String hostname = "10.14.1.164";

	private boolean useTCP;
	
	private Timer timer;
	private TimerTask refreshTask;

	private long dataCount = 0;
	private Date startDate = null;

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Usage rudp rudp | tcp\n\tSpecify rudp or tcp");
		}
		else {
			if(args[0].toLowerCase().compareTo("rudp") == 0) {
				new Main(false);
			}
			else if(args[0].toLowerCase().compareTo("rudp") == 0) {
				new Main(true);
			}
			else {
				System.out.println("Invalid argument. Specify tcp or rudp");
			}
		}
	}
	
	public Main(boolean useTCP) {
		RUDPSocket sockSend = null;
		Socket tcpSend = null;
		RUDPDatagram dgram;
		Integer checkCounter = 0;
		
		//Start receive thread
		this.useTCP = useTCP;
		this.start();
		
		//Wait for thread to startup
		try {
			this.start();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}

		//Create buffer
		byte buffer[] = new byte[BUFFER_SIZE];
		new Random().nextBytes(buffer);
		
		//Start measurement timer
		timer = new Timer();
		refreshTask = new RefreshTask();

		try {
			//Init.
			InetAddress dst = InetAddress.getByName(Main.hostname);
			timer.schedule(refreshTask, 500,500);

			//Create connection end point
			if(useTCP) {
				tcpSend = new Socket(dst,PORT_SRC);
			}
			else { //use RUDP
				sockSend = new RUDPSocket(PORT_SRC);
			}

			while(true) {
				//Insert running number into random byte array
				byte[] number = ByteBuffer.allocate(4).putInt(checkCounter).array();
				System.arraycopy(number, 0, buffer, 0, 4);
				dgram = new RUDPDatagram(dst, 40000, buffer);
				checkCounter++;
				
				if(useTCP) {
					tcpSend.getOutputStream().write(buffer);
				}
				else { //use RUDP
					sockSend.send(dgram);
				}
			}
		}
		catch (DestinationNotReachableException dste) {
			System.out.println("RUDP link failed!");
			return;
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void run() {
		RUDPSocket sockRecv = null;
		ServerSocket tcpServer;
		Socket tcpRecv;
		DataInputStream tcpStream = null;
		byte[] buffer = null;
		int currentCheck;
		int oldCheck = -1;
		
		//Create connection end points
		try {
			if(useTCP) {
				tcpServer = new ServerSocket(PORT_DST);
				tcpRecv = tcpServer.accept();
				tcpStream = new DataInputStream(tcpRecv.getInputStream());
				buffer = new byte[BUFFER_SIZE];
			}
			else { //use RUDP
				sockRecv = new RUDPSocket(PORT_DST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			while(true) {
				//Start measuring with first data packet
				if(startDate == null) {
					startDate = new Date();
					timer.schedule(new RefreshTask(), 500,500);
				}

				if(useTCP) {
					tcpStream.readFully(buffer);
				}
				else { //use RUDP
					buffer = sockRecv.receive();
				}

				//Check data
				currentCheck = ByteBuffer.wrap(buffer,0,4).getInt();
				if(currentCheck != oldCheck + 1) {
					System.out.println("EPIC FAIL");
				}

				oldCheck = currentCheck;
				dataCount += buffer.length;
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
}
