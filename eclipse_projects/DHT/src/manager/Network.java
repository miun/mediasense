package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import manager.listener.NodeMessageListener;

public class Network {
	private static Network instance = null;
	
	//Listener lists
	private List<NodeMessageListener> nodeMessageListener;

	//Client list
	private HashMap<String,Communication> clients;
	
	private Network() {
		//Singleton class
		instance = this;
		clients = new HashMap<String,Communication>();
		
		//Init lists
		nodeMessageListener = new ArrayList<NodeMessageListener>();
	}
	
	public static Network getInstance() {
		//Return singleton object
		if(instance == null) instance = new Network();
		return instance;
	}
	
	public void sendMessage(Message m) {
		//Get the receiver of the message
		Communication receiver = null;
		receiver = clients.get(m.toIp);
		
		//Send the message to the receiver
		if(receiver != null)
			receiver.handleMessage(m);
		
		//Inform all NodeMessageListeners about the message
		for(NodeMessageListener nml: nodeMessageListener) {
			nml.OnNodeMessage(m);
		}
	}

	public boolean addNode(Communication comm) {
		//Add node to list
		if(!clients.containsKey(comm.getLocalIp())) {
			clients.put(comm.getLocalIp(), comm);
			return true;
		} else 
			return false;
	}
	
	public void removeNode(String networkAddress) {
		clients.remove(networkAddress);
	}
	
	public void addNodeMessageListener(NodeMessageListener listener) {
		nodeMessageListener.add(listener);
	}
}
