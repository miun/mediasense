package communication.rudp.socket.datagram;

import java.net.InetSocketAddress;

public class RUDPDatagramBuilder {
	private RUDPDatagramPacket packets[];

	InetSocketAddress address;
	
	private short fragAmount;
	private short fragCount;

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
		RUDPDatagramPacketOut packet;
		byte[] packetData;
		short fragmentCount;

		//Calculate the number of fragments needed
		fragmentCount = (short)Math.ceil((double)data.length / RUDPDatagramPacketOut.getMaxDataLength());
		
		//Create array for packets
		this.packets = new RUDPDatagramPacket[fragmentCount];

		for(short i = 0; i < fragmentCount; i++) {
			//New packet
			packet = new RUDPDatagramPacketOut();
			
			//Generate new buffer with correct size
			if(i == (fragmentCount - 1)) {
				//The last packet
				packetData = new byte[data.length - (i * RUDPDatagramPacketOut.getMaxDataLength())];
			}
			else {
				//Every other packet is FULL
				packetData = new byte[RUDPDatagramPacketOut.getMaxDataLength()];
			}

			//Create
			System.arraycopy(data,RUDPDatagramPacketOut.getMaxDataLength() * i,packetData,0,packetData.length);
			packet.setData(packetData,false);
			packet.setFragment(i, fragmentCount);
			packets[i] = packet;
		}

		this.fragAmount = fragmentCount;
		this.fragCount = fragmentCount;
	}
	
	public void assimilateFragment(RUDPDatagramPacket packet) {
		int fragNr;
	
		if(packet.getFlag(RUDPDatagramPacket.FLAG_FRAGMENT)) {
			fragNr = packet.getFragmentNr();
			
			synchronized(this) {
				if(fragNr <= fragCount) {
					//Count the fragments we already have
					if(packets[fragNr] == null) {
						fragAmount++;
					}
	
					//Assimilate data
					packets[fragNr] = packet;
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
	
	public InetSocketAddress getSocketAddress() {
		return address;
	}

	public RUDPDatagramPacket[] getFragmentedPackets() {
		//Return cloned list
		synchronized(this) {
			return packets.clone();
		}
	}
}