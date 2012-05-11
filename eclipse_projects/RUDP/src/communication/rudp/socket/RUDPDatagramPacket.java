package communication.rudp.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import communication.rudp.socket.exceptions.InvalidRUDPPacketException;
import communication.rudp.socket.rangeset.DeltaRangeList;

public class RUDPDatagramPacket {
	public static final int MAX_PACKET_SIZE = 65535;
	public static final int MAX_PACKET_RETRY = 3;
	public static final int RESERVED_ACK_COUNT = 32;
	public static final int RESERVED_ACK_SIZE = RESERVED_ACK_COUNT * 2 * (Short.SIZE / 8) + Integer.SIZE / 8;
	
	//Flags
	public static final int FLAG_FIRST = 1;
//	public static final int FLAG_RESET = 2;
	public static final int FLAG_ACK = 4;
	public static final int FLAG_DATA = 8;
	public static final int FLAG_RESEND = 16;
	public static final int FLAG_FRAGMENT = 32;
	
	private boolean flag_first = false;
	private boolean flag_reset = false;
	private boolean flag_ack = false;
	private boolean flag_data = false;
	private boolean flag_resend = false;
	private boolean flag_fragment = false;
	
	//Resend timer and task
	private int retries = 0;
	private Timer timer;
	private TimerTask task_resend;
	
	//Interface to send packets with
	private RUDPPacketSenderInterface listener;

	//Sequence of this packet
	private int sender_seq;
	private int sender_window_start;
	
	//If this packet is part of a fragmented datagram,
	//these flags indicate the first and last fragment
	private short frag_nr;
	private short frag_count;

	//ACK data
	int ack_start_seq;
	List<Short> ack_data;
	
	//Data
	int data_off;
	int data_len;
	byte[] data;
	
	public RUDPDatagramPacket() {
		//Create an empty packet without resend-timer stuff
		//Used for acknowledge-only packets, that don't need to be resend
		timer = null;
		listener = null;
	}
		
	public RUDPDatagramPacket(Timer timer,RUDPPacketSenderInterface listener) {
		//Create a packet with resend capabilities
		this.timer = timer;
		this.listener = listener;
	}

	//Deserialize packet
	public RUDPDatagramPacket(byte[] packet) throws InvalidRUDPPacketException {
		ByteArrayInputStream bis;
		DataInputStream dis;
		int flag;
		int ack_count = 0;
		int ack_size = 0;
		
		bis = new ByteArrayInputStream(packet);
		dis = new DataInputStream(bis);
		
		//Read flag
		try {
			flag = dis.readByte();
			flag_first = (flag & FLAG_FIRST) != 0 ? true : false; 
//			flag_reset = (flag & FLAG_RESET) != 0 ? true : false; 
			flag_ack = (flag & FLAG_ACK) != 0 ? true : false; 
			flag_data = (flag & FLAG_DATA) != 0 ? true : false; 
			flag_fragment = (flag & FLAG_FRAGMENT) != 0 ? true : false; 
			flag_resend = (flag & FLAG_RESEND) != 0 ? true : false;
			
			//Read static fields
			sender_window_start = dis.readInt();
			if(flag_data) sender_seq = dis.readInt();
			if(flag_ack) {
				ack_count = dis.readShort();
				
				//Check that the ACK field is not too long
				if(ack_count > RESERVED_ACK_COUNT) throw new InvalidRUDPPacketException();
				
				//Get size of ACK field
				ack_size = ack_count * 2 * Short.SIZE/8 + Integer.SIZE/8 ;
			}
			
			//Fragment data fields
			if(flag_fragment) {
				frag_nr = dis.readShort();
				frag_count = dis.readShort();
			}
			
			//Read data if available
			if(flag_data) {
				int dataSize = bis.available() - ack_size;
				if(dataSize < 0) throw new InvalidRUDPPacketException();
				data = new byte[dataSize];
				dis.readFully(data,0,dataSize);
			}
	
			//Read variable length fields
			if(flag_ack) {
				ack_start_seq = dis.readInt();
				ack_data = new ArrayList<Short>();
				for(int i = 0; i < ack_count; i++) {
					ack_data.add(dis.readShort());				
				}
			}
			
			dis.close();
			bis.close();
		}
		catch (IOException e) {
			//Transform to invalid packet exception
			throw new InvalidRUDPPacketException();
		}
	}

	//Set fragment information
	public void setFragmentFlag(boolean flag) {
		flag_fragment = flag;
	}
	
	public void setFragment(short nr,short count) {
		this.flag_fragment = true;
		this.frag_nr = nr; 
		this.frag_count = count;
	}
	
	//Set acknowledge 
	public void setACK(int ack_seq,List<Short> ack_data) {
		if(ack_data == null) {
			flag_ack = false;
		}
		else {
			flag_ack = true;
			this.ack_data = ack_data;
			this.ack_start_seq = ack_seq;
		}
	}
	
	//Set data
	public void setDataFlag(boolean flag) {
		flag_data = flag;
		data = new byte[0];
		data_len = 0;
		data_off = 0;
	}
	
	//Sequence window
	public void setSenderWindowStart(int sender_window_start) {
		this.sender_window_start = sender_window_start;
	}
	
	public int getSenderWindowStart() {
		return sender_window_start;
	}
	
	public void setData(byte[] data,int data_off,int data_len,int seq,boolean resend) {
		flag_data = true;
		flag_resend = resend;
		this.data_off = data_off;
		this.data_len = data_len;
		this.data = data;
		this.sender_seq = seq;
	}
	
