package communication.rudp.socket;

public class RUDPDatagram {
	private byte[] data;
	
	public RUDPDatagram(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
}
