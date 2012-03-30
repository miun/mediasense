package manager;

import java.util.HashMap;

public class Communication {
	private HashMap<String,LookupServiceInterface> clients;
	
	public Communication() {
		clients = new HashMap<String,LookupServiceInterface>();
	}
	
	public boolean addClient(String address, LookupServiceInterface client) {
		if(!clients.containsKey(address)) {
			clients.put(address, client);
			return true;
		}else 
			return false;
	}
	
	public void removeClient(String address) {
		clients.remove(address);
	}
	
	/**
	 * Search the right client and trigger the handle event
	 * @param m message to the client
	 */
	public void sendMessage(Message m) {
		LookupServiceInterface receiver = null;
		receiver = clients.get(m.toIp);
		if(receiver!=null)
			receiver.handleMessage(m);
	}
}
