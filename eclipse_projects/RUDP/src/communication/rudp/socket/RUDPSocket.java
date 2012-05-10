package communication.rudp.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import communication.DestinationNotReachableException;
import communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import communication.rudp.socket.listener.RUDPReceiveListener;

public class RUDPSocket extends Thread implements RUDPSocketInterface,RUDPLinkTimeoutListener,RUDPReceiveListener {
	//The capacity defines the receive queue length in packets
	//The maximum size of each packet can be 64 kB
	private static final int QUEUE_CAPACITY = 128;
	
	private DatagramSocket sock;
	private DatagramPacket recv_buffer;
	private LinkedBlockingQueue<RUDPDatagram> recv_queue;
	private HashMap<InetSocketAddress,RUDPLink> links;
	private Timer timer;
	private boolean failed = false;

	public RUDPSocket(DatagramSocket sock) {
		//Set socket
		this.sock = sock;
		
		//Receive buffer, timer and link map
		recv_buffer = new DatagramPacket(new byte[65536],65536);
		recv_queue = new LinkedBlockingQueue<RUDPDatagram>(QUEUE_CAPACITY);
		timer = new Timer("RUDP timer");
		links = new HashMap<InetSocketAddress,RUDPLink>();
		
		//Start receive thread
		this.start();
	}
	
	public RUDPSocket() throws SocketException {
		this(new DatagramSocket());
	}

	public RUDPSocket(int port)  throws SocketException {
		this(new DatagramSocket(port));
	}
	
	public RUDPSocket(SocketAddress bindaddr) throws SocketException {
		this(new DatagramSocket(bindaddr));
	}

	public RUDPSocket(int port,InetAddress inetaddr) throws SocketException {
		this(new DatagramSocket(port,inetaddr));
	}
	
	@Override
	public void run() {
		RUDPLink link;
		InetSocketAddress sa;
		
		//Receive thread
		while(true) {
			try {
				//Receive datagram
				sock.receive(recv_buffer);
				sa = new InetSocketAddress(recv_buffer.getAddress(),recv_buffer.getPort());
				
				//Find or create link
				synchronized(links) {
					link = links.get(recv_buffer.getSocketAddress());
					if(link == null) {
						link = new RUDPLink(sa,this,this,this,timer);
						links.put(sa,link);
					}
				}
				
				//The link takes care of received data
				int packetLength = recv_buffer.getLength();
				byte[] packetBuffer = new byte[packetLength];
				System.arraycopy(recv_buffer.getData(),0,packetBuffer, 0,packetLength);
				link.putReceivedData(packetBuffer,packetLength);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(RUDPDatagram datagram) throws IOException {
		RUDPLink link;
		InetSocketAddress sa;
		
		//Get or create link
		sa = datagram.getSocketAddress();
		link = links.get(sa);
		
		if(link == null) {
			link = new RUDPLink(sa,this,this,this,timer);
			links.put(sa, link);
		}
		
		//Process send request in link
		link.send(datagram);
	}
	
	public boolean isFailed() {
		return failed;
	}
	
	public void restore() {
		failed = false;
	}
	
	@Override
	public void onLinkTimeout(InetSocketAddress sa,RUDPLink link) {
		//Link timed out => remove it from list
		links.remove(sa);
	}

	@Override
	public void onRUDPDatagramReceive(RUDPDatagram datagram) {
		//Enqueue datagram for delivering
		try {
			recv_queue.put(datagram);
		}
		catch (InterruptedException e) {
			//To nothing here
		}
	}

	public byte[] receive() throws DestinationNotReachableException {
		RUDPDatagram dgram;
		Exception sock_exception;
		
		//Throw exception if there is one
		try {
			//Take the next datagram
			dgram = recv_queue.take();
			sock_exception = dgram.getException();
			if(sock_exception != null) throw sock_exception;

			//Return data
			return dgram.getData();
		}
		catch(DestinationNotReachableException e) {
			//Forward exception to inform about a not reachable destination
			throw e;
		}
		catch (InterruptedException e) {
			//Do nothing
			return null;
		}
		catch (Exception exception) {
			//Other error should not happen
			exception.printStackTrace();
			return null;
		}
	}

	@Override
	public void triggerSend(RUDPLink link, RUDPDatagramPacket packet) {
		DatagramPacket dgram;
		byte[] data;
		
		//Serialize 
		data = packet.serializePacket();
		
		try {
			//Create UDP datagram and send it
			dgram = new DatagramPacket(data,data.length,link.getSocketAddress());
			sock.send(dgram);
			
			//Trigger send event
			//TODO find a better timeout mechanism
			//packet.triggerSend(link.getAvgRTT * 1.5);
			//packet.triggerSend(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
