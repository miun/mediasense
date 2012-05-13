package communication.rudp.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class RUDPDatagram {
	private Exception exception;
	private InetAddress dst;
	private int port;
	
	private byte[][] data;
	private short fragAmount;
	private short fragCount;
	
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
		this.data = new byte[1][]; 
		this.data[0] = data;
		this.fragAmount = 1;
		this.fragCount = 1;
		
		//TODO fragmentation
		
	}
	
	public RUDPDatagram(InetAddress dst,int port,short fragCount) {
		//Datagram is a fragmented datagram
		this.dst = dst;
		this.port = port;
		this.data = new byte[fragCount][]; 
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
					this.data[fragNr] = packet.getData();
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
				for(int i = 0; i < fragCount; i++) totalSize += data[i].length;
				result = new byte[totalSize];
				
				//Create one large datagram
				for(int i = 0; i < fragCount; i++) {
					System.arraycopy(data[i],0,result,offset,data[i].length);
					offset += data[i].length;
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
}
