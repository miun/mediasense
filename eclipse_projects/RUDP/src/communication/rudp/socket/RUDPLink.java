package communication.rudp.socket;

import java.net.InetSocketAddress;
import java.util.Timer;

import communication.rudp.socket.exceptions.InvalidRUDPPacketException;
import communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import communication.rudp.socket.listener.RUDPReceiveListener;

public class RUDPLink implements RUDPDatagramPacketSenderInterface {
	public static final int WINDOW_SIZE = 150;
	public static final int WINDOW_SIZE_BOOST = 100;

	//The RUDP socket interface
	private RUDPSocketInterface socketInterface;
	
	//Sender and receiver
	private RUDPSender sender;
	private RUDPReceiver receiver;

	//Address this link is associated with
	private InetSocketAddress remoteSockAddr;

	//Listener  & interfaces
	private RUDPLinkTimeoutListener timeoutListener;
	
	private boolean firstPacket;
	private boolean linkSynced;

	public RUDPLink(InetSocketAddress sockAddr,RUDPSocketInterface socketInterface,RUDPLinkTimeoutListener timeoutListener,RUDPReceiveListener receiveListener,Timer timer) {
		//Create an own timer if no one was specified
		if(timer == null) {
			timer = new Timer();
		}
		
		//The state flags
		firstPacket = true;
		linkSynced = false;

		//Set timer and listener
		this.socketInterface = socketInterface;
		this.timeoutListener = timeoutListener;

		//Create receiver and sender
		sender = new RUDPSender(this,socketInterface,timer);
		receiver = new RUDPReceiver(this,socketInterface,receiveListener,timer);

		//Network address
		this.remoteSockAddr = sockAddr;
	}
	
	public InetSocketAddress getSocketAddress() {
		return remoteSockAddr;
	}
	
	public void sendDatagram(RUDPDatagram datagram) throws InterruptedException {
		sender.sendDatagram(datagram);
	}
	
	public void sendDatagramPacket(RUDPDatagramPacket datagramPacket) {
		//Flag first packet
		synchronized(this) {
			if(firstPacket) {
				datagramPacket.setFirstFlag(true);
				firstPacket = false;
			}
		
			//Set window size
			datagramPacket.setWindowSize(WINDOW_SIZE);
	
			//Add the ACK overlay stream
			receiver.setAckStream(datagramPacket);
		}
		
		//Forward to socket interface
		socketInterface.sendDatagramPacket(datagramPacket,remoteSockAddr);
	}
	
	public void handleAckPacket(RUDPDatagramPacket packet) {
		//Forward acknowledge data to sender
		if(packet.getFlag(RUDPDatagramPacket.FLAG_ACK)) {
			sender.handleAckData(packet.getAckWindowStart(), packet.getAckSeqData());
		}
	}
	
	public void reset() {
		RUDPDatagramPacket resetPacket;

		//Send first packet again after reset
		synchronized(this) {
			firstPacket = true;
			linkSynced = false;
			
			//Reset both
			receiver.reset();
			sender.reset();
			
			//Send a reset packet, because we need a first packet for synchronization
			resetPacket = new RUDPDatagramPacket();
			resetPacket.setResetFlag(true);
		}
		
		sender.sendDatagramPacket(resetPacket);
		System.out.println("UNSYNCHRONIZED PACKET RECEIVED - FIRST PACKET MISSING");
	}

	public void putReceivedData(byte[] data) {
		RUDPDatagramPacket packet;
		
		try {
			//Extract packet
			packet = new RUDPDatagramPacket(data);
		}
		catch (InvalidRUDPPacketException e1) {
			System.out.println("INVALID PACKET RECEIVED");
			return;
		}
		
		System.out.println("RECEIVE\n" + packet.toString(getSocketAddress().getPort()) + "\n");

		//Handle reset packets
		handleResetFlag(packet);

		//Handle new window sequence
		handleFirstPacket(packet);

		//Update receiver window and 
		updateReceiverWindow(packet);
		
		//Handle ACK-data
		handleAckPacket(packet);
		
		//Handle data packet
		handleDataPacket(packet);
	}
	
	public void datagramConsumed() {
		//Forward that a datagram has been consumed
		receiver.datagramConsumed();
	}
	
	public void updateReceiverWindow(RUDPDatagramPacket packet) {
		//Forward new window information
		sender.updateReceiverWindow(packet.getAckWindowStart(), packet.getWindowSize());
	}
	
	//Handle reset flag
	private void handleResetFlag(RUDPDatagramPacket packet) {
		if(packet.getFlag(RUDPDatagramPacket.FLAG_RESET)) {
			//Inform link
			reset();
		}
	}
	
	private void handleFirstPacket(RUDPDatagramPacket packet) {
		synchronized(this) {
			if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST)) {
				//Take the first sequence number as the receiver window start
				sender.resetReceiverWindow(packet.getPacketSeq(),packet.getWindowSize());
				linkSynced = true;
			}
			else if (!linkSynced) {
				RUDPDatagramPacket resetPacket;
				
				//TODO is this correct ???
				resetPacket = new RUDPDatagramPacket();
				resetPacket.setResetFlag(true);
				socketInterface.sendDatagramPacket(resetPacket, remoteSockAddr);
				
				//Handle unsync'ed situation
				//reset();
			}
		}
	}
	
	private void handleDataPacket(RUDPDatagramPacket packet) {
		//Handle payload data
		if(linkSynced) {
			receiver.handlePayloadData(packet);
		}
	}
}