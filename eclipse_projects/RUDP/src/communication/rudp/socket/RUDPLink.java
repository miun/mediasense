package communication.rudp.socket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import communication.rudp.socket.exceptions.InvalidRUDPPacketException;
import communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import communication.rudp.socket.listener.RUDPReceiveListener;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPLink implements RUDPPacketSenderInterface {
	private static final int MAX_ACK_DELAY = 100;
	private static final int MAX_DATA_PACKET_RETRIES = 3;
	private static final int WINDOW_SIZE = 150;
	private static final int WINDOW_SIZE_BOOST = 100;
	private static final int PERSIST_PERIOD = 1000;
	
	//Global variables
	private InetSocketAddress sa;
	private Timer timer;
	private int avg_RTT;
	private Semaphore semaphoreWindowSize;
	private int semaphorePermitCount;

	//Listener  & interfaces
	private RUDPSocketInterface socket;
	private RUDPLinkTimeoutListener listener_timeout;
	private RUDPReceiveListener listener_receive;
	
	//Sender stuff
	private int currentPacketSeq;		//Current packet sequence number
	private boolean isSynced = false;	//Is the window in sync with the other side?!?
	private boolean isFirst = true;		//The first packet must be marked as that!
	
	//Contains sent unacknowledged packets
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_out;
	
	//Receiver list
	//private int currentReceivePointer;
	private TreeMap<Integer,RUDPDatagramBuilder> packetBuffer_in;
	
	//Acknowledge stuff
	private int receiveWindowStart;
	private DeltaRangeList ackRange;
	private TimerTask task_ack;
	
	public RUDPLink(InetSocketAddress sa,RUDPSocketInterface socket,RUDPLinkTimeoutListener listener_to,RUDPReceiveListener listener_recv,Timer timer) {
		//Create data structures
		packetBuffer_out = new HashMap<Integer,RUDPDatagramPacket>();
		packetBuffer_in = new TreeMap<Integer,RUDPDatagramBuilder>();
		ackRange = new DeltaRangeList();
		
		//Set timer and listener
		this.listener_timeout = listener_to;
		this.listener_receive = listener_recv;
		this.socket = socket;
		this.semaphoreWindowSize = new Semaphore(1,true);
		semaphorePermitCount = 1;
		
		//Set or create timer
		if(timer == null) {
			this.timer = new Timer();
		}
		else {
			this.timer = timer;
		}
		
		//Network address
		this.sa = sa;
	}
	
	public InetSocketAddress getSocketAddress() {
		return sa;
	}
	
	public void send(RUDPDatagram datagram) throws InterruptedException {
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
			synchronized(this) {
				packet.setPacketSequence(currentPacketSeq);
				packetBuffer_out.put(currentPacketSeq,packet);

				//Increment sequence number
				currentPacketSeq++;
			}
			
			//Forward to socket interface
			//TODO replace with a nice function
			//p.triggerSend(avg_RTT * 1.5);
			packet.sendPacket(timer,this,MAX_DATA_PACKET_RETRIES,1000000);
		}
	}
	
	public void putReceivedData(byte[] data,int len) {
		RUDPDatagramPacket packet;

		try {	
			//Extract packet
			packet = new RUDPDatagramPacket(data);
		}
		catch (InvalidRUDPPacketException e1) {
			System.out.println("INVALID PACKET RECEIVED");
			return;
		}
		
		System.out.println("RECEIVE\n" + packet.toString() + "\n");
		
		//Handle reset packets
		handleResetFlag(packet);

		//Handle ACK-data
		handleAckData(packet);
		
		//Handle new window sequence
		updateReceiveWindow(packet);
		
		//Handle payload data
		handlePayloadData(packet);
		
		System.out.println("AckRangeOffset:\t" + receiveWindowStart + "\n");
	}
	
	private void handleAckData(RUDPDatagramPacket packet) {
		RUDPDatagramPacket ack_pkt;
		DeltaRangeList rangeList;
		int ackSeqOffset;
		
		//First process acknowledge data, if present
		if(packet.getFlag(RUDPDatagramPacket.FLAG_ACK)) {
			//Recreate range list
			rangeList = new DeltaRangeList(packet.getAckSeqData());
			ackSeqOffset = packet.getAckWindowStart();
			
			synchronized(this) {
				//Acknowledge all packets
				for(Integer i: rangeList.toElementArray()) {
					ack_pkt = packetBuffer_out.remove(i + ackSeqOffset);
					if(ack_pkt != null) {
						ack_pkt.acknowldege();
					}
				}
			}
		}
	}
	
	//Handle reset flag
	private void handleResetFlag(RUDPDatagramPacket packet) {
		if(packet.getFlag(RUDPDatagramPacket.FLAG_RESET)) {
			synchronized(this) {
				//Reset link state
				isFirst = true;
				isSynced = false;
			
				//Clear internal data structures to have a new start
				ackRange.clear();
				packetBuffer_in.clear();
				packetBuffer_out.clear();
				
				//TODO reset semaphore
				semaphoreWindowSize.drainPermits();
				semaphoreWindowSize.release(packet.getWindowSize());
			}
		}
	}
	
	private void updateReceiveWindow(RUDPDatagramPacket packet) {
		//Calculate delta to old window
		int delta;

		synchronized(this) {
			//Release semaphore delta times
			//semaphoreWindowSize.release(WINDOW_SIZE - packetBuffer_out.size());
			
			if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST)) {
				//First packet => The packet sequence number is the beginning of the window
				receiveWindowStart = packet.getPacketSeq();
				
				//We are sync'ed now
				isSynced = true;
			}
			
			if(!isSynced) {
				//Send a reset packet, because we need a first packet for synchronization
				RUDPDatagramPacket resetPacket = new RUDPDatagramPacket();
				
				//Send
				resetPacket.setResetFlag(true);
				sendPacket(resetPacket);
				
				//TODO reset semaphore
				semaphoreWindowSize.drainPermits();
				System.out.println("UNSYNCHRONIZED PACKET RECEIVED - FIRST PACKET MISSING");
			}
			else {
				//Release semaphore
				delta = packet.getWindowSize() - (currentPacketSeq - receiveWindowStart);

				if(delta > 0) {
					semaphoreWindowSize.release(delta);
				}
				else {
					//TODO handle?
					//The other side decreased the windows size!
					//And we have data transmitted beyond that size
				}
			}
		}
	}
	
	private void handlePayloadData(RUDPDatagramPacket packet) {
		RUDPDatagramBuilder dgram;
		int newRangeElement;
		List<RUDPDatagram> readyDatagrams = null;
		
		//Process data packet
		synchronized(this) {
			if(isSynced && packet.getFlag(RUDPDatagramPacket.FLAG_DATA)) {
				//Check if packet is within window bounds
				if((packet.getPacketSeq() - receiveWindowStart) < 0 || (packet.getPacketSeq() - receiveWindowStart) > WINDOW_SIZE) {
					System.out.println("INVALID PACKET RECEIVED - PACKET SEQ OUT OF WINDOW BOUNDS");
				}
				else {
					//Insert into receiving packet buffer
					if(packet.getFlag(RUDPDatagramPacket.FLAG_FRAGMENT)) {
						if((dgram = packetBuffer_in.get(packet.getPacketSeq() - packet.getFragmentNr())) == null) {
							//Create new fragmented datagram
							dgram = new RUDPDatagramBuilder(sa,packet.getFragmentCount());
							dgram.assimilateFragment(packet);
							//, packet.getData());
							packetBuffer_in.put(packet.getPacketSeq() - packet.getFragmentNr(),dgram);
						}
						else {
							//We are the Borg, and we will...
							dgram.assimilateFragment(packet);
						}
					}
					else {
						//Create new normal datagram
						dgram = new RUDPDatagramBuilder(sa, packet);
						packetBuffer_in.put(packet.getPacketSeq() - packet.getFragmentNr(),dgram);
					}
					
					//Calculate relative position and add to packet range list
					newRangeElement = packet.getPacketSeq() - receiveWindowStart;
					ackRange.add((short)newRangeElement);
					
					//Datagrams ready for delivery
					readyDatagrams = new ArrayList<RUDPDatagram>();
					
				
					RUDPDatagramBuilder d;
					boolean deleteFromBuffer = true;
					
					for(Integer i : ackRange.toElementArray()) {
						d = packetBuffer_in.floorEntry(receiveWindowStart + i).getValue();

						if(d!=null && d.isComplete()) {
							
							readyDatagrams.add(dgram.toRUDPDatagram());
							d.setDeployed();
							
							if(deleteFromBuffer && d.isAckSent()) {
								//Remove from list
								packetBuffer_in.remove(receiveWindowStart);
								
								//Shift receive pointer
								receiveWindowStart += d.getFragmentCount();

								//Shift range and foreign window
								ackRange.shiftRanges((short)(-1 * d.getFragmentCount()));
							}
							else {
								deleteFromBuffer = false;
							}
						}
					}	

					//Start ACK-timer, if necessary...
					//...or send immediately if it is the very first packet
					//to speed up the window-size negotiation process
					if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST) || packet.getPacketSeq() - receiveWindowStart >= WINDOW_SIZE_BOOST) {
						sendPacket(new RUDPDatagramPacket());
						if(task_ack != null) {
							task_ack.cancel();
							task_ack = null;
						}
					}
					else {
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
			for(RUDPDatagram d: readyDatagrams) listener_receive.onRUDPDatagramReceive(d);
		}
	}
	
	private void setAckStream(RUDPDatagramPacket packet) {
		//Check if we can acknowledge something
		synchronized(this) {
			if(!ackRange.isEmpty()) {
				List<Short> ackList;
				
				//Put ACK stream into packet
				ackList = ackRange.toDifferentialArray();
				packet.setACKData(receiveWindowStart,ackList);
				
				//Inform packages that their ack is sent
				RUDPDatagramBuilder d;
				boolean deleteFromBuffer = true;
				
				for(Integer i : ackRange.toElementArray()) {
					d = packetBuffer_in.floorEntry(receiveWindowStart + i).getValue();
					
					if(d!=null) {
						d.setAckSent();
						
						if(deleteFromBuffer && d.isDeployed()) {
							//Remove from list
							packetBuffer_in.remove(receiveWindowStart);
							
							//Shift receive pointer
							receiveWindowStart += d.getFragmentCount();

							//Shift range and foreign window
							ackRange.shiftRanges((short)(-1 * d.getFragmentCount()));
						}
						else {
							deleteFromBuffer = false;
						}
					}
					
				}
				
				//DEBUG - report if the ACK list did not fit into one packet
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
				packet.setACKData(0, null);
			}
		}
	}
	
	@Override
	public void sendPacket(RUDPDatagramPacket p) {
		//Add the ACK overlay stream
		setAckStream(p);

		//Set window size
		p.setWindowSize(WINDOW_SIZE);
		
		//Set first flag at first packet
		synchronized(this) {
			if(isFirst) {
				p.setFirstFlag(true);
				isFirst = false;
			}
		}

		//TODO remove debug output
		System.out.println("SEND\n" + p.toString() + "\n");

		//Send packet
		socket.triggerSend(this, p);
	}
	
	private class AcknowledgeTask extends TimerTask {
		@Override
		public void run() {
			//Send new empty packet that will contain ACK data
			RUDPDatagramPacket packet = new RUDPDatagramPacket();
			sendPacket(packet);
		}
	}
}
