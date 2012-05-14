package communication.rudp.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class RUDPDatagramBuilder {
	private RUDPDatagramPacket[] packets;
	private Exception exception;

	InetSocketAddress address;
	
	private short fragAmount;
	private short fragCount;

	private boolean isDeployed = false;

	public RUDPDatagramBuilder(InetSocketAddress address,short fragCount) {
		//Datagram is a fragmented datagram
		this.address = address;
		this.packets = new RUDPDatagramPacket[fragCount];
		this.fragCount = fragCount;
		this.fragAmount = 0;
	}
	
	public RUDPDatagramBuilder(InetSocketAddress address,RUDPDatagramPacket packet) {
		//Datagram is a fragmented datagram
		this.address = address;
		this.packets = new RUDPDatagramPacket[1];
		this.packets[0] = packet;
		this.fragCount = 1;
		this.fragAmount = 1;
	}
	
	public RUDPDatagramBuilder(RUDPDatagram dgram) {
		this.address = dgram.getSocketAddress();
		
		fragment(dgram.getData());
	}
	
	public RUDPDatagram toRUDPDatagram() {
		//Construct a user RUDPDatagram
		if(isComplete()) {
			int totalSize = 0;
			int offset = 0;
			byte[] data;
			
			//Count total size
			synchronized(this) {
				for(int i = 0; i < fragCount; i++) totalSize += packets[i].getData().length;
				data = new byte[totalSize];
				
				//Create one large datagram
				for(int i = 0; i < fragCount; i++) {
					System.arraycopy(packets[i].getData(),0,data,offset,packets[i].getData().length);
					offset += packets[i].getData().length;
				}
			}
			
			return new RUDPDatagram(address, data);
		}
		else {
			return null;
		}
	}

	private void fragment(byte[] data) {
		List<RUDPDatagramPacket> packetList = new ArrayList<RUDPDatagramPacket>();
		RUDPDatagramPacket packet;
	
		byte[] packetData;
		int dataSize;
		int dataLen;
		int remainingPacketLength;
		
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
				
				packetData = new byte[remainingPacketLength < dataLen ? remainingPacketLength : dataLen];
				System.arraycopy(data,offset,packetData,0,packetData.length);
				packet.setData(packetData,false);
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
			this.packets = new RUDPDatagramPacket[fragmentCounter];
			
			//Set fragment count, because now we know it and put to data
			for(RUDPDatagramPacket p: packetList) {
				p.setFragment(p.getFragmentNr(), (short)packetList.size());
				this.packets[p.getFragmentNr()] = p;
			}
			
			this.fragAmount = fragmentCounter;
			this.fragCount = fragmentCounter;
		}
		else {
			//Only one packet in this datagram
			packet.setData(data.clone(),false);
			this.packets = new RUDPDatagramPacket[1];
			this.packets[0] = packet;
			this.fragAmount = 1;
			this.fragCount = 1;
		}
	}
	
	public void assimilateFragment(RUDPDatagramPacket packet) {
		int fragNr;
	
		if(packet.getFlag(RUDPDatagramPacket.FLAG_FRAGMENT)) {
			fragNr = packet.getFragmentNr();
			
			synchronized(this) {
				if(fragNr <= fragCount) {
					//Count the fragments we already have
					if(this.packets[fragNr] == null) {
						fragAmount++;
					}
	
					//Assimilate data
					this.packets[fragNr] = packet;
				}
			}
		}
	}

	public short getFragmentCount() {
		return fragCount;
	}
	
	public short getFragmentAmount() {
		return fragAmount;
	}
	
	public boolean isComplete() {
		return fragAmount == fragCount;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public InetSocketAddress getSocketAddress() {
		return address;
	}
	
	public void setDeployed() {
		isDeployed = true;
	}
	
	public boolean isDeployed() {
		return isDeployed;
	}
	
	public void setAckSent() {
		for(RUDPDatagramPacket p: packets) {
			p.setIsAckSent();
		}
	}

	public boolean isAckSent() {
		//TODO what to return without data or incomplete data
		for(RUDPDatagramPacket p: packets) {
			if(p == null || !p.isAckSent()) {
				return false;
			}
		}
		return true;
	}

	public void setPacketsSendable(Timer t, RUDPPacketSenderInterface l) {
		//Give the timer and listener to the packets
		for(RUDPDatagramPacket p: packets) {
			p.setTimer(t);
			p.setListener(l);
		}
	}

	public RUDPDatagramPacket[] getFragmentedPackets() {
		return packets.clone();
	}
}