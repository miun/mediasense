package communication.rudp;

import communication.Message;

public class RUDPMessageContainer {
	//Flags
	private static final int FLAG_RESET = 1;
	private static final int FLAG_ACK = 2;
	private static final int FLAG_DATA = 4;
	private static final int FLAG_RESEND = 8;
	
	private byte flags = 0;
	private int seq_own;
	private int seq_foreign;
	private boolean[] ack_array;
	private Message msg;

	public void setReset(boolean active) {
		//Reset just consists of a flag
		if(active) {
			setFlag(FLAG_RESET);
		}
		else {
			resetFlag(FLAG_RESET);
		}
	}
	
	public void setACK(boolean active,int seq,boolean[] ack_array) {
		if(active) {
			setFlag(FLAG_ACK);
			seq_foreign = seq;
			this.ack_array = ack_array; 
		}
		else {
			resetFlag(FLAG_ACK);
		}
	}
	
	public void setData(boolean active,boolean resend,int seq,Message msg) {
		if(active) {
			setFlag(FLAG_DATA);
			setFlag(FLAG_RESEND);
			this.seq_own = seq;
			this.msg = msg;
		}
		else {
			resetFlag(FLAG_DATA);
		}
	}

	private void setFlag(int flag) {
		flags |= flag;
	}
	
	private void resetFlag(int flag) {
		flags &= (-1 - flag);
	}
	
	public boolean getFlag(int flag) {
		//Extract flag
		if((flags & flag) != 0) return true;
		else return false;
	}
	
	
	
	private byte[] serializePacket() {
		return null;
	}
	
	private RUDPMessageContainer deserializePacket(byte[] data) {
		return null;
	}
}