	public Boolean getFlag(int flag) {
		switch(flag) {
			case FLAG_FIRST: return flag_first;
//			case FLAG_RESET: return flag_reset;
			case FLAG_ACK: return flag_ack;
			case FLAG_DATA: return flag_data;
			case FLAG_RESEND: return flag_resend;
			case FLAG_FRAGMENT: return flag_fragment;
			default: return null;
		}
	}

	public byte[] serializePacket() {
		ByteArrayOutputStream bos;
		DataOutputStream dos;
		int ack_count = 0;
		
		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			
			//Write flags
			//(flag_reset ? FLAG_RESET : 0) + 
			dos.writeByte((flag_first ? FLAG_FIRST : 0) + (flag_ack ? FLAG_ACK : 0) + (flag_data ? FLAG_DATA : 0) + (flag_resend ? FLAG_RESEND : 0) + (flag_fragment ? FLAG_FRAGMENT : 0));
			
			//Write window sequence
			dos.writeInt(sender_window_start);
			
			//Write sequence
			if(flag_data) dos.writeInt(sender_seq);

			//Write static length fields
			if(flag_ack) {
				ack_count = ack_data.size();
				if(ack_count > RESERVED_ACK_COUNT) ack_count = RESERVED_ACK_COUNT;
				dos.writeShort(ack_count);
			}

			//Write fragment counters
			if(flag_fragment) {
				dos.writeShort(frag_nr);
				dos.writeShort(frag_count);
			}
			
			//Write data
			if(flag_data) dos.write(data,data_off,data_len);

			//Write variable length fields
			if(flag_ack) {
				dos.writeInt(ack_start_seq);
				for(int i = 0; i < ack_count; i++) {
					dos.writeShort(ack_data.get(i));
				}
			}
			
			//Flush, close and write
			dos.flush();
			dos.close();
			bos.close();
			
			return bos.toByteArray();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getRemainingLength() {
		//Subtract the IP and UDP header
		int size;
		
		//Packet size minus IP, UDP header
		size = MAX_PACKET_SIZE - 20 - 8;

		//Minus reserved space for acknowledgements
		size -= RESERVED_ACK_SIZE;
		
		//Flag
		size -= Byte.SIZE / 8;
		
		//Window sequence number
		size -= Integer.SIZE;

		//Sequence number
		if(flag_data) size -= Integer.SIZE / 8;
		
		//Fragment
		if(flag_fragment) size -= 2 * (Short.SIZE / 8);
		
		if(flag_ack) {
			//ACK length
			size -= Short.SIZE / 8;

			//ACK field + ACK sequence number
			size -= ack_data.size() * (Short.SIZE / 8) + Integer.SIZE / 8;
		}
		
		//Data field
		if(flag_data) size -= data.length * (Byte.SIZE / 8);
		return size;
	}
	
	public Short getFragmentNr() {
		return frag_nr;
	}
	
	public Short getFragmentCount() {
		return frag_count;
	}
	
	public void triggerSend(int timeout) {
		//Set the resend flag if it is not the first attempt
		if(retries > 0) {
			flag_resend = true;
		}
		
		//Cancel old timer
		if(task_resend != null) {
			synchronized(this) {
				task_resend.cancel();
			}
		}
		
		//TODO handle a failed packet
		if(retries >= MAX_PACKET_RETRY) {
			//Do not schedule the TimerTask again
			System.out.println("PACKET failed");
		} 
		else {
			//Send packet
			listener.sendPacket(this);
			
			//Restart new timer
			synchronized(this) {
				task_resend = new TimeoutTask(timeout * 2);
				timer.schedule(task_resend,timeout);
			}

			//Increment retries
			retries++;
		}
	}
	
	private class TimeoutTask extends TimerTask {
		private int timeout;
		
		public TimeoutTask(int timeout) {		
			this.timeout = timeout;
		}
		
		@Override
		public void run() {
			triggerSend(timeout);
		}
	}
	
	public void acknowldege() {
		//Cancel resend task
		synchronized(this) {
			if(task_resend != null) {
				task_resend.cancel();
				task_resend = null;
			}
		}
	}
	
	public boolean isAcknowledged() {
		synchronized(this) {
			return task_resend == null;
		}
	}
	
	public int getSenderSeqNr() {
		return sender_seq;
	}
	
	public List<Short> getAckData() {
		return ack_data;
	}
	
	public int getAckStartSequence() {
		return ack_start_seq;
	}
	
	public void setFirstFlag(boolean flag) {
		flag_first = flag;
	}
	
	public String toString() {
		String result;
		
		result = flag_first ? "FIRST" : "";
		result += (flag_ack ? ",ACK" : "");
		result += (flag_fragment ? ",FRAGMENT" : "");
		result += (flag_data ? ",DATA" : "");
		result += (flag_resend ? ",RESEND" : "");
		
		result += "\nOWN_SEQ:" + sender_seq + " WND_START_SEQ:" + sender_window_start + " FRAG_NR:" + frag_nr + " FRAG_COUNT:" + frag_count + " RETRIES:" + retries;
		
		if(flag_ack && ack_data.size() > 0) {
			result += (new DeltaRangeList(this.getAckData())).toString(ack_start_seq);
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sender_seq;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RUDPDatagramPacket other = (RUDPDatagramPacket) obj;
		if (sender_seq != other.sender_seq)
			return false;
		return true;
	}
}
