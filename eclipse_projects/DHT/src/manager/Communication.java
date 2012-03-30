package manager;

import java.util.HashMap;

import manager.dht.Node;

public class Communication {
	private HashMap<String,Node> nodes;
	
	public Communication() {
		nodes = new HashMap<String, Node>();
	}
	
	public void sendMessage(Message m) {
		//Node receiver = nodes.get(m.);
		
	}
}
