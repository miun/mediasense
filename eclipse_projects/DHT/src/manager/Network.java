package manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import manager.dht.FingerEntry;
import manager.dht.NodeID;
import manager.dht.messages.broadcast.BroadcastMessage;
import manager.listener.FingerChangeListener;
import manager.listener.NodeMessageListener;

public class Network {
	private static Network instance = null;
	public static Integer msg_delay = 250; 
	
	//Listener lists
	private HashMap<Integer,Set<NodeMessageListener>> nodeMessageListener;
	private Set<FingerChangeListener> fingerChangeListener;

	//Client list
	private TreeMap<String,Communication> clients;
	
	private Network() {
		//Singleton class
		instance = this;
		clients = new TreeMap<String,Communication>();
		
		nodeMessageListener = new HashMap<Integer, Set<NodeMessageListener>>();
		fingerChangeListener = new HashSet<FingerChangeListener>();
	}
	
	public static Network getInstance() {
		//Return singleton object
		if(instance == null) instance = new Network();
		return instance;
	}
	
	public void sendMessage(Message m) {
		int messageType = m.getType();

		//Get the receiver of the message
		Communication receiver = null;
		receiver = clients.get(m.getToIp());
		
		//Send the message to the receiver
		if(receiver != null) {
			receiver.handleMessage(m);
		}
		else {
			System.out.println("!!!!! UNKNOWN DESTINATION !!!!!");
		}
		
		//Check whether it is a Broadcast message
		if (messageType == Message.BROADCAST) {
			//Extract the broadcast message
			messageType = ((BroadcastMessage)m).extractMessage().getType();
			System.out.println("NETWORK: " + messageType + "\n");
		}
		
		//Inform all NodeMessageListeners listening to that type of message
		if(nodeMessageListener.containsKey(messageType)) {
			for(NodeMessageListener nml: nodeMessageListener.get(messageType)) {
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
		List<Integer> intersections = new ArrayList<Integer>();

		Communication startClient = clients.get(startNodeName);
		Communication currentClient = startClient;

		NodeID start,end;
		int counter = 0;

		//Test if node exists
		if(startClient == null) {
			return "Cannot find node " + startNodeName + "\n"; 
		}
		
		//Set start and end region on DHT circle
		start  = currentClient.getNodeID();
		end = start;
		
		//Header
		StringBuffer result = new StringBuffer("Pos\tNetworkAddress\t||  NodeID\n");

		//Loop through the circle
		while(true) {
			//Get next node
			result.append(new Integer(counter).toString() + "\t" + currentClient.showNodeInfo()+"\n");
			alreadyShown.add(currentClient);
			currentClient = clients.get(currentClient.getSuccessorAddress());
			
			//Check for loop
			if(alreadyShown.contains(currentClient)) break;
			
			//Test for loop intersections
			if(start.compareTo(end) > 0) {
				if(currentClient.getNodeID().compareTo(start) >= 0 || currentClient.getNodeID().compareTo(end) <= 0) {
					//Intersection detected!!
					result.append(">>> Intersection detected <<<\n");
					intersections.add(counter);
				}
			}
			else {
				if(currentClient.getNodeID().compareTo(start) >= 0 && currentClient.getNodeID().compareTo(end) <= 0) {
					//Intersection detected!!
					result.append(">>> Intersection detected <<<\n");
					intersections.add(counter);
				}
			}
			
			//Shift end forward
			end = currentClient.getNodeID();
			counter++;
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
		
		//Show intersections
		if(intersections.size() > 0) {
			result.append("\nDHT contains " + new Integer(intersections.size()).toString() + " intersections @ ");
			for(int i: intersections) {
				result.append(new Integer(i).toString() + ",");
			}
		}
		
		return result.toString();
	}

	public void addFingerChangeListener(FingerChangeListener listener) {
		//Add listener to list
		fingerChangeListener.add(listener);
	}
	
	public void removeFingerChangeListener(FingerChangeListener listener) {
		//Remove listener
		fingerChangeListener.remove(listener);
	}
	
	public void fireFingerChangeEvent(int eventType,NodeID node,NodeID finger) {
		//Inform all listener
		for(FingerChangeListener l: fingerChangeListener) l.OnFingerChange(eventType, node, finger);
	}
	
	public String showFinger(String nodeAddress) {
		TreeMap<FingerEntry,FingerEntry> fingerTable;
		TreeMap<Integer,FingerEntry> localTable;
		
		FingerEntry finger;
		FingerEntry identity;
		
		Communication client;
		String result = "";
		int log2;
		
		//Get and check node
		client = clients.get(nodeAddress);
		if(client == null) return "Node " + nodeAddress + " not found!";
		
		//Get list
		fingerTable = client.getNode().getFingerTable();
		
		//Transform table
		localTable = new TreeMap<Integer,FingerEntry>();

		//Successor
		finger = client.getNode().getSuccessor(client.getNode().getIdentity().getNodeID());
		log2 = NodeID.logTwoFloor(finger.getNodeID().sub(client.getNode().getIdentity().getNodeID()));
		localTable.put(log2,finger);

		//For each finger
		for(FingerEntry fingerEntry: fingerTable.keySet()) {
			log2 = NodeID.logTwoFloor(fingerEntry.getNodeID().sub(client.getNodeID()));
			localTable.put(log2, fingerEntry);
		}
		
		//Print list
		for(int log2temp: localTable.keySet()) {
			finger = localTable.get(log2temp);
			result = result + "Addr: " + finger.getNetworkAddress() + " | hash:{" + finger.getNodeID().toString() + "} | log2: " + new Integer(log2temp).toString() + "\n";
		}
		
		return result;
	}
}
