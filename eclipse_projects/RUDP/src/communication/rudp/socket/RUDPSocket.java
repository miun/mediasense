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
import communication.rudp.socket.datagram.RUDPAbstractDatagram;
import communication.rudp.socket.datagram.RUDPDatagram;
import communication.rudp.socket.datagram.RUDPDatagramPacket;
import communication.rudp.socket.datagram.RUDPDatagramPacketOut;
import communication.rudp.socket.datagram.RUDPExceptionDatagram;
import communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import communication.rudp.socket.listener.RUDPReceiveListener;

public class RUDPSocket extends Thread implements RUDPSocketInterface,RUDPLinkTimeoutListener,RUDPReceiveListener {
	private DatagramSocket sock;
	private DatagramPacket recv_buffer;
	
	private LinkedBlockingQueue<RUDPAbstractDatagram> recv_queue;
	private HashMap<InetSocketAddress,RUDPLink> links;
	private Timer timer;
	
	public RUDPSocket(DatagramSocket sock) {
		//Set socket
		this.sock = sock;
		
		//Receive buffer, timer and link map
		recv_buffer = new DatagramPacket(new byte[RUDPDatagramPacket.MAX_PACKET_SIZE],RUDPDatagramPacket.MAX_PACKET_SIZE);
		recv_queue = new LinkedBlockingQueue<RUDPAbstractDatagram>();
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
		
		//Set buffer as big as WINDOW_SIZE * MAX_PACKET_SIZE;
		try {
			//int size = sock.getReceiveBufferSize();
			sock.setReceiveBufferSize(RUDPLink.WINDOW_SIZE * RUDPDatagramPacket.MAX_PACKET_SIZE);
		}
		catch (SocketException e) {
			System.out.println(e.getMessage());
		}
		
		//Receive thread
		while(true) {
			try {
				//Receive datagram
				sock.receive(recv_buffer);
				sa = new InetSocketAddress(recv_buffer.getAddress(),recv_buffer.getPort());
				
				//Find or create link
				synchronized(links) {
					link = links.get(sa);
					if(link == null) {
						link = new RUDPLink(sa,this,this,this,timer);
						links.put(sa,link);
					}
				}
				
				//Create a copy of the received data
				int packetLength = recv_buffer.getLength();
				byte[] packetBuffer = new byte[packetLength];
				System.arraycopy(recv_buffer.getData(),0,packetBuffer, 0,packetLength);
				
				//Forward
				link.putReceivedData(packetBuffer);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(RUDPDatagram datagram) throws DestinationNotReachableException,InterruptedException {
		RUDPLink link;
		InetSocketAddress sa;
		
		//Get or create link
		sa = datagram.getSocketAddress();

		synchronized(links) {
			link = links.get(sa);
			
			if(link == null) {
				link = new RUDPLink(sa,this,this,this,timer);
				links.put(sa, link);
			}
		}
			
		//Process send request in link
		link.sendDatagram(datagram);
	}

	@Override
	public void onLinkTimeout(RUDPLink link) {
		//Link timed out => remove it from list
		synchronized(links) {
			links.remove(link.getSocketAddress());
		}
	}

	@Override
	public void onRUDPDatagramReceive(RUDPAbstractDatagram datagram) {
		//Enqueue datagram for delivering
		try {
			recv_queue.put(datagram);
		}
		catch (InterruptedException e) {
			//To nothing here
		}
	}

	public byte[] receive() throws InterruptedException,DestinationNotReachableException {
		RUDPAbstractDatagram abstractDgram;
		RUDPExceptionDatagram exceptionDgram;
		RUDPDatagram dgram;
		RUDPLink link;
		
		//Take the next datagram
		abstractDgram = recv_queue.take();
		
		//Cast
		if(abstractDgram.getClass().equals(RUDPDatagram.class)) {
			dgram = (RUDPDatagram)abstractDgram;
			
			//Inform link about datagram consumption
			synchronized(links) {
				link = links.get(dgram.getSocketAddress());
				if(link != null) {
					link.datagramConsumed();
				}
			}
			
			//Return data
			return dgram.getData();
		}
		else {
			//Link failed
			exceptionDgram = (RUDPExceptionDatagram)abstractDgram;
			throw exceptionDgram.getException();
		}
	}

	@Override
	public void sendDatagramPacket(RUDPDatagramPacketOut packet,InetSocketAddress sa) {
		DatagramPacket dgram;
		byte[] data;
		
		//Serialize 
		data = packet.serializePacket();
		
		try {
			//TODO remove debug output
			//System.out.println("SEND\n" + packet.toString(sa.getPort()) + "\n");

			//Create UDP datagram and send it
			dgram = new DatagramPacket(data,data.length,sa);
			sock.send(dgram);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rehabilitateLink(InetSocketAddress sockAddr) {
		RUDPLink link;
		
		synchronized(links) {
			link = links.get(sockAddr);
			if(link != null) {
				link.rehabilitate();
			}
		}
	}
}
