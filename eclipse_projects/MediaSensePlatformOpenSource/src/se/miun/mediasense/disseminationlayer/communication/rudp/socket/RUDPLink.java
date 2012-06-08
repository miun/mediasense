package se.miun.mediasense.disseminationlayer.communication.rudp.socket;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagram;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacket;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketIn;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagramPacketOut;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPExceptionDatagram;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.exceptions.InvalidRUDPPacketException;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.exceptions.RUDPDestinationNotReachableException;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.listener.RUDPLinkFailListener;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.listener.RUDPReceiveListener;

public class RUDPLink implements RUDPLinkFailListener,RUDPDatagramPacketSenderInterface {
	private static final int LINK_TIMEOUT_PERIOD = 5 * 1000 * 60; //5 minutes
	public static final int WINDOW_SIZE = 1000;
	public static final int WINDOW_SIZE_BOOST = 50;

	//The RUDP socket interface
	private RUDPSocketInterface socketInterface;
	
	//Sender and receiver
	private RUDPSender sender;
	private RUDPReceiver receiver;

	//Address this link is associated with
	private InetSocketAddress remoteSockAddr;

	//Timeout
	private Timer timer;
	private RUDPReceiveListener receiveListener;
	private RUDPLinkTimeoutListener timeoutListener;
	private TimeoutTask timeoutTask;
	private Date lastAction;
	
	private boolean receiverSynced;
	private boolean linkFailed;

	public RUDPLink(InetSocketAddress sockAddr,RUDPSocketInterface socketInterface,RUDPLinkTimeoutListener timeoutListener,RUDPReceiveListener receiveListener,Timer timer) {
		//Create an own timer if no one was specified
		if(timer == null) {
			timer = new Timer();
		}
		
		//The state flags
		receiverSynced = false;
		linkFailed = false;

		//Set timer and listener
		this.receiveListener = receiveListener;
		this.socketInterface = socketInterface;
		this.timeoutListener = timeoutListener;
		this.timer = timer;

		//Network address
		this.remoteSockAddr = sockAddr;
		
		sender = new RUDPSender(this,this,timer);
		receiver = new RUDPReceiver(this,receiveListener,remoteSockAddr,timer);

		//Rehabilitate
		rehabilitate();
	}
	
	public InetSocketAddress getSocketAddress() {
		return remoteSockAddr;
	}
	
	public void sendDatagram(RUDPDatagram datagram) throws InterruptedException,RUDPDestinationNotReachableException {
		try {
			sender.sendDatagram(datagram);
		}
		catch (RUDPDestinationNotReachableException e) {
			//Insert address
			throw new RUDPDestinationNotReachableException(remoteSockAddr);
		}
	}
	
	@Override
	public void sendDatagramPacket(RUDPDatagramPacketOut datagramPacket) {
		synchronized(this) {
			if(!linkFailed) {
				//Set window size
				datagramPacket.setWindowSize(WINDOW_SIZE);
			
				//Add the ACK overlay stream
				receiver.setAckStream(datagramPacket);
			}
		}
		
		//Forward to socket interface
		socketInterface.sendDatagramPacket(datagramPacket,remoteSockAddr);
	}
	
	public void handleAckPacket(RUDPDatagramPacket packet) {
		synchronized(this) {
			if(!linkFailed) {
				//Forward acknowledge data to sender
				if(packet.getFlag(RUDPDatagramPacket.FLAG_ACK)) {
					sender.handleAckData(packet.getAckWindowStart(), packet.getAckSeqData());
				}
			}
		}
	}
	
	public void putReceivedData(byte[] data) {
		RUDPDatagramPacketIn packet;
		
		try {
			//Extract packet
			packet = new RUDPDatagramPacketIn(data);
		}
		catch (InvalidRUDPPacketException e1) {
			System.out.println("INVALID PACKET RECEIVED");
			return;
		}
		
		//Handle reset packets
		handleResetFlag(packet);

		//Handle new window sequence
		handleFirstPacket(packet);
		
		//Handle persist packets
		handlePersistPacket(packet);
		
		//Update window size
		sender.setReceiverWindowSize(packet.getWindowSize());
		
		//Handle ACK-data
		handleAckPacket(packet);
		
		//Handle data packet
		handleDataPacket(packet);
	}
	
