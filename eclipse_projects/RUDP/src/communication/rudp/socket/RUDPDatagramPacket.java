package communication.rudp.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RUDPDatagramPacket {
	private static final int MAX_PACKET_SIZE = 65535;
	private static final int MAX_PACKET_RETRY = 3;
	
	//Flags
	public static final int FLAG_FIRST = 1;
	public static final int FLAG_RESET = 2;
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
	
	private Timer timer;
	private TimerTask task_resend;
	private RUDPSendTimeoutListener listener;
	private int retries = 0;

	//Sequence of this packet
	private int seq;
	
	//If this packet is part of a fragmented datagram,
	//these flags indicate the first and last fragment
	private short frag_nr;
	private short frag_count;

	//ACK data
	short ack_data[];
	
	//Data
	int data_off;
	int data_len;
	byte[] data;
		
	public RUDPDatagramPacket(Timer timer,RUDPSendTimeoutListener listener) {
		this.timer = timer;
		this.listener = listener;
	}
	
	public RUDPDatagramPacket(int seq,boolean resend,Short frag_nr,Short frag_last,short[] ack_data,byte[] data,int data_off,int data_len) {
		//Set all the data
		setData(data,data_off,data_len, seq, resend);
		setFragment(frag_nr, frag_last);
		setACK(ack_data);
	}
	
	public RUDPDatagramPacket(byte[] packet) throws InvalidRUDPPacketException,IOException {
		ByteArrayInputStream bis;
		DataInputStream dis;
		int flag;
		int ack_count = -1;
		
		bis = new ByteArrayInputStream(packet);
		dis = new DataInputStream(bis);
		
		//Read flag
		flag = dis.readByte();
		flag_first = (flag & FLAG_FIRST) != 0 ? true : false; 
		flag_reset = (flag & FLAG_RESET) != 0 ? true : false; 
		flag_ack = (flag & FLAG_ACK) != 0 ? true : false; 
		flag_data = (flag & FLAG_DATA) != 0 ? true : false; 
		flag_fragment = (flag & FLAG_FRAGMENT) != 0 ? true : false; 
		flag_resend = (flag & FLAG_RESEND) != 0 ? true : false;
		
		//Read static fields
		if(flag_ack) ack_count = dis.readShort();
		if(flag_data) seq = dis.readInt();
		if(flag_fragment) {
			frag_nr = dis.readShort();
			frag_count = dis.readShort();
		}
		
		//Read variable length fields
		if(flag_ack) {
			ack_data = new short[ack_count];
			for(int i = 0; i < ack_count; i++) {
				ack_data[i] = dis.readShort();				
			}
		}
		
		//Read data, everything till the end of the packet
		data = new byte[bis.available()];
		dis.read(data);
		dis.close();
		bis.close();
	}

	//Set fragment information
	public void setFragment(Short nr,Short count) {
		if(nr == null || count == null) {
			flag_fragment = false;
		}
		else {
			flag_fragment = true;
			this.frag_nr = nr; 
			this.frag_count = count;
		}
	}
	
	//Set acknowledge 
	public void setACK(short[] ack_data ) {
		if(ack_data == null) {
			flag_ack = false;
		}
		else {
			flag_ack = true;
			this.ack_data = ack_data;
		}
	}
	
	//Set data
	public void setData(byte[] data,int data_off,int data_len,int seq,boolean resend) {
		if(data == null) {
			flag_data = false;
			flag_resend = false;
			this.data = null;
		}
		else {
			flag_data = true;
			flag_resend = resend;
			this.data_off = data_off;
			this.data_len = data_len;
			this.data = data;
			this.seq = seq;
		}
	}
	
	public Boolean getFlag(int flag) {
		switch(flag) {
			case FLAG_FIRST: return flag_first;
			case FLAG_RESET: return flag_reset;
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
		
		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			
			//Write flags
			dos.writeByte((flag_first ? FLAG_FIRST : 0) + (flag_reset ? FLAG_RESET : 0) + (flag_ack ? FLAG_ACK : 0) + (flag_data ? FLAG_DATA : 0) + (flag_resend ? FLAG_RESEND : 0) + (flag_fragment ? FLAG_FRAGMENT : 0));
			
			//Write static length fields
			if(flag_ack) dos.writeShort(ack_data.length);
			if(flag_data) dos.writeInt(seq);
			if(flag_fragment) {
				dos.writeShort(frag_nr);
				dos.writeShort(frag_count);
			}
			
			//Write variable length fields
			if(flag_ack) for(short s: ack_data) dos.writeShort(s);
			if(flag_data) dos.write(data,data_off,data_len);
			
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
		int size = MAX_PACKET_SIZE - 8;
		
		//Flag
		size -= Byte.SIZE / 8; 

		//ACK length
		if(flag_ack) size -= Short.SIZE / 8;
		
		//Sequence number
		if(flag_data) size -= Integer.SIZE / 8;
		
		//Fragment
		if(flag_fragment) size -= 2 * (Short.SIZE / 8);
		
		//ACK field
		if(flag_ack) size -= ack_data.length * (Short.SIZE / 8);
		
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
		//Increment retries
		retries++;
		
		//TODO handle a failed packet
		if(retries >= MAX_PACKET_RETRY) {
			System.out.println("PACKET failed");
		}
		
		//Cancel old timer
		if(task_resend != null) {
			task_resend.cancel();
		}
		
		//Restart new timer
		task_resend = new TimeoutTask(this);
		timer.schedule(task_resend,timeout);
	}
	
	private class TimeoutTask extends TimerTask {
		private RUDPDatagramPacket packet;
		
		public TimeoutTask(RUDPDatagramPacket packet) {		
			this.packet = packet;
		}
		
		@Override
		public void run() {
			listener.onSendTimeout(packet);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + seq;
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
		if (seq != other.seq)
			return false;
		return true;
	}
}
