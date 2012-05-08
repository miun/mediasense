package communication.rudp.socket;
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
	
	//Packet buffers
	private HashMap<RUDPDatagramPacket,Integer> packetBuffer_out;
	private HashMap<Integer,RUDPDatagramPacket> packetBuffer_in;
	private DeltaRangeList packetRange;
	
	//Timers
	TimerTask task_ack;
	TimerTask task_resend;
	
	public RUDPLink(InetSocketAddress sa,RUDPSocketInterface socket,RUDPLinkTimeoutListener listener_to,RUDPReceiveListener listener_recv,Timer timer) {
		//Create data structures
		packetBuffer_out = new HashMap<RUDPDatagramPacket,Integer>();
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
		int remainingPacketLength;
		
		//Create new packet
		packet = new RUDPDatagramPacket(timer,this);
		
		//Check if we can acknowledge something
		//TODO
		
		//Length of the data to send
		dataSize = datagram.getData().length;
		
		if(dataSize > packet.getRemainingLength()) {
			short fragmentCounter = 0;
			
			//We have to do fragmentation
			//packet.setFragment((short)0,(short)0);
			
			//For each fragment
			for(int offset = 0; offset < dataSize;) {
				//Create datagram packet
				packet.setFragment(fragmentCounter,(short)0);
				remainingPacketLength = packet.getRemainingLength();
				packet.setData(datagram.getData(), offset,remainingPacketLength, seq_own, false);
				packetList.add(packet);
				
				//Add to send buffer
				packetBuffer_out.put(packet, 0);
				
				//Increment offset
				offset += remainingPacketLength;
				
				//Create new packet
				packet = new RUDPDatagramPacket(timer,this);
				
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
			packetBuffer_out.put(packet, 0);
			
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
		
		
	}

	@Override
	public void onSendTimeout(RUDPDatagramPacket p) {
		// TODO Auto-generated method stub
		
	}
}
