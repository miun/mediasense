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
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPSender implements RUDPDatagramPacketSenderInterface {
	private static final int MAX_DATA_PACKET_RETRIES = 5;
	private static final int PACKET_FIRST_RETRY_PERIOD = 200;
	private static final int PERSIST_PERIOD = 1000;

	//Link; Socket interface to send UDP datagrams
	private RUDPLink link;
	
	//The sender is shutdown and waits for rehabilitate()
	private boolean isShutdown;
	
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
	
	public RUDPSender(RUDPLink link,RUDPSocketInterface socketInterface,Timer timer) {
		this.link = link;
		this.timer = timer;

		//Init buffer
		packetBuffer_out = new HashMap<Integer,RUDPDatagramPacketOut>();

		//Our initial sequence number
		currentPacketSeq = 0;
		senderWindowStart = currentPacketSeq;
		
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
			if(isShutdown) throw new DestinationNotReachableException(link.getSocketAddress());
		}

		//Create datagram builder from user datagram
		dgramBuilder = new RUDPDatagramBuilder(datagram); 
		packetList = dgramBuilder.getFragmentedPackets();
		
		//Send packets
		for(RUDPDatagramPacket packet: packetList) {
			//Create output packet
			packetOut = new RUDPDatagramPacketOut(packet);
				
			//Enter the semaphore to stay within window size
			semaphoreWindowSize.acquire();
			
			//Add packet to out list
			synchronized(this) {
				if(isShutdown) {
					//Tell everybody that the link failed
					semaphoreWindowSize.release(1);
					throw new DestinationNotReachableException(link.getSocketAddress());					
				}
				
				//Decrease semaphore permit count 
				semaphorePermitCount--;
				
				packetOut.setPacketSequence(currentPacketSeq);
				packetBuffer_out.put(currentPacketSeq,packetOut);

				//Increment sequence number
				currentPacketSeq++;
			}
			
			//Forward to socket interface
			//TODO replace with a nice function
			//p.triggerSend(avg_RTT * 1.5);
			packetOut.sendPacket(this,link,timer,PACKET_FIRST_RETRY_PERIOD,MAX_DATA_PACKET_RETRIES);
		}
	}

	@Override
	public void sendDatagramPacket(RUDPDatagramPacketOut p) {
		//Forward to link
		link.sendDatagramPacket(p);
	}

	public void handleAckData(int ackSeqOffset,List<Short> ackSeqData) {
		RUDPDatagramPacketOut ack_pkt;
		DeltaRangeList rangeList;
		
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
		this.receiverWindowSize = receiverWindowSize;
	}
	
	public void updateReceiverWindow(int newReceiverWindowStart,int newReceiverWindowSize) {
		RUDPDatagramPacketOut packet;
		int newSemaphorePermitCount;
		int idx;
		int delta;
		
		//Check for window size change
		if(newReceiverWindowSize != receiverWindowSize) {
			System.out.println("RESIZE OF WINDOW_SIZE NOT IMPLEMENTED YET!!!");
			return;
		}
		
		//Check if new window start is reasonable
		if(senderWindowStart > senderWindowStart + receiverWindowSize) {
			if(newReceiverWindowStart < senderWindowStart  && newReceiverWindowStart > (senderWindowStart + receiverWindowSize)) {
				System.out.println("INVALID RECEIVER_WINDOW_START!!!");
				return;
			}
		}
		else {
			if(newReceiverWindowStart < senderWindowStart || newReceiverWindowStart > (senderWindowStart + receiverWindowSize)) {
				System.out.println("INVALID RECEIVER_WINDOW_START!!!");
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

		//-----
		
		//Release semaphore
		newSemaphorePermitCount = receiverWindowSize - (currentPacketSeq - newReceiverWindowStart);
		
		delta = newSemaphorePermitCount - semaphorePermitCount;
		semaphorePermitCount = newSemaphorePermitCount;

		if(delta > 0) {
			semaphoreWindowSize.release(delta);
			
			System.out.println(semaphoreWindowSize.getQueueLength() + " - " + semaphoreWindowSize.availablePermits());
		}
		else if(delta < 0){
			System.out.println("The WINDOW_SIZE has been decreased. This feature is not implemented yet!");
			//TODO handle?
			//The other side decreased the windows size!
			//And we have transmitted data beyond that limit
		}
	}
	
	public void reset() {
		synchronized(this) {
			//Acknowledge all packets
			for(RUDPDatagramPacketOut packet: packetBuffer_out.values()) {
				packet.acknowldege();
			}
			
			//Reset sender window start to current seq. number
			senderWindowStart = currentPacketSeq;
			packetBuffer_out.clear();
			
			//TODO reset semaphore
			semaphoreWindowSize.drainPermits();
		}
	}
	
	public void shutdown() {
		synchronized(this) {
			isShutdown = true;

			//Reset state of sender
			reset();

			//Open semaphore and let everybody run into an exception
			semaphoreWindowSize.release(1);
		}
	}
	
	public void rehabilitate() {
		synchronized(this) {
			isShutdown = false;
		}
	}
	
	private class persistTask extends TimerTask {

		@Override
		public void run() {
			//TODO do persist here...
			
		}
		
	}
}
