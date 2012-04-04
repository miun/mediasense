package manager;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import manager.dht.NodeID;
import manager.listener.NodeMessageListener;

public class Network {
	private static Network instance = null;
	public static Integer msg_delay = 250; 
	
	//Listener lists
	//private List<NodeMessageListener> nodeMessageListener;
	private HashMap<Integer,Set<NodeMessageListener>> nodeMessageListener;

	//Client list
	private HashMap<String,Communication> clients;
	
	private Network() {
		//Singleton class
		instance = this;
		clients = new HashMap<String,Communication>();
		
		nodeMessageListener = new HashMap<Integer, Set<NodeMessageListener>>();  
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
		
		//Inform all NodeMessageListeners listening to that type of message
		if(nodeMessageListener.containsKey(m.type)) {
			for(NodeMessageListener nml: nodeMessageListener.get(m.type)) {
				nml.OnNodeMessage(new Date(),m);
			}
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
	
	public void addNodeMessageListener(int msgType,NodeMessageListener listener) {
		//Check if list exists, otherwise create it
		if(!nodeMessageListener.containsKey(msgType)) {
			nodeMessageListener.put(msgType,new HashSet<NodeMessageListener>());
		}
		
		//Get listener list
		Set<NodeMessageListener> nml = nodeMessageListener.get(msgType);
		nml.add(listener);
	}
	
	public void removeNodeMessageListener(int msgType,NodeMessageListener listener) {
		Set<NodeMessageListener> listeners = nodeMessageListener.get(msgType);
		if(listeners!=null) {
			listeners.remove(listener);
		}
	}
	
	public boolean setMessageDelay(int delay,String networkAddress) {
		if(networkAddress == null) {
			msg_delay = delay;
			return true;
		}
		else {
			//Find node and set delay
			Communication comm = clients.get(networkAddress);
			
			if(comm != null) {
				comm.setMessageDelay(delay);
				return true;
			}
			else return false;
		}
	}
	
	/**
	 * Returns a String representing detailed information about all nodes 
	 * in the network 
	 * @return A String representing detailed information about all nodes in
	 * the network
	 * @see public String showNodeInfo(String networkAddress)
	 */
	public String showNodeInfo() {
		String result = "";
		
		//For each node
		for(Communication comm: clients.values()) {
			result += comm.showNodeInfo() + "\n";
		}
		
		return result;
	}
	
	/**
	 * This method returns a String representing detailed information about the 
	 * node that belongs to the network address given as parameter
	 * @param networkAddress from the Node to show
	 * @return A String representing detailed Node information
	 */
	public String showNodeInfo(String networkAddress) {
		//forward to the communication
		Communication comm = clients.get(networkAddress);
		if(comm == null) {
			return "There is no node with networkAddress {" + networkAddress + "}";
		}
		else {
			return comm.showNodeInfo();
		}
	}
	
	/**
	 * This method return a String representing the whole circle structure
	 * starting at the Node which belongs to the Network address given as
	 * parameter
	 * @param startNodeName Entry point for the circle
	 * @return A string that represents the circle if it is ok, or shows
	 * error information if not
	 */
	public String showCircle(String startNodeName) {
		HashSet<Communication> alreadyShown = new HashSet<Communication>();

		Communication startClient = clients.get(startNodeName);
		Communication currentClient = startClient;
		NodeID start = currentClient.getNodeID();
		NodeID end = start;

		//Test if node exists
		if(startClient == null) {
			return "Cannot find node " + startNodeName + "\n"; 
		}
				
		//Header
		StringBuffer result = new StringBuffer("NetworkAddress\t||  NodeID\n");

		//Loop through the circle
		while(true) {
			//Get next node
			result.append(currentClient.showNodeInfo()+"\n");
			alreadyShown.add(currentClient);
			currentClient = clients.get(currentClient.getSuccessorAddress());
			
			//Check for loop
			if(alreadyShown.contains(currentClient)) break;
			
			//Test for loop intersections
			if(start.compareTo(end) > 0) {
				if(currentClient.getNodeID().compareTo(start) >= 0 || currentClient.getNodeID().compareTo(end) <= 0) {
					//Intersection detected!!
					result.append(">>> Intersection detected <<<\n");
				}
			}
			else {
				if(currentClient.getNodeID().compareTo(start) >= 0 && currentClient.getNodeID().compareTo(end) <= 0) {
					//Intersection detected!!
					result.append(">>> Intersection detected <<<\n");
				}
			}
			
			//Shift end forward
			end = currentClient.getNodeID();
		};
		
		if(currentClient == startClient) {
			//Circle does not contain side-loop
			if(alreadyShown.size() < clients.size()) {
				result.append("DHT has orphaned nodes!\nIterated over " + alreadyShown.size() + " of " + clients.size());
			}
			else {
				result.append("DHT is OK!\nIterated over all " + alreadyShown.size() + " nodes!");
			}
		}
		else {
			//Circle contains a side-loop! 
			result.append("Aborting iteration! DHT contains side-loop!\nLoop destination is: " + currentClient.showNodeInfo() + "\nIterated over " + alreadyShown.size() + " Nodes of " + clients.size());
		}
		
		return result.toString();
	}
}
