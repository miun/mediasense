package manager;

import java.util.HashMap;

import manager.listener.NodeMessageListener;

public class Network {
	private HashMap<String,LookupServiceInterface> clients;
	
	public Network() {
		clients = new HashMap<String,LookupServiceInterface>();
	}
	
	public void sendMessage(Message m) {
		//Get the receiver of the message
		LookupServiceInterface receiver = null;
		receiver = clients.get(m.toIp);
		//Send the message to the receiver
		if(receiver!=null)
			receiver.handleMessage(m);
		//Inform all NodeMessageListeners about the message
	}
	
	public boolean addNode(String address, LookupServiceInterface node) {
		if(!clients.containsKey(address)) {
			clients.put(address, node);
			return true;
		} else 
			return false;
	}
	
	public void removeNode(String address) {
		clients.remove(address);
	}
}
