package communication.rudp.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class RUDPDatagram {
	private Exception exception;
	private InetAddress dst;
	private int port;
	
	private boolean isDeployed = false;
	
	//private byte[][] data;
	private short fragAmount;
	private short fragCount;
	private RUDPDatagramPacket[] data;
	
	public RUDPDatagram(InetAddress dst,int port,Exception e) {
		//Datagram contains an exception
		this.dst = dst;
		this.port = port;
		exception = e;
	}
	
	public RUDPDatagram(InetAddress dst,int port,byte[] data) {
		//Datagram contains data
		this.dst = dst;
		this.port = port;
		//this.data = new byte[1][]; 
		//this.data[0] = data;
		this.fragAmount = 1;
		this.fragCount = 1;
				
		//TODO fragmentation
		List<RUDPDatagramPacket> packetList = new ArrayList<RUDPDatagramPacket>();
		int dataSize;
		int dataLen;
		int remainingPacketLength;
		RUDPDatagramPacket packet;
		
		dataSize = data.length;
		dataLen = dataSize;
		
		//new packet
		packet = new RUDPDatagramPacket();
		
		if(dataSize > packet.getMaxDataLength()) {
			short fragmentCounter = 0;
			
			//fragmentation
			for(int offset = 0; offset < dataSize;) {
				//Create datagram packet
				packet.setFragment(fragmentCounter,(short)0);
				remainingPacketLength = packet.getMaxDataLength();
				packet.setData(data, offset,remainingPacketLength < dataLen ? remainingPacketLength : dataLen , false);
				packetList.add(packet);
				
				//Increment offset
				offset += remainingPacketLength;
				dataLen -= remainingPacketLength; 
				
				//Create new packet
				packet = new RUDPDatagramPacket();
				//packet.setDataFlag(true);
				
				//Increment fragment and sequence counter
				fragmentCounter++;
			}
			
			//Allocate array
			this.data = new RUDPDatagramPacket[fragmentCounter];
			
			//Set fragment count, because now we know it and put to data
			for(RUDPDatagramPacket p: packetList) {
				p.setFragment(p.getFragmentNr(), (short)packetList.size());
				this.data[p.getFragmentNr()] = p;
			}
			
			
			this.fragAmount = fragmentCounter;
			this.fragCount = fragmentCounter;
		}
		else {
			//Only one packet in this datagram
			packet.setData(data, 0, data.length, false);
			this.data = new RUDPDatagramPacket[1];
			this.data[0] = packet;
		}
		
	}
	
	public RUDPDatagram(InetAddress dst,int port,short fragCount) {
		//Datagram is a fragmented datagram
		this.dst = dst;
		this.port = port;
		this.data = new RUDPDatagramPacket[fragCount];
		this.fragCount = fragCount;
		this.fragAmount = 0;
	}
	
	public void assimilateFragment(RUDPDatagramPacket packet) {
		int fragNr;

		if(packet.getFlag(RUDPDatagramPacket.FLAG_FRAGMENT)) {
			fragNr = packet.getFragmentNr();
			
			synchronized(this) {
				if(fragNr <= fragCount) {
					//Count the fragments we already have
					if(this.data[fragNr] == null) {
						fragAmount++;
					}
	
					//Assimilate data
					this.data[fragNr] = packet;
				}
			}
		}
	}
	
	public InetAddress getDst() {
		return dst;
	}

	public int getPort() {
		return port;
	}

	public byte[] getData() {
		//Return received data
		if(isComplete()) {
			int totalSize = 0;
			int offset = 0;
			byte[] result;
			
			//Count total size
			synchronized(this) {
				for(int i = 0; i < fragCount; i++) totalSize += data[i].getData().length;
				result = new byte[totalSize];
				
				//Create one large datagram
				for(int i = 0; i < fragCount; i++) {
					System.arraycopy(data[i].getData(),0,result,offset,data[i].getData().length);
					offset += data[i].getData().length;
				}
			}
			
			return result;
		}
		else {
			return null;
		}
	}
	
	public short getFragmentCount() {
		return fragCount;
	}
	
	public boolean isComplete() {
		return fragAmount == fragCount;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public InetSocketAddress getSocketAddress() {
		return new InetSocketAddress(dst,port);
	}
	
	public void setDeployed() {
		isDeployed = true;
	}
	
	public boolean isDeployed() {
		return isDeployed;
	}
	
	public void setAckSent() {
		for(RUDPDatagramPacket p: data) {
			p.setIsAckSent();
		}
	}
	
	public boolean isAcknowledged() {
		//TODO what to return without data or incomplete data
		for(RUDPDatagramPacket p: data) {
			if(!p.isAckSent()) {
				return false;
			}
		}
		return true;
	}
	
	public void setPacketsSendable(Timer t, RUDPPacketSenderInterface l) {
		//Give the timer and listener to the packets
		for(RUDPDatagramPacket p: data) {
			p.setTimer(t);
			p.setListener(l);
		}
	}
	
	public RUDPDatagramPacket[] getFragments() {
		return data.clone();
	}
}
