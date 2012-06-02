package communication.rudp.socket.datagram;

import java.util.List;

public class RUDPDatagramPacket {
	//Packet properties
	public static final int MAX_PACKET_SIZE = 65535;
	public static final int RESERVED_ACK_COUNT = 64;
	public static final int RESERVED_ACK_SIZE = RESERVED_ACK_COUNT * 2 * (Short.SIZE / 8) + (Integer.SIZE / 8);
	
	//Possible flags
	public static final int FLAG_FIRST = 1;
	public static final int FLAG_RESET = 2;
	public static final int FLAG_ACK = 4;
	public static final int FLAG_DATA = 8;
	public static final int FLAG_RESEND = 16;
	public static final int FLAG_PERSIST = 32;
	
	//Flags
	protected boolean flag_first = false;
	protected boolean flag_reset = false;
	protected boolean flag_ack = false;
	protected boolean flag_data = false;
	protected boolean flag_resend = false;
	protected boolean flag_persist = false;
	
	//Sequence of this packet
	protected int packet_seq;
	protected int window_size;
	
	//If this packet is part of a fragmented datagram,
	//these flags indicate the first and last fragment
	protected short frag_nr;
	protected short frag_count;

	//ACK data
	protected int ack_window_start;
	protected List<Short> ack_seq_data;
	
	//Data
	protected byte[] data;
	
	//TODO remove
	protected int id;

	public static final int getMaxDataLength() {
		//Subtract the IP and UDP header
		int size;
		
		//Packet size minus IP, UDP header
		size = MAX_PACKET_SIZE - 20 - 8;

		//Minus reserved space for acknowledgements
		size -= RESERVED_ACK_SIZE;
		
		//Flag
		size -= Byte.SIZE / 8;
		
		//Window sequence number
		size -= Integer.SIZE / 8;

		//Sequence number
		size -= Integer.SIZE / 8;
		
		//Fragment
		size -= 2 * (Short.SIZE / 8);
		
		//Data field
		return size;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + packet_seq;
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
		if (packet_seq != other.packet_seq)
			return false;
		return true;
	}

	public int getWindowSize() {
		return window_size;
	}

	public Boolean getFlag(int flag) {
		switch(flag) {
			case FLAG_FIRST: return flag_first;
			case FLAG_RESET: return flag_reset;
			case FLAG_ACK: return flag_ack;
			case FLAG_DATA: return flag_data;
			case FLAG_RESEND: return flag_resend;
			case FLAG_PERSIST: return flag_persist;
			default: return null;
		}
	}

	public Short getFragmentNr() {
		return frag_nr;
	}
	
	public Short getFragmentCount() {
		return frag_count;
	}
	
	public int getPacketSeq() {
		return packet_seq;
	}
	
	public List<Short> getAckSeqData() {
		return ack_seq_data;
	}
	
	public int getAckWindowStart() {
		return ack_window_start;
	}

	public byte[] getData() {
		return data;
	}
	
	public int getId() {
		return id;
	}
}
