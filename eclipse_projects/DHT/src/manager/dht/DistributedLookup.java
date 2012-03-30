package manager.dht;

import manager.LookupServiceInterface;
import manager.Message;

public class DistributedLookup implements LookupServiceInterface {
	//The one and only node (for the moment)
	Node node;
	
	public DistributedLookup() {
		node = new Node();
	}
	
	@Override
	public void resolve(String uci) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(String uci) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

}
