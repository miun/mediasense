package manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import manager.dht.DestinationNotReachableException;
import manager.dht.FingerEntry;
import manager.dht.Node;
import manager.dht.NodeID;


/**
 * The communication simulates the communication layer between the nodes. 
 * To test our implementation of the DHT on its own this class
 * has been built. 
 * @author florianrueter
 *
 */
public class Communication extends Thread implements CommunicationInterface {
	private Network network;
	private Node node = null;
	private BlockingQueue<Message> queue;
	private String networkAddress;
	private int messageDelay = 1;
	
	/**
	 * Creates the Communication object.
	 * @param network the network handling the communication
	 * @param networkAddress representing this Communication object
	 */
	public Communication(Network network,String networkAddress) {
		this.network = network;
		this.networkAddress = networkAddress;
		
		//Create message queue
		queue = new LinkedBlockingQueue<Message>();
	}
	
	/**
	 * initialise this Communication object with a Node object and trigger the the Thread.start() method.
	 * @param node to initialize this Communication object.
	 */
	public void start(Node node) {
		//Start
		if(node == null) return;
		this.node = node;
		this.start();
	}
	
	/**
	 * send a message over the network
	 * @param m message to send
	 */
	public void sendMessage(Message m) throws DestinationNotReachableException {
		//Foward to network
		network.sendMessage(m,messageDelay);
	}
	
	/**
	 * Adds a message to the queue, that will be taken one after another
	 * @param msg to handle
	 */
	public void handleMessage(Message msg) {
		//Add message to queue
		queue.add(msg);
	}
	
	/**
	 * Sets the message delay for this communication object. This delay is added only on this Communication
	 * object to the global network delay.
	 * @param delay in milliseconds
	 */
	public void setMessageDelay(Integer delay) {
		this.messageDelay = delay;
	}
	
	/**
	 * 
	 * @return The network address and the NodeID object, which belong to the Node object that belong to this
	 * communication object, in a human-readable format.
	 */
	public String showNodeInfo() {
		if(node!=null) {
			return "Node_info{" + networkAddress + "}\t||  NodeID{0x" + node.getIdentity().getNodeID().toString() + "}\tconnected: " + (node.getStateConnected() ? "YES" : "NO") + "\t blockedFor: " + (node.getStateBlockJoinFor() == null ? "-" : node.getStateBlockJoinFor());
		} else {
			return "Node_info{" + networkAddress + "}: Node not started";
		}
	}
	
	/**
	 * 
	 * @return the network address that belongs to the Node object of the successor from the
	 * Node object that belongs to this Communication object.
	 */
	public String getSuccessorAddress() {
		return node.getSuccessor(null).getNetworkAddress();
	}
	
	/**
	 * 
	 * @return the NodeID object that belongs to the Node object that belongs to this 
	 * Communication object.
	 */
	public NodeID getNodeID() {
		return node.getIdentity().getNodeID();
	}
	
	/**
	 * 
	 * @return the Node object which belongs to this communication object.
	 */
	public Node getNode() {
		return node;
	}
	
	@Override
	public String getLocalIp() {
		return networkAddress;
	}
	
	@Override
	public void fireFingerChangeEvent(int eventType, FingerEntry node,FingerEntry finger) {
		network.fireFingerChangeEvent(eventType, node, finger);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				//Get message from the queue
				Message msg = queue.take();
				//handle it
				node.handleMessage(msg);
			} catch (InterruptedException e) {
				//We shall quit, so do we
				break;
			}
		}
	}

	@Override
	public void fireKeepAliveEvent(NodeID key, String networkAddress) {
		//Forward event
		network.fireKeepAliveEvent(key, networkAddress);
	}
	
	public int getMessageDelay() {
		return messageDelay;
	}

	@Override
	public void shutdown() {
		//The object will probably last longer than this function call!!!
		
		//Forward to node
		node.shutdown();
		
		//Interrupt thread
		this.interrupt();
	}
	
	public void kill() {
		//kill the node
		node.killMe();
		
		//interrupt the thread
		this.interrupt();
	}
}
