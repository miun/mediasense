package manager.dht;

public class NodeID implements Comparable<NodeID> {
	public static final int ADDRESS_SIZE = 1;
	private byte[] id = new byte[ADDRESS_SIZE];
	
	public NodeID(byte[] id) {
		this.id = id;
	}
	
	public byte[] getID() {
		return id;
	}
	
	public String toString() {
		return String.valueOf(Integer.parseInt(SHA1Generator.convertToHex(id),16));
	}

	@Override
	public int compareTo(NodeID comp) {
		//Its us!
		if(comp == this) return 0;
		
		for(int i = 0; i < ADDRESS_SIZE; i++) {
			if(comp.id[i] > id[i]) return 1;
			else if(comp.id[i] < id[i]) return -1;
		}

		//Equal
		return 0;
	}
	
	
}
