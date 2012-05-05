package communication.rudp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import communication.Message;

public class RUDPMessageContainer {
	//Flags
	//private static final int FLAG_RESET = 1;
	private static final int FLAG_ACK = 2;
	private static final int FLAG_DATA = 4;
	private static final int FLAG_RESEND = 8;
	private static final int FLAG_FRAGMENT = 16;
	
	//private boolean flag_reset = false;
	private boolean flag_ack = false;
	private boolean flag_data = false;
	private boolean flag_resend = false;
	private boolean flag_fragment = false;

	private int seq_own;
	private int seq_foreign;
	private boolean[] ack_array;
	private int frag_first;
	private int frag_last;
	
	private Message msg;

	//public void setReset(boolean active) {
//		flag_reset = active;
//	}
	
	public void setFragment(Integer first,Integer last) {
		if(first == null || last == null) {
			flag_fragment = false;
		}
		else {
			flag_fragment = true;
			this.frag_first = first;
			this.frag_last = last; 
		}
	}
	
	public void setACK(boolean active,int seq,boolean[] ack_array) {
		flag_ack = active;
		
		if(active) {
			seq_foreign = seq;
			this.ack_array = ack_array; 
		}
	}
	
	public void setData(boolean active,boolean resend,int seq,Message msg) {
		flag_data = active;
		flag_resend = resend;
		
		if(active) {
			this.seq_own = seq;
			this.msg = msg;
		}
	}
	
	public Boolean getFlag(int flag) {
		switch(flag) {
	//	case FLAG_RESET: return flag_reset;
		case FLAG_ACK: return flag_ack;
		case FLAG_DATA: return flag_data;
		case FLAG_RESEND: return flag_resend;
		case FLAG_FRAGMENT: return flag_fragment;
		default: return null;
		}
	}

	private byte[] serializePacket() {
		ByteArrayOutputStream bos;
		DataOutputStream dos;
		
		try {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			
			//Write flags
			dos.writeByte((flag_ack ? FLAG_ACK : 0) + (flag_data ? FLAG_DATA : 0) + (flag_resend ? FLAG_RESEND : 0) + (flag_fragment ? FLAG_FRAGMENT : 0));
			
			//Write ack field
			if(flag_ack) {
				
			}
			
			//Write fragment fields
			if(flag_fragment) {
				
			}
			
			//Write data
			if(flag_data) {
				
			}
			
		}
		catch(IOException e) {
			
		}
		
		return null;
	}
	
	private RUDPMessageContainer deserializePacket(byte[] data) {
		return null;
	}
}