	public void datagramConsumed() {
		//Forward that a datagram has been consumed
		synchronized(this) {
			if(!linkFailed) {
				receiver.datagramConsumed();
			}
		}
	}
	
	//Handle reset flag
	private void handleResetFlag(RUDPDatagramPacket packet) {
		if(packet.getFlag(RUDPDatagramPacket.FLAG_RESET)) {
			//Reset the sender
			sender.reset();
		}
	}
	
	private void handleFirstPacket(RUDPDatagramPacketIn packet) {
		synchronized(this) {
			if(!linkFailed) {
				if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST)) {
					if(receiverSynced) {
						//Reset receiver because it has already been initialized
						receiver.reset();
					}
					
					//Take the first sequence number as the receiver window start
					receiver.setReceiverWindowStart(packet.getPacketSeq());
					receiverSynced = true;
				} 
			}
		}
	}
	
	private void handleDataPacket(RUDPDatagramPacketIn packet) {
		RUDPDatagramPacketOut resetPacket;
		
		//Handle payload data
		synchronized(this) {
			if(!linkFailed) {
				if(packet.getFlag(RUDPDatagramPacket.FLAG_DATA)) {
					if(receiverSynced) {
						receiver.handlePayloadData(packet);
					}
					else {
						//Send a reset packet, because we need a first packet for synchronization first
						resetPacket = new RUDPDatagramPacketOut();
						resetPacket.setResetFlag(true);
						sendDatagramPacket(resetPacket);
					}
				}
			}
		}
	}
	
	private void handlePersistPacket(RUDPDatagramPacketIn packet) {
		//Send an ACK packet back if this was an persist packet
		synchronized(this) {
			if(!linkFailed && receiverSynced) {
				if(packet.getFlag(RUDPDatagramPacket.FLAG_PERSIST)) {
					//create packet
					RUDPDatagramPacketOut ack = new RUDPDatagramPacketOut();
					sendDatagramPacket(ack);
				}
			}
		}
	}
	
	private class TimeoutTask extends TimerTask {
		private RUDPLink link;
		
		public TimeoutTask(RUDPLink link) {
			this.link = link;
		}
		
		@Override
		public void run() {
			synchronized(link) {
				Date now = new Date();
				long interval;
				
				//Calculate interval
				interval = now.getTime() - lastAction.getTime(); 
				
				if(interval >= LINK_TIMEOUT_PERIOD) {
					//Timeout => trigger link removal
					timeoutListener.onLinkTimeout(link);
				}
				else {
					//Wait for the remaining period
					timeoutTask = new TimeoutTask(link);
					timer.schedule(timeoutTask,interval);
				}
			}
		}
	}
	
	//This function gets called when a packet finally failed to send
	public void eventLinkFailed() {
		synchronized(this) {
			linkFailed = true;

			//Stop and reset sender and receiver immediately
			sender.shutdown();
			receiver.shutdown();
			
			//Produce exception and forward it
			RUDPExceptionDatagram dgram = new RUDPExceptionDatagram(remoteSockAddr);
			receiveListener.onRUDPDatagramReceive(dgram);
		}
	}
	
	public void rehabilitate() {
		synchronized(this) {
			if(linkFailed) {
				//Create receiver and sender
				sender.rehabilitate();
				receiver.rehabilitate();
				
				//Prepare timeout task
				lastAction = new Date();
				timeoutTask = new TimeoutTask(this);
				timer.schedule(timeoutTask, LINK_TIMEOUT_PERIOD);
				
				//Reset fail state
				linkFailed = false;
				receiverSynced = false;
			}
		}
	}
	
	public void shutdown() {
		//Manually shut down link
		synchronized(this) {
			linkFailed = true;
	
			//Stop and reset sender and receiver immediately
			sender.shutdown();
			receiver.shutdown();
		}
	}
}