package communication.rudp.socket;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import communication.DestinationNotReachableException;
import communication.rudp.socket.datagram.RUDPDatagram;
import communication.rudp.socket.datagram.RUDPDatagramPacket;
import communication.rudp.socket.datagram.RUDPDatagramPacketIn;
import communication.rudp.socket.datagram.RUDPDatagramPacketOut;
import communication.rudp.socket.datagram.RUDPExceptionDatagram;
import communication.rudp.socket.exceptions.InvalidRUDPPacketException;
import communication.rudp.socket.listener.RUDPLinkFailListener;
import communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import communication.rudp.socket.listener.RUDPReceiveListener;

public class RUDPLink implements RUDPLinkFailListener {
	private static final int LINK_TIMEOUT_PERIOD = 5 * 1000 * 60; //5 minutes
	public static final int WINDOW_SIZE = 150;
	public static final int WINDOW_SIZE_BOOST = 100;

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
	
	private boolean firstPacket;
	private boolean linkSynced;
	private boolean linkFailed;

	public RUDPLink(InetSocketAddress sockAddr,RUDPSocketInterface socketInterface,RUDPLinkTimeoutListener timeoutListener,RUDPReceiveListener receiveListener,Timer timer) {
		//Create an own timer if no one was specified
		if(timer == null) {
			timer = new Timer();
		}
		
		//The state flags
		firstPacket = true;
		linkSynced = false;
		linkFailed = false;

		//Set timer and listener
		this.receiveListener = receiveListener;
		this.socketInterface = socketInterface;
		this.timeoutListener = timeoutListener;
		this.timer = timer;

		//Network address
		this.remoteSockAddr = sockAddr;
		
		sender = new RUDPSender(this, socketInterface, timer);
		receiver = new RUDPReceiver(this,receiveListener, timer);

		//Rehabilitate
		rehabilitate();
	}
	
	public InetSocketAddress getSocketAddress() {
		return remoteSockAddr;
	}
	
	public void sendDatagram(RUDPDatagram datagram) throws InterruptedException,DestinationNotReachableException {
		sender.sendDatagram(datagram);
	}
	
	public void sendDatagramPacket(RUDPDatagramPacketOut datagramPacket) {
		synchronized(this) {
			if(!linkFailed) {
				//Flag first packet
				if(firstPacket) {
					datagramPacket.setFirstFlag(true);
					firstPacket = false;
				}
				
				//Flag reset packet
				if(!linkSynced) {
					datagramPacket.setResetFlag(true);
				}
				
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

		//Update receiver window and 
		updateReceiverWindow(packet);
		
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
	
	public void updateReceiverWindow(RUDPDatagramPacket packet) {
		//Forward new window information
		synchronized(this) {
			if(!linkFailed) {
				sender.updateReceiverWindow(packet.getAckWindowStart(), packet.getWindowSize(),packet.getId());
			}
		}
	}
	
	//Handle reset flag
	private void handleResetFlag(RUDPDatagramPacket packet) {
		if(packet.getFlag(RUDPDatagramPacket.FLAG_RESET)) {
			//Reset sender
			synchronized(this) {
				firstPacket = true;
				sender.reset();
			}
		}
	}
	
	private void handleFirstPacket(RUDPDatagramPacketIn packet) {
		RUDPDatagramPacketOut resetPacket = null;
		
		synchronized(this) {
			if(!linkFailed) {
				if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST)) {
					//Reset receiver at FIRST packet
					receiver.reset();
					
					//Take the first sequence number as the receiver window start
					receiver.setReceiverWindowStart(packet.getPacketSeq());
					//sender.resetReceiverWindow(packet.getAckWindowStart(), packet.getWindowSize());
					
					//Receiver is sync'ed
					linkSynced = true;
				} 
				else if(!linkSynced) {
					//Send a reset packet, because we need a first packet for synchronization
					resetPacket = new RUDPDatagramPacketOut();
					resetPacket.setResetFlag(true);
					sendDatagramPacket(resetPacket);
				}
			}
		}
	}
	
	private void handleDataPacket(RUDPDatagramPacketIn packet) {
		//Handle payload data
		synchronized(this) {
			if(!linkFailed && linkSynced) {
				receiver.handlePayloadData(packet);
			}
		}
	}
	
	private void handlePersistPacket(RUDPDatagramPacketIn packet) {
		//Send an ack packet back if this was an persist packet
		synchronized(this) {
			if(!linkFailed && linkSynced) {
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
				linkSynced = false;
				firstPacket = true;
			}
		}
	}
}