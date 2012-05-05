package communication;

public class TestMessage extends Message {

	byte[] data;
	
	public TestMessage(String from, String to,byte[] data) {
		super(from, to, 123);
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
}
