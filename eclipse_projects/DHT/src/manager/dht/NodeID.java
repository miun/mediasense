package manager.dht;

public class NodeID implements Comparable<NodeID> {
	public static final int ADDRESS_SIZE = 20;
	private byte[] id = new byte[ADDRESS_SIZE];
	
	public NodeID(byte[] id) {
		this.id = id;
	}
	
	public byte[] getID() {
		return id;
	}
	
	public String toString() {
		return SHA1Generator.convertToHex(id);
	}

	@Override
	public int compareTo(NodeID comp) {
		//Its us!
		if(comp == this) return 0;
		
		for(int i = 0; i < ADDRESS_SIZE; i++) {
			if((comp.id[i] < 0 ? comp.id[i] + 255 : comp.id[i]) > (id[i] < 0 ? id[i] + 255 : id[i])) return -1;
			else if((comp.id[i] < 0 ? comp.id[i] + 255 : comp.id[i]) < (id[i] < 0 ? id[i] + 255 : id[i])) return 1;
		}

		//Equal
		return 0;
	}
	
	//-----
	//Static math functions
	//-----
	
	public static NodeID add(NodeID hash) {
		for(int i = 0; )
		
		return null;
	}
	
	public static NodeID powerOfTwo(int n) {
		byte[] hash = new byte[ADDRESS_SIZE];
		if(n >= 0 && n < ADDRESS_SIZE * 8) {
			hash[n / 8] = (byte)(n % 8);
		}
		return null;
	}
}
