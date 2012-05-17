package communication.rudp.socket;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Semaphore;

import communication.rudp.socket.datagram.RUDPDatagram;
import communication.rudp.socket.datagram.RUDPDatagramBuilder;
import communication.rudp.socket.datagram.RUDPDatagramPacket;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPSender implements RUDPDatagramPacketSenderInterface {
	private static final int MAX_DATA_PACKET_RETRIES = 5;
	private static final int PACKET_FIRST_RETRY_PERIOD = 200;
	private static final int PERSIST_PERIOD = 1000;

	//Link; Socket interface to send UDP datagrams
	RUDPLink link;
	RUDPSocketInterface socketInterface;
	
	//Timer
	Timer timer;
	
	//Semaphore to stay within window size
	private Semaphore semaphoreWindowSize;
	private int semaphorePermitCount;
	
	//Sequence of the next packet
	private int currentPacketSeq;

	//Contains sent, unprocessed packets
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_out;
	private int receiverWindowStart;
	private int senderWindowStart;
	
	public RUDPSender(RUDPLink link,RUDPSocketInterface socketInterface,Timer timer) {
		this.link = link;
		this.timer = timer;
		this.socketInterface = socketInterface;

		//Init buffer
		packetBuffer_out = new HashMap<Integer,RUDPDatagramPacket>();

		//Our initial sequence number
		currentPacketSeq = 0;
		senderWindowStart = currentPacketSeq;
		
		//Init semaphore
		this.semaphoreWindowSize = new Semaphore(1,true);
		semaphorePermitCount = 1;
	}

	public void sendDatagram(RUDPDatagram datagram) throws InterruptedException {
		RUDPDatagramBuilder dgramBuilder;
		RUDPDatagramPacket[] packetList;

		//Create datagram builder from user datagram
		dgramBuilder = new RUDPDatagramBuilder(datagram); 
		packetList = dgramBuilder.getFragmentedPackets();
		
		//Send packets
		for(RUDPDatagramPacket packet: packetList) {
			//Enter the semaphore to stay within window size
			semaphoreWindowSize.acquire();
			
			//Add packet to out list
			synchronized(link) {
				//Decrease semaphore permit count 
				semaphorePermitCount--;
				
				packet.setPacketSequence(currentPacketSeq);
				packetBuffer_out.put(currentPacketSeq,packet);

				//Increment sequence number
				currentPacketSeq++;
			}
			
			//Forward to socket interface
			//TODO replace with a nice function
			//p.triggerSend(avg_RTT * 1.5);
			packet.sendPacket(timer,this,MAX_DATA_PACKET_RETRIES,PACKET_FIRST_RETRY_PERIOD);
		}
	}

	@Override
	public void sendDatagramPacket(RUDPDatagramPacket p) {
		//Forward to link
		link.sendDatagramPacket(p);
	}

	public void handleAckData(int ackSeqOffset,List<Short> ackSeqData) {
		RUDPDatagramPacket ack_pkt;
		DeltaRangeList rangeList;
		
		//Recreate range list
		rangeList = new DeltaRangeList(ackSeqData);
		
		synchronized(link) {
			//Acknowledge all packets
			for(Integer i: rangeList.toElementArray()) {
				ack_pkt = packetBuffer_out.get(i + ackSeqOffset);
				if(ack_pkt != null) {
					ack_pkt.acknowldege();
				}
			}
		}
	}
	
	public void resetReceiverWindow(int receiverWindowStart,int receiverWindowSize) {
		//Reset out window
		this.receiverWindowStart = receiverWindowStart;
	}
	
	public void updateReceiverWindow(int receiverWindowStart,int receiverWindowSize) {
		RUDPDatagramPacket packet;
		int newSemaphorePermitCount;
		int idx;
		int delta;
		
		//Check if new window start is reasonable
		if(senderWindowStart > senderWindowStart + receiverWindowSize) {
			if(receiverWindowStart < senderWindowStart  && receiverWindowStart > (senderWindowStart + receiverWindowSize)) {
				System.out.println("INVALID RECEIVER_WINDOW_START!!!");
				return;
			}
		}
		else {
			if(receiverWindowStart < senderWindowStart || receiverWindowStart > (senderWindowStart + receiverWindowSize)) {
				System.out.println("INVALID RECEIVER_WINDOW_START!!!");
				return;
			}
		}

		//Remove all packets from buffer
		//No for-loop possible, because of the overflow
		idx = senderWindowStart;
		
		while(idx != receiverWindowStart) {
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
		senderWindowStart = receiverWindowStart;

		//-----
		
		//Release semaphore
		newSemaphorePermitCount = receiverWindowSize - (currentPacketSeq - receiverWindowStart);
		
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
		synchronized(link) {
			//Reset sender window start to current seq. number
			senderWindowStart = currentPacketSeq;
			packetBuffer_out.clear();
			
			//TODO reset semaphore
			semaphoreWindowSize.drainPermits();
			semaphoreWindowSize.release(1);
		}
	}

	@Override
	public void eventLinkFailed() {
		link.eventLinkFailed();
	}
}
