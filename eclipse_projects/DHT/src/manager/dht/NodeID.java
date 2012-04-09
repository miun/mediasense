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
		//Long version
		//return SHA1Generator.convertToHex(id);

		//Shorter version
		String result = SHA1Generator.convertToHex(id);
		return result.substring(0,4) + "..." + result.substring(36,40); 
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
	
	//Add two node hash values
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
			hash[(NodeID.ADDRESS_SIZE - 1) - (n / 8)] = (byte)(1 << (n % 8));
		}
		return new NodeID(hash);
	}

	//Subtract two node hash values
	public NodeID sub(NodeID hash) {
		int immediate;
		int carry = 0;
		byte[] temp = new byte[ADDRESS_SIZE];
		
		for(int i = 0; i < ADDRESS_SIZE; i++) {
			immediate = id[i] - hash.id[i] - carry;
			temp[i] = (byte)(immediate % 255); 
			carry = immediate >> 8;
		}
		
		return new NodeID(temp);
	}
	
	//TODO figure out if this works!!!
	public static int logTwoFloor(NodeID nodeID) {
		int temp;
		
		//For each byte
		for(int i = 0; i < NodeID.ADDRESS_SIZE; i++) {
			if(nodeID.id[i] != 0) {
				temp = nodeID.id[i];
				
				//For each bit
				for(int j = 0; j < 8; j++) {
					temp = temp << 1;
					if(temp > 255) {
						//Return found position
						return (NodeID.ADDRESS_SIZE * 8 - 1) - (i * 8) - j;
					}
				}
			}
		}
		
		return 0;
	}
	
	public static int logTwoCeil(NodeID nodeID) {
		//Easy as that :-)
		return logTwoFloor(nodeID) + 1;
	}
}
