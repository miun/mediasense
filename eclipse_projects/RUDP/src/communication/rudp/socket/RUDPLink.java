package communication.rudp.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import communication.rudp.socket.listener.RUDPLinkTimeoutListener;
import communication.rudp.socket.listener.RUDPReceiveListener;
import communication.rudp.socket.listener.exceptions.InvalidRUDPPacketException;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPLink implements RUDPPacketSenderInterface {
	protected static final int MAX_ACK_DELAY = 100;
	
	//Global variables
	private InetSocketAddress sa;
	private Timer timer;
	private int avg_RTT;

	//Listener  & interfaces
	private RUDPSocketInterface socket;
	private RUDPLinkTimeoutListener listener_timeout;
	private RUDPReceiveListener listener_receive;
	
	//Sender stuff
	private int seq_own;		//Continuous seq. number
	private int seq_window;		//The last acknowledged packet
	private boolean isSynced;	//Is the window in sync with the other side?!?
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_out;	//Contains sent un-acknowledged packets
	
	//Receiver stuff
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_in;
	
	//Acknowledge stuff
	private int ack_window;
	private DeltaRangeList packetRangeAck;
	private TimerTask task_ack;
	
	public RUDPLink(InetSocketAddress sa,RUDPSocketInterface socket,RUDPLinkTimeoutListener listener_to,RUDPReceiveListener listener_recv,Timer timer) {
		//Create data structures
		packetBuffer_out = new HashMap<Integer,RUDPDatagramPacket>();
		packetBuffer_in = new HashMap<Integer,RUDPDatagramPacket>();
		packetRangeAck = new DeltaRangeList();
		
		//Set timer and listener
		this.listener_timeout = listener_to;
		this.listener_receive = listener_recv;
		this.socket = socket;
		
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
	
	public void send(RUDPDatagram datagram) {
		List<RUDPDatagramPacket> packetList = new ArrayList<RUDPDatagramPacket>();
		RUDPDatagramPacket packet;
		int dataSize;
		int dataLen;
		int remainingPacketLength;
		
		//Create new packet
		packet = new RUDPDatagramPacket(timer,this);
		packet.setDataFlag(true);
		
		//Length of the data to send
		dataSize = datagram.getData().length;
		dataLen = dataSize;
		
		if(dataSize > packet.getRemainingLength()) {
			short fragmentCounter = 0;
			
			//For each fragment
			for(int offset = 0; offset < dataSize;) {
				//Create datagram packet
				packet.setFragment(fragmentCounter,(short)0);
				remainingPacketLength = packet.getRemainingLength();
				packet.setData(datagram.getData(), offset,remainingPacketLength < dataLen ? remainingPacketLength : dataLen , seq_own, false);
				packetList.add(packet);
				
				//Add to send buffer
				packetBuffer_out.put(seq_own,packet);
				
				//Increment offset
				offset += remainingPacketLength;
				dataLen -= remainingPacketLength; 
				
				//Create new packet
				packet = new RUDPDatagramPacket(timer,this);
				packet.setDataFlag(true);
				
				//Increment fragment and sequence counter
				fragmentCounter++;
				seq_own++;
			}
			
			//Set fragment count, because now we know it
			for(RUDPDatagramPacket p: packetList) {
				p.setFragment(p.getFragmentNr(), (short)packetList.size());
			}
		}
		else {
			//NO fragmentation
			packet.setData(datagram.getData(),0, datagram.getData().length, seq_own, false);
			packetList.add(packet);
			
			//Add to send buffer
			packetBuffer_out.put(seq_own,packet);
			
			//Increment sequence number
			seq_own++;
		}
		
		//Send packets
		for(RUDPDatagramPacket p: packetList) {
			//Forward to socket interface
			socket.triggerSend(this, p);
		}
	}
	
	public void putReceivedData(byte[] data,int len) {
		RUDPDatagramPacket packet;

		try {	
			//Extract packet
			packet = new RUDPDatagramPacket(data);
		}
		catch (InvalidRUDPPacketException e1) {
			System.out.println("INVALID PACKET");
			return;
		}
		catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		
		//Handle ACK-data
		handleAckData(packet);
		
		//Handle new window sequence
		handleWindowSequence(packet);
		
		//Handle payload data
		handlePayloadData(packet);
	}
	
	private void handleAckData(RUDPDatagramPacket packet) {
		RUDPDatagramPacket ack_pkt;
		DeltaRangeList rangeList;
		int seq_offset;
		
		//Die Wurst mit vier Seiten
		
		//First process acknowledge data, if present
		if(packet.getFlag(RUDPDatagramPacket.FLAG_ACK)) {
			//Recreate range list
			rangeList = new DeltaRangeList(packet.getAckData());
			seq_offset = packet.getAckSeq();
			
			//Acknowledge all packets
			for(Integer i: rangeList.toElementArray()) {
				ack_pkt = packetBuffer_out.remove(i + seq_offset);
				if(ack_pkt != null) ack_pkt.acknowldege();
			}
		}
	}
	
	private void handleWindowSequence(RUDPDatagramPacket packet) {
		//Calculate delta to old window
		int delta;
		
		if(packet.getFlag(RUDPDatagramPacket.FLAG_FIRST)) {
			//First packet => take ack-window is reference
			seq_window = packet.getWindowSequence();
			isSynced = true;
		}
		else if(isSynced) {
			//Calculate window shift / check for overflow
			//TODO does this overflow thing really work???
			delta = packet.getWindowSequence() - seq_window;
			if(delta < 0) delta += Integer.MAX_VALUE;
			
			//TODO check limits etc.
			
			//Shift all ranges
			packetRangeAck.shiftRanges((short)(-1 * delta));
			
			//Shift window / check for overflow
			seq_window += delta; 
			if(seq_window < 0) seq_window += Integer.MAX_VALUE;
		}
		else {
			//TODO failed
		}
	}
	
	private void handlePayloadData(RUDPDatagramPacket packet) {
		RUDPDatagram dgram;
		int newRangeElement;
		
		//Process data packet
		if(packet.getFlag(RUDPDatagramPacket.FLAG_DATA)) {
			//Insert into data structures
			packetBuffer_in.put(packet.getSequenceNr(),packet);
			
			//Add to range list
			newRangeElement = packet.getSequenceNr() - ack_window;
			if(newRangeElement > Short.MAX_VALUE) {
				//TODO
			}
			else {
				packetRangeAck.add((short)newRangeElement);
			}
			
			//Start ack-timer, if necessary
			synchronized(this) {
				if(task_ack == null) {
					task_ack = new AcknowledgeTask();
					timer.schedule(task_ack, MAX_ACK_DELAY);
				}
			}
			
			//Check if new datagrams are ready for delivery
			
			
		}
	}
	
	private void setAckStream(RUDPDatagramPacket packet) {
		//Check if we can acknowledge something
		if(!packetRangeAck.isEmpty()) {
			List<Short> ackList;
			
			ackList = packetRangeAck.toDifferentialArray();
			packet.setACK(ack_window,ackList);
			
			//TODO what if there are more ranges than space
			//Disable ack timer
			synchronized(this) {
				if(task_ack != null) {
					task_ack.cancel();
					task_ack = null;
				}
			}
		}
		else {
			//Remove ack data
			packet.setACK(0, null);
		}
	}
	
	private void setWindowSequence(RUDPDatagramPacket packet) {
		packet.setWindowSequence(seq_window);
	}

	@Override
	public void sendPacket(RUDPDatagramPacket p) {
		//Add the ack overlay stream
		setAckStream(p);
		setWindowSequence(p);
		
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
