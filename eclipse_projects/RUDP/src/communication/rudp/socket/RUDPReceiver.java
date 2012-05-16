package communication.rudp.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import communication.rudp.socket.listener.RUDPReceiveListener;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPReceiver {
	private static final int MAX_ACK_DELAY = 100;

	//Link; Socket interface to send raw UDP datagrams
	RUDPLink link;
	RUDPSocketInterface socketInterface;
	RUDPReceiveListener receiveListener;
	
	//Timer
	Timer timer;
	
	//Receiver list
	private int receiverWindowStart;
	private TreeMap<Integer,RUDPDatagramBuilder> packetBuffer_in;
	
	//Acknowledge stuff
	private int ackWindowStart;
	private DeltaRangeList ackRange;
	private TimerTask task_ack;

	public RUDPReceiver(RUDPLink link,RUDPSocketInterface socketInterface,RUDPReceiveListener receiveListener,Timer timer) {
		this.link = link;
		this.timer = timer;
		this.receiveListener = receiveListener;
		
		packetBuffer_in = new TreeMap<Integer,RUDPDatagramBuilder>();
		ackRange = new DeltaRangeList();
	}

	public void putReceivedPacket(RUDPDatagramPacket packet) {
	}

	public void handlePayloadData(RUDPDatagramPacket packet) {
		RUDPDatagramBuilder dgram;
		int newRangeElement;
		List<RUDPDatagram> readyDatagrams = null;
		Entry<Integer,RUDPDatagramBuilder> mapEntry;
		
		//Process data packet
		synchronized(this) {
			if(packet.getFlag(RUDPDatagramPacket.FLAG_DATA)) {
				//Check if packet is within window bounds
				if((packet.getPacketSeq() - receiverWindowStart) < 0 || (packet.getPacketSeq() - receiverWindowStart) > RUDPLink.WINDOW_SIZE) {
					//TODO send up to date ACK packet ?! 
					System.out.println("INVALID PACKET RECEIVED - PACKET SEQ OUT OF WINDOW BOUNDS");
				}
				else {
					//Insert into receiving packet buffer
					if(packet.getFlag(RUDPDatagramPacket.FLAG_FRAGMENT)) {
						if((dgram = packetBuffer_in.get(packet.getPacketSeq() - packet.getFragmentNr())) == null) {
							//Create new fragmented datagram
							dgram = new RUDPDatagramBuilder(link.getSocketAddress(),packet.getFragmentCount());
							dgram.assimilateFragment(packet);

							packetBuffer_in.put(packet.getPacketSeq() - packet.getFragmentNr(),dgram);
						}
						else {
							//We are the Borg, and we will...
							dgram.assimilateFragment(packet);
						}
					}
					else {
						//Create new non-fragmented datagram
						dgram = new RUDPDatagramBuilder(link.getSocketAddress(), packet);
						packetBuffer_in.put(packet.getPacketSeq() - packet.getFragmentNr(),dgram);
					}
					
					//Calculate relative position and add to packet range list
					newRangeElement = packet.getPacketSeq() - receiverWindowStart;
					ackRange.add((short)newRangeElement);
					
					//Datagrams ready for delivery
					readyDatagrams = new ArrayList<RUDPDatagram>();
					
					//Add ready datagrams to deploy
					for(Integer i : ackRange.toElementArray()) {
						mapEntry = packetBuffer_in.floorEntry(receiverWindowStart + i); 
						
						if(mapEntry != null) {
							dgram = mapEntry.getValue();

							if(dgram != null && dgram.isComplete()) {
								readyDatagrams.add(dgram.toRUDPDatagram());
							}
							else {
								//First gap or end of ACK array reached
								break;
							}
						}
						else {
							break;
						}
					}	

					//Start ACK-timer, if necessary...
					//...or send immediately if it is the very first packet
					//to speed up the window-size negotiation process
					if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST) || packet.getPacketSeq() - receiverWindowStart >= RUDPLink.WINDOW_SIZE_BOOST) {
						//Send an empty packet, that will get ACK data (automatically)
						link.sendDatagramPacket(new RUDPDatagramPacket());
						if(task_ack != null) {
							task_ack.cancel();
							task_ack = null;
						}
					}
					else {
						//Wait some time for more packets, so we can combine several ACKs
						if(task_ack == null) {
							task_ack = new AcknowledgeTask();
							timer.schedule(task_ack, MAX_ACK_DELAY);
						}
					}
				}
			}
		}
		
		//Forward all ready datagrams to upper layer
		if(readyDatagrams != null) {
			for(RUDPDatagram d: readyDatagrams) receiveListener.onRUDPDatagramReceive(d);
		}
	}

	public void datagramConsumed() {
		RUDPDatagramBuilder dgram;
		boolean windowReOpened = false;
		
		//TODO send an ACK message when a datagram was consumed
		//that opened our receive window after it was 100% full
		//This is done to forestall the other side's persist timer
		
		//A datagram has been consumed => shift receive window one step forward
		synchronized(this) {
			dgram = packetBuffer_in.get(receiverWindowStart);
			if(dgram != null) {


				//Has the window been reopened
				//TODO what do we need here???
				//if(xxx - receiveWindowStart == 0) windowReOpened = true;
				
				//if(dgram.isAckSent()) {
				packetBuffer_in.remove(receiverWindowStart);

				//Shift receive pointer
				receiverWindowStart += dgram.getFragmentCount();
	
				//Shift range and foreign window
				ackRange.shiftRanges((short)(-1 * dgram.getFragmentCount()));
				
				//Inform the sender about the window reopening
				if(windowReOpened) {
					//Send new empty packet that will contain ACK data
					RUDPDatagramPacket packet = new RUDPDatagramPacket();
					link.sendDatagramPacket(packet);
				}
				
				System.out.println("!!!!! Consumed " + receiverWindowStart + " - " + dgram.getFragmentAmount());
			}
		}
	}
	
	public void setAckStream(RUDPDatagramPacket packet) {
		//Set the sequence of the ACK window start
		packet.setAckWindowStart(receiverWindowStart);

		//Check if we can acknowledge something
		synchronized(this) {
			if(!ackRange.isEmpty()) {
				List<Short> ackList;
				
				//Put ACK stream into packet
				ackList = ackRange.toDifferentialArray();
				packet.setACKData(ackRange.toDifferentialArray());

				//TODO debug output
				if(ackList.size() > RUDPDatagramPacket.RESERVED_ACK_COUNT) {
					System.out.println("RESERVED_ACK_COUNT OVERFLOW");
				}
				
				//TODO there could be more ranges then we are able to send
				//in one packet. test if this is fine
				//Disable ACK timer
				if(task_ack != null) {
					task_ack.cancel();
					task_ack = null;
				}
			}
			else {
				//Remove ACK data
				packet.setACKData(null);
			}
		}
	}

	private class AcknowledgeTask extends TimerTask {
		@Override
		public void run() {
			//Send new empty packet that will contain ACK data
			RUDPDatagramPacket packet = new RUDPDatagramPacket();
			link.sendDatagramPacket(packet);
		}
	}
	
	public void reset() {
		synchronized(this) {
			//Stop acknowledge task
			if(task_ack != null) {
				task_ack.cancel();
				task_ack = null;
			}
			
			//Clear internal data structures to have a new start
			ackRange.clear();
			packetBuffer_in.clear();
		}
	}
}
