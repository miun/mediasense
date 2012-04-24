package manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;

import manager.dht.DestinationNotReachableException;
import manager.dht.FingerEntry;
import manager.dht.Node;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;
import manager.listener.NodeMessageListener;

public class Network {
	private static Network instance = null;
	public static int msg_delay = 250;
	
	//Timer for package transmission
	private Timer timer;
	
	//Counter for sequential addresses
	private static int sequential_address = 0;
	
	//Listener lists
	private HashMap<Integer,Set<NodeMessageListener>> nodeMessageListener;
	private Set<NodeMessageListener> nodeMessageListenerAll;
	private Set<FingerChangeListener> fingerChangeListener;
	private Set<NodeListener> nodeListener;
	private Set<KeepAliveListener> keepAliveListener;

	//Client list
	private TreeMap<String,Communication> clients;
	
	private Network() {
		//Singleton class
		instance = this;
		clients = new TreeMap<String,Communication>();
		
		this.timer = new Timer();
		
		nodeMessageListener = new HashMap<Integer, Set<NodeMessageListener>>();
		nodeMessageListenerAll = new HashSet<NodeMessageListener>();
		fingerChangeListener = new HashSet<FingerChangeListener>();
		nodeListener = new HashSet<NodeListener>();
		keepAliveListener = new HashSet<KeepAliveListener>();
	}
	
	public static Network getInstance() {
		//Return singleton object
		if(instance == null) instance = new Network();
		return instance;
	}
	
	public String getRandomClientAddress(boolean mustBeConnected) {
		if(clients.size() > 0) {
			//create a list from all keys
			List<Communication> randomList = new LinkedList<Communication>(clients.values());
			
			//shuffle
			Collections.shuffle(randomList);
			Node cur = randomList.get(0).getNode();
			
			//It does not matter if it is a connected one - return the first
			return cur.getIdentity().getNetworkAddress();
			
		}
		else {
			//There are no clients
			return null;
		}
	}
	
	public static synchronized String createSequentialAddress() {
		//Create an address 
		String result = new Integer(sequential_address).toString(); 
		sequential_address++;
		return result;
	}
	
	public Collection<Communication> getClients() {
		synchronized(clients) {
			return this.clients.values();
		}
	}
	
	public void sendMessage(Message m, int senderDelay) throws DestinationNotReachableException{
		Communication receiver = null;
		
		synchronized(clients) {
			receiver = clients.get(m.getToIp());
		}
		
		//Send the message to the receiver
		if(receiver != null) {
			timer.schedule(new MessageForwarder(receiver, m, nodeMessageListener, nodeMessageListenerAll), msg_delay+receiver.getMessageDelay()+senderDelay);
		}
		else {
			//System.out.println("!!!!! UNKNOWN DESTINATION !!!!! " + m.toString());
			throw new DestinationNotReachableException("The destination: ("+m.getToIp()+") is not reachable");
		}
	}

	public void addNode(String address,String bootstrap) {
		Communication comm;
		Node node;
		
		//Add node to list
		synchronized(clients) {
			if(!clients.containsKey(address)) {
				//Create new DHT client
				comm = new Communication(getInstance(), address);
				node = new Node(comm, bootstrap);

				//Insert
				clients.put(address, comm);
				
				//start the Communication object
				comm.start(node);
				
				//Inform listeners
				synchronized(nodeListener) {
					for(NodeListener nl: nodeListener) nl.onNodeAdd(new Date(),comm);
				}
			} 
		}
	}
	
	public void removeNode(String networkAddress) {
		Communication com;
		
		//Remove and shutdown
		synchronized (clients) {
			com = clients.remove(networkAddress);
		}
		
		if(com != null) {
			//Shutdown
			com.shutdown();

			//Print
			System.out.println("Shut down " + com.getLocalIp());
	
			//Inform listeners
			synchronized(nodeListener) {
				for(NodeListener nl: nodeListener) nl.onNodeRemove(new Date(),com);
			}
		}
	}
	
	public void killNode(String networkAddress) {
		Communication com;
		
		//Remove and shutdown
		synchronized (clients) {
			com = clients.remove(networkAddress);
		}
		
		if(com != null) {
			//Kill
			com.kill();

			//Print
			System.out.println("Killed " + com.getLocalIp());
	
			//Inform listeners
			synchronized(nodeListener) {
				for(NodeListener nl: nodeListener) nl.onNodeRemove(new Date(),com);
			}
		}
	}
	
	public void addNodeMessageListener(Integer msgType,NodeMessageListener listener) {
		synchronized(nodeMessageListener) {
			if(msgType != null) {
				//Don't add the listener if it listens to all messages
				if(!nodeMessageListenerAll.contains(listener)) {
					//Check if list exists, otherwise create it
					if(!nodeMessageListener.containsKey(msgType)) {
						nodeMessageListener.put(msgType,new HashSet<NodeMessageListener>());
					}
					
					//Get listener list
					Set<NodeMessageListener> nml = nodeMessageListener.get(msgType);
					nml.add(listener);
				}
			}
			else {
				//msgType == null means listen to all messages
				nodeMessageListenerAll.add(listener);
				
				for(Set<NodeMessageListener> s: nodeMessageListener.values()) {
					//Remove listener
					s.remove(listener);
				}
			}
		}
	}
	
