package communication.rudp.socket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPLink implements RUDPSendTimeoutListener {
	private int seq_own;
	private int seq_foreign;
	
	//Average
	private int avg_RTT;
	
	//Timeout listener
	private InetSocketAddress sa;
	private Timer timer;
	private RUDPSocketInterface socket;
	private RUDPLinkTimeoutListener listener_timeout;
	private RUDPReceiveListener listener_receive;
	
	//Packet buffers <seq-number,packet>
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_out;
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_in;
	private DeltaRangeList packetRange;
	
	//Timers
	TimerTask task_ack;
	TimerTask task_resend;
	
	public RUDPLink(InetSocketAddress sa,RUDPSocketInterface socket,RUDPLinkTimeoutListener listener_to,RUDPReceiveListener listener_recv,Timer timer) {
		//Create data structures
		packetBuffer_out = new HashMap<Integer,RUDPDatagramPacket>();
		packetBuffer_in = new HashMap<Integer,RUDPDatagramPacket>();
		packetRange = new DeltaRangeList();
		
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
		
		//Check if we can acknowledge something
		//TODO
		
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

		//First process acknowledge data, if present
		if(packet.getFlag(RUDPDatagramPacket.FLAG_ACK)) {
			RUDPDatagramPacket ack_pkt;
			DeltaRangeList rangeList;
			int seq_offset;

			//Recreate range list
			rangeList = new DeltaRangeList(packet.getAckData());
			seq_offset = packet.getAckSeq();
			
			//Acknowledge all packets
			for(Integer i: rangeList.toElementArray()) {
				ack_pkt = packetBuffer_out.remove(i + seq_offset);
				if(ack_pkt != null) ack_pkt.acknowldege();
			}
		}
		
		//Process data packet
		if(packet.getFlag(RUDPDatagramPacket.FLAG_DATA)) {
			
		}
		
		//Insert into data structures
		//packetBuffer_in.put(packet.get, arg1)
	}

	@Override
	public void onSendTimeout(RUDPDatagramPacket p) {
		socket.triggerSend(this, p);
	}
}
