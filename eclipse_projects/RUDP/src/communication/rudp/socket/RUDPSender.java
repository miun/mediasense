package communication.rudp.socket;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import communication.DestinationNotReachableException;
import communication.rudp.socket.datagram.RUDPDatagram;
import communication.rudp.socket.datagram.RUDPDatagramBuilder;
import communication.rudp.socket.datagram.RUDPDatagramPacket;
import communication.rudp.socket.datagram.RUDPDatagramPacketOut;
import communication.rudp.socket.listener.RUDPLinkFailListener;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPSender {
	private static final int MAX_DATA_PACKET_RETRIES = 5;
	private static final int PACKET_FIRST_RETRY_PERIOD = 250000000;
	private static final int PERSIST_PERIOD = 1000;

	//Link; Socket interface to send UDP datagrams
	//private RUDP link;
	private RUDPDatagramPacketSenderInterface sendInterface;
	private RUDPLinkFailListener failListener;
	//private InetAddress 
	
	//The sender is shutdown and waits for rehabilitate()
	private boolean isShutdown;
	private boolean firstPacket;
	
	//Timer
	private Timer timer;
	private TimerTask persistTask;
	
	//Semaphore to stay within window size
	private Semaphore semaphoreWindowSize;
	private int semaphorePermitCount;
	
	//Sequence of the next packet
	private int currentPacketSeq;

	//Contains sent, unprocessed packets
	private HashMap<Integer,RUDPDatagramPacketOut> packetBuffer_out;
	private int receiverWindowSize;
	private int senderWindowStart;
	//private boolean firstACK;
	
	public RUDPSender(RUDPDatagramPacketSenderInterface sendInterface,RUDPLinkFailListener failListener,Timer timer) {
		this.sendInterface = sendInterface;
		this.failListener = failListener;
		this.timer = timer;

		//Init buffer
		packetBuffer_out = new HashMap<Integer,RUDPDatagramPacketOut>();

		//Our initial sequence number
		currentPacketSeq = 100;
		receiverWindowSize = 1;
		senderWindowStart = currentPacketSeq;
		firstPacket = true;
		
		//Init semaphore
		this.semaphoreWindowSize = new Semaphore(1,true);
		semaphorePermitCount = 1;
	}

	public void sendDatagram(RUDPDatagram datagram) throws InterruptedException,DestinationNotReachableException {
		RUDPDatagramBuilder dgramBuilder;
		RUDPDatagramPacket[] packetList;
		RUDPDatagramPacketOut packetOut;

		//If sender is shut down
		synchronized(this) {
			if(isShutdown) throw new DestinationNotReachableException();
		}

		//Create datagram builder from user datagram
		dgramBuilder = new RUDPDatagramBuilder(datagram);
		packetList = dgramBuilder.getFragmentedPackets();

		// Send packets
		for (RUDPDatagramPacket packet : packetList) {
			synchronized(this) {
				//Activate persist timer?
				if(semaphorePermitCount == 0) {
					persistTask = new PersistTask(this,sendInterface);
					timer.schedule(persistTask, PERSIST_PERIOD);
				}
			}
			
			// Enter the semaphore to stay within window size
			semaphoreWindowSize.acquire();

			// Add packet to out list
			synchronized (this) {
				//Cancel persist timer again
				if(persistTask != null) {
					persistTask.cancel();
					persistTask = null;
				}
				
				if(isShutdown) {
					//Tell everybody that the link failed
					semaphoreWindowSize.release(1);
					throw new DestinationNotReachableException();
				}

				// Decrease semaphore permit count
				semaphorePermitCount--;

				// Create output packet
				packetOut = new RUDPDatagramPacketOut(packet);
				packetOut.setPacketSequence(currentPacketSeq);
				packetBuffer_out.put(currentPacketSeq, packetOut);

				// Increment sequence number
				currentPacketSeq++;
			}

			// Forward to socket interface
			// TODO replace with a nice function
			// p.triggerSend(avg_RTT * 1.5);

			//Flag first packet
			if(firstPacket) {
				packetOut.setFirstFlag(true);
				firstPacket = false;
			}
			
			packetOut.sendPacket(sendInterface, failListener, timer, PACKET_FIRST_RETRY_PERIOD,MAX_DATA_PACKET_RETRIES);
		}
	}

	public void handleAckData(int ackSeqOffset,List<Short> ackSeqData) {
		RUDPDatagramPacketOut ack_pkt;
		DeltaRangeList rangeList;
		
		//Implicit acknowledge with ACK window start sequence
		updateReceiverWindow(ackSeqOffset);
		
		//Recreate range list
		rangeList = new DeltaRangeList(ackSeqData);
		
		synchronized(this) {
			//Acknowledge all packets
			for(Integer i: rangeList.toElementArray()) {
				ack_pkt = packetBuffer_out.get(i + ackSeqOffset);
				if(ack_pkt != null) {
					ack_pkt.acknowldege();
				}
			}
		}
	}
	
	public void setReceiverWindowSize(int receiverWindowSize) {
		//Reset out window
		synchronized(this) {
			if(this.receiverWindowSize > 1) {
				//System.out.println("CHANGE OF WINDOW SIZE NOT IMPLEMENTED YET!");
			}
			else {
				this.receiverWindowSize = receiverWindowSize;
			}
		}
	}
	
/*	public void resetReceiverWindow(int newReceiverWindowStart,int newReceiverWindowSize) {
		//Reset receiver window information
		senderWindowStart = newReceiverWindowStart;
		receiverWindowSize = newReceiverWindowSize;
	}*/
	
	public void updateReceiverWindow(int newReceiverWindowStart) {
		RUDPDatagramPacketOut packet;
		int idx;
		
		synchronized(this) {
			//Check if new window start is reasonable
			if(senderWindowStart > senderWindowStart + receiverWindowSize) {
				if(newReceiverWindowStart < senderWindowStart  && newReceiverWindowStart > (senderWindowStart + receiverWindowSize)) {
					System.out.println("INVALID RECEIVER_WINDOW_START!!! ");
					return;
				}
			}
			else {
				if(newReceiverWindowStart < senderWindowStart || newReceiverWindowStart > (senderWindowStart + receiverWindowSize)) {
					System.out.println("INVALID RECEIVER_WINDOW_START!!! ");
					return;
				}
			}
			
			//Remove all packets from buffer
			//No for-loop possible, because of the overflow
			idx = senderWindowStart;
			
			while(idx != newReceiverWindowStart) {
				//Remove and acknowledge, if it exists
				//Which it should, otherwise the receiver acknowledged something we did not receive...
				if((packet = packetBuffer_out.remove(idx)) != null) {
					packet.acknowldege();
				}
				else {
					//TODO debug output
					System.out.println("We can never get here! - Ohh, i see...");
				}
				
				idx++;
			}
	
			//Set new sender window start
			senderWindowStart = newReceiverWindowStart;
		}

		//Update semaphore
		updateSemaphorePermitCount(newReceiverWindowStart);
	}
	
	private void updateSemaphorePermitCount(int newReceiverWindowStart) {
		int newSemaphorePermitCount;
		int delta;

		synchronized(this) {
			//Release semaphore
			newSemaphorePermitCount = receiverWindowSize - (currentPacketSeq - newReceiverWindowStart);
			delta = newSemaphorePermitCount - semaphorePermitCount;
			semaphorePermitCount = newSemaphorePermitCount;
	
			if(delta > 0) {
				semaphoreWindowSize.release(delta);
				
				//System.out.println(semaphoreWindowSize.getQueueLength() + " - " + semaphoreWindowSize.availablePermits());
			}
			else if(delta < 0){
				System.out.println("The WINDOW_SIZE has been decreased. This feature is not implemented yet! ");
				//TODO handle?
				//The other side decreased the windows size!
				//And we have transmitted data beyond that limit
			}
		}
	}
	
	public void reset() {
		synchronized(this) {
			//Acknowledge and remove all packets
			for(RUDPDatagramPacketOut packet: packetBuffer_out.values()) packet.acknowldege();
			packetBuffer_out.clear();

			//Send first packet again
			firstPacket = true;
			
			//Reset sender window start to current seq. number
			currentPacketSeq = 100;
			receiverWindowSize = 1;
			senderWindowStart = currentPacketSeq;
			
			//Reset semaphore
			semaphoreWindowSize.drainPermits();
			semaphorePermitCount = 1;
			semaphoreWindowSize.release();
		}
	}
	
	public void shutdown() {
		synchronized(this) {
			isShutdown = true;

			//Reset state of sender
			reset();
		}
	}
	
	public void rehabilitate() {
		synchronized(this) {
			isShutdown = false;
		}
	}
	
	private class PersistTask extends TimerTask {
		private RUDPDatagramPacketSenderInterface sendInterface;
		private RUDPDatagramPacketOut packet;
		private Object lockObj;
		
		public PersistTask(Object lockObj,RUDPDatagramPacketSenderInterface sendInterface) {
			this.lockObj = lockObj;
			this.sendInterface = sendInterface;
		}
		
		@Override
		public void run() {
			synchronized (lockObj) {
				if(persistTask != null) {
					//Send persist packet
					packet = new RUDPDatagramPacketOut();
					packet.setPersistFlag(true);
					packet.sendPacket(sendInterface, failListener, timer, PACKET_FIRST_RETRY_PERIOD, MAX_DATA_PACKET_RETRIES);
				}
			}
		}
		
		@Override
		public boolean cancel() {
			synchronized(lockObj) {
				if(packet != null) {
					packet.acknowldege();
				}
			}

			return super.cancel();
		}
	}
}
