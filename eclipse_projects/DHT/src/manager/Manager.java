package manager;

import manager.dht.Node;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;
import manager.listener.FingerChangeListener;
import manager.listener.NodeMessageListener;
import manager.ui.CircleGUI;
import manager.ui.console.Console;


public final class Manager {
	private static Manager instance;
	
	//Classes for handling nodes at let them communicate with each other
	private Network network;
	
	//UI classes
	private Console console;
	
	//To create nodes in ascending order
	private int newNodeCounter = -1;
	
	private CircleGUI circle;
	
	public static void main(String[] args) {
		new Manager();
	}
	
	public Manager getInstance() {
		return instance;
	}
	
	//Singleton class
	private Manager() {
		circle = new CircleGUI();
		
		//Set own class as instance
		instance = this;
		
		//Create objects for node communication
		network = Network.getInstance();

		//Create UI classes
		console = new Console(this.getInstance());
		//gui = new GUI(this.getInstance());
		
		console.run();
		System.out.println("Good bye!");
	}
	
	public void stopManager() {
		//Stop everything
		console.notifyExit();
	}
	
	public Node createNode(String networkAddress) {
		return null;
	}
	
	public int addNode() {
		//No bootstrapping!
		return addNode(null);
	}
	
	public int addNode(String bootstrapAddress) {
		Communication comm;
		Node node;
		
		//Add node with communication interface adopted from MediaSense
		comm = new Communication(network,new Integer(++newNodeCounter).toString());
		node = new Node(comm,bootstrapAddress);
		
		circle.addPoint(SHA1Generator.SHA1(new Integer(newNodeCounter).toString()));
		//Give control to the network
		network.addNode(comm);
		
		//Start
		//TODO allow to create nodes with delayed starting capability
		comm.start(node);
		return newNodeCounter;
	}
	
	public boolean setMessageDelay(int delay,String networkAddress) {
		//Forward to network
		return network.setMessageDelay(delay, networkAddress);
	}
	
	public String showNodeInfo() {
		//Forward to network
		return network.showNodeInfo();
	}
	
	public String showNodeInfo(String networkAddress) {
		//Forward to network
		return network.showNodeInfo(networkAddress);
	}
	
	public String showCircle(String startAddress) {
		//Forward to network
		return network.showCircle(startAddress);
	}
	
	public void addNodeMessageListener(int msgType, NodeMessageListener listener) {
		//Forward to network
		network.addNodeMessageListener(msgType, listener);
	}
	
	public void removeNodeMessageListener(int msgType, NodeMessageListener listener) {
		//Forward to network
		network.removeNodeMessageListener(msgType, listener);
	}

	public void addFingerChangeListener(FingerChangeListener listener) {
		//Forward listener
		network.addFingerChangeListener(listener);
	}
	
	public void removeFingerChangeListener(FingerChangeListener listener) {
		//Forward listener
		network.removeFingerChangeListener(listener);
	}
	
	public String showFinger(String nodeAddress) {
		return network.showFinger(nodeAddress);
	}
}