	public void removeNodeMessageListener(Integer msgType,NodeMessageListener listener) {
		synchronized(nodeMessageListener) {
			if(msgType != null) {
				Set<NodeMessageListener> listeners = nodeMessageListener.get(msgType);
				if(listeners!=null) {
					listeners.remove(listener);
				}
			}
			else {
				nodeMessageListenerAll.remove(listener);
			}
		}
	}
	
	public boolean setMessageDelay(int delay,String networkAddress) {
		if(networkAddress == null) {
			msg_delay = delay;
			return true;
		}
		else {
			Communication comm;
			
			//Find node and set delay
			synchronized(clients) {
				comm = clients.get(networkAddress);
			}

			if(comm != null) {
				comm.setMessageDelay(delay);
				return true;
			}
			else return false;
		}
	}
	
	/**
	 * Returns the messagedelay for a specific node or the network
	 * @param networkAddress if null the delay from the network is returned, if not null the 
	 * the delay from the client with the given address will be returned or -1 if there is no
	 * such client
	 * @return the delay from the network or a specific client. 
	 */
	public int getMessageDelay(String networkAddress) {
		if(networkAddress == null) {
			//return the delay of the network
			return msg_delay;
		} else {
			Communication com = clients.get(networkAddress);
			if(com != null) {
				return com.getMessageDelay();
			} else {
				return -1;
			}
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
		synchronized(clients) {
			for(Communication comm: clients.values()) {
				result += comm.showNodeInfo() + "\n";
			}
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
		Communication comm;
		
		synchronized(clients) {
			comm = clients.get(networkAddress);
		}
		
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

		Communication startClient;
		Communication currentClient;

		NodeID start,end;
		int counter = 0;
		
		synchronized(clients) {
			startClient = clients.get(startNodeName);
			currentClient = startClient;
		}

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
			
			synchronized(clients) {
				currentClient = clients.get(currentClient.getSuccessorAddress());
			}
			
			//Hole detected!
			if(currentClient == null) break;
			
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
		else if(currentClient == null) {
			//Hole detected
			result.append("Hole detected! Successor is NULL. Iterated over " + alreadyShown.size() + " nodes of " + clients.size());
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
		synchronized(fingerChangeListener) {
			fingerChangeListener.add(listener);
		}
	}
	
	public void removeFingerChangeListener(FingerChangeListener listener) {
		//Remove listener
		synchronized (fingerChangeListener) {
			fingerChangeListener.remove(listener);
		}
	}
	
	public void fireFingerChangeEvent(int eventType,FingerEntry node,FingerEntry finger) {
		//Inform all listener
		synchronized(fingerChangeListener) {
			for(FingerChangeListener l: fingerChangeListener) {
				l.OnFingerChange(eventType, node, finger);
			}
		}
	}
	
	public String showFinger(String nodeAddress) {
		TreeMap<FingerEntry,FingerEntry> fingerTable;
		TreeMap<Integer,FingerEntry> showTable;
		
		FingerEntry currentFinger;
		FingerEntry successorFinger;
		FingerEntry predecessorFinger;
		
		Communication client;
		String result = "";
		int log2;
		
		//Get and check node
		synchronized(clients) {
			client = clients.get(nodeAddress);
			if(client == null) return "Node " + nodeAddress + " not found!";
			
			//Get successor and predecessor from client
			successorFinger = client.getNode().getSuccessor(null);
			predecessorFinger = client.getNode().getPredecessor();
		}
		
		//Get list from client
		fingerTable = client.getNode().getFingerTable();
		
		//Transform table
		showTable = new TreeMap<Integer,FingerEntry>();

		//For each finger
		FingerEntry fingerEntry = successorFinger;
		
		while(!fingerEntry.getNodeID().equals(client.getNodeID())) {
			//Insert finger
			log2 = NodeID.logTwoFloor(fingerEntry.getNodeID().sub(client.getNodeID()));
			showTable.put(log2, fingerEntry);
			
			//Next finger
			fingerEntry = getSuccessorFinger(fingerTable, fingerEntry.getNodeID());//client.getNode().getSuccessor(fingerEntry.getNodeID());
		}
		
		//Print list
		for(int log2temp: showTable.keySet()) {
			currentFinger = showTable.get(log2temp);
			
			if(currentFinger.equals(successorFinger)) {
				result = result + "Addr: " + currentFinger.getNetworkAddress() + " | hash:{" + currentFinger.getNodeID().toString() + "} | log2: " + new Integer(log2temp).toString() + " SUC\n";
			}
			else {
				result = result + "Addr: " + currentFinger.getNetworkAddress() + " | hash:{" + currentFinger.getNodeID().toString() + "} | log2: " + new Integer(log2temp).toString() + "\n";
			}
		}
		
		//Add the predecessor at the very end if there is a predecessor
		if(predecessorFinger!=null) {
			log2 = NodeID.logTwoFloor(predecessorFinger.getNodeID().sub(client.getNodeID()));
			result = result + "Addr: " + predecessorFinger.getNetworkAddress() + " | hash:{" + predecessorFinger.getNodeID().toString() + "} | log2: " + new Integer(log2).toString() + " PRE\n";
		} else {
			result = result + "currently no predeccessor\n";
		}
		return result;
	}
	
	public void addNodeListener(NodeListener nl) {
		synchronized(nodeListener) {
			nodeListener.add(nl);
		}
	}
	
	public void removeNodeListener(NodeListener nl) {
		synchronized(nodeListener) {
			nodeListener.remove(nl);
		}
	}
	
	public void addKeepAliveListener(KeepAliveListener listener) {
		synchronized(keepAliveListener) {
			keepAliveListener.add(listener);
		}
	}
		
	public void removeKeepAliveListener(KeepAliveListener listener) {
		synchronized(keepAliveListener) {
			keepAliveListener.remove(listener);
		}
	}
	
	public void fireKeepAliveEvent(NodeID key,String networkAddress) {
		//Call each handler
		synchronized(keepAliveListener) {
			for(KeepAliveListener kal: keepAliveListener) {
				kal.OnKeepAliveEvent(new Date(), key, networkAddress);
			}
		}
	}

	public double calculateHealthOfDHT(boolean listMissingFinger) {
		TreeMap<FingerEntry,FingerEntry> fingerTable;
		TreeMap<FingerEntry,Communication> dht;
		
		FingerEntry currentSuccessor;
		FingerEntry bestSuccessor;
		NodeID hash_log2;
		
		int count_max = 0;
		int count_ok = 0;
		
		//Print caption
		if(listMissingFinger) System.out.println("Printing missing finger list...");

		//Copy DHT into a map accessible through the NodeID  
		dht = new TreeMap<FingerEntry,Communication>();

		synchronized(clients) {
			for(Communication client: clients.values()) {
				FingerEntry newFinger;
				newFinger = new FingerEntry(client.getNodeID(),client.getLocalIp());
				dht.put(newFinger,client);
			}
		}
		
		//Check the quality of each client's finger table
		for(Communication client : dht.values()) {
			//Get finger table
			fingerTable = client.getNode().getFingerTable();
			
			//Check each finger
			for(int i = 0; i < NodeID.ADDRESS_SIZE * 8; i++) {
				//Get current finger, if any, of the DHT region specified by log2 
				hash_log2 = NodeID.powerOfTwo(i).add(client.getNodeID());
				currentSuccessor = getSuccessorFinger(fingerTable,hash_log2);
				bestSuccessor = getSuccessorDHT(dht,hash_log2);
				
				//Compare
				if(!bestSuccessor.equals(client.getNode().getIdentity())) {
					//If a node exists there must be finger
					count_max++;
				
					if(currentSuccessor.equals(bestSuccessor)) {
						//Current successor is best successor
						count_ok++;
					}
					else {
						if(listMissingFinger) {
							//Show missing finger
							System.out.println("Node: (" + client.getLocalIp() + ") MISSING: (" + bestSuccessor.getNetworkAddress() + ")" );
						}
					}

					//Set new log2 to skip unnecessary ranges
					i = NodeID.logTwoFloor(bestSuccessor.getNodeID().sub(client.getNodeID()));
				}
				else {
					//Finished all nodes
					break;
				}
			}
		}
		
		//Return rate of DHT health#
		//If there is only one node there can't be a finger, therefore the DHT is OK
		return count_max > 0 ? (double)count_ok / count_max : 1.0;
	}

	private FingerEntry getSuccessorFinger(TreeMap<FingerEntry,FingerEntry> table,NodeID nodeID) {
		FingerEntry hash = new FingerEntry(nodeID,null);
		FingerEntry result;

		synchronized(table) {
			//Get successor of us
			result = table.higherKey(hash);
			if(result == null) { 
				//There is no higher key in the finger tree
				result = table.firstKey();
			}
		}
		
		return result;
	}

	private FingerEntry getSuccessorDHT(TreeMap<FingerEntry,Communication> table,NodeID nodeID) {
		FingerEntry hash = new FingerEntry(nodeID,null);
		FingerEntry result;

		synchronized(table) {
			//Get successor of us
			result = table.higherKey(hash);
			if(result == null) { 
				//There is no higher key in the finger tree
				result = table.firstKey();
			}
		}
		
		return result;
	}
	
	public int getNumberOfClients() {
		return clients.size();
	}

	public void breakNode(String address) {
		Communication client;
		
		client = clients.get(address);
		if(client != null) client.getNode().debugBreak();
	}
}
