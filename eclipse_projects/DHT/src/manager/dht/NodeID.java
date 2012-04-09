package manager.dht;

import java.util.Arrays;

public class NodeID implements Comparable<NodeID> {
	public static final int ADDRESS_SIZE = 20;
	private byte[] id = new byte[ADDRESS_SIZE];
	
	public static NodeID getMaxNodeID() {
		byte[] maxNodeID = new byte[ADDRESS_SIZE];
		for(int i=0;i<ADDRESS_SIZE;i++) {
			maxNodeID[i] = Byte.MAX_VALUE;
		}
		return new NodeID(maxNodeID);
	}
	
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeID other = (NodeID) obj;
		if (!Arrays.equals(id, other.id))
			return false;
		return true;
	}

	//Add two node hash values
	public NodeID add(NodeID hash) {
		int immediate;
		int carry = 0;
		byte[] temp = new byte[ADDRESS_SIZE];
		
		for(int i = ADDRESS_SIZE - 1; i >= 0; i--) {
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
		
		for(int i = ADDRESS_SIZE - 1; i >= 0; i--) {
			immediate = id[i] - hash.id[i] - carry;
			temp[i] = (byte)(immediate % 255);
			if(immediate < -128 || immediate > 127) carry = 1;
			//carry = immediate >> 8;
		}
		
		return new NodeID(temp);
	}
	
	//Subtract integers
	public NodeID sub(int n) {
		byte hash[] = new byte[ADDRESS_SIZE];
		
		for(int i = 0; i < ADDRESS_SIZE; i++) {
			hash[ADDRESS_SIZE - i - 1] = (byte)(n % 256);
			n = n >> 8;
		}
		
		return sub(new NodeID(hash));
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
					if(temp > 255 || temp < 0) {
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
	
	public boolean between(NodeID start,NodeID end) {
		//Check if THIS node is in [start,end]
		if(start.compareTo(end) < 0) {
			//Node must be INSIDE of the range to be in between
			if(this.compareTo(start) >= 0 && this.compareTo(end) <= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else if(start.compareTo(end) > 0) {
			//Node must be OUTSIDE of the range to be in between ;-)
			if(this.compareTo(start) >= 0 || this.compareTo(end) <= 0) {
				return true;
			}
			else {
				return false;
			}
		}
		else return false;
	}
}
