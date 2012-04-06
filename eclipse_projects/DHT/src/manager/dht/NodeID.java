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
	
	public NodeID add(NodeID hash) {
		int immediate;
		int carry = 0;
		byte[] temp = new byte[ADDRESS_SIZE];
		
		for(int i = 0; i < ADDRESS_SIZE; i++) {
			immediate = id[i] + hash.id[i] + carry;
			temp[i] = (byte)(immediate % 255); 
			carry = immediate >> 8;
		}
		
		return new NodeID(temp);
	}
	
	public static NodeID powerOfTwo(int n) {
		byte[] hash = new byte[ADDRESS_SIZE];
		if(n >= 0 && n < ADDRESS_SIZE * 8) {
			hash[n / 8] = (byte)(n % 8);
		}
		return null;
	}

	public NodeID Sub(NodeID hash) {
		return null;
	}
	
	public static int logTwoRoundUp(NodeID nodeID) {
		return 0;
	}
}
