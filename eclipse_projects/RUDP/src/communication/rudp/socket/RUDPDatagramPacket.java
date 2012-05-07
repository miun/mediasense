package communication.rudp.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RUDPDatagramPacket {
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

	//Sequence of this packet
	private int seq;
	
	//If this packet is part of a fragmented datagram,
	//these flags indicate the first and last fragment
	private short frag_first;
	private short frag_nr;
	private short frag_last;

	//ACK data
	short ack_data[];
	
	//Data
	byte[] data;
	
	public RUDPDatagramPacket() {
		//Empty constructor also possible
	}
	
	public RUDPDatagramPacket(int seq,boolean resend,Short frag_first,Short frag_nr,Short frag_last,short[] ack_data,byte[] data) {
		//Set all the data
		setData(data, seq, resend);
		setFragment(frag_first, frag_nr, frag_last);
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
			frag_first = dis.readShort();
			frag_nr = dis.readShort();
			frag_last = dis.readShort();
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
	public void setFragment(Short first,Short nr,Short last) {
		if(first == null || last == null || nr == null) {
			flag_fragment = false;
		}
		else {
			flag_fragment = true;
			this.frag_first = first;
			this.frag_last = last;
			this.frag_nr = nr; 
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
	public void setData(byte[] data,int seq,boolean resend) {
		if(data == null) {
			flag_data = false;
			flag_resend = false;
			this.data = null;
		}
		else {
			flag_data = true;
			flag_resend = resend;
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
				dos.writeShort(frag_first);
				dos.writeShort(frag_nr);
				dos.writeShort(frag_last);
			}
			
			//Write variable length fields
			if(flag_ack) for(short s: ack_data) dos.writeShort(s);
			if(flag_data) dos.write(data);
			
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
