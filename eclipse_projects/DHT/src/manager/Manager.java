package manager;

import manager.dht.Node;
import manager.listener.NodeMessageListener;
import manager.ui.GUI;
import manager.ui.console.Console;


public final class Manager {
	private static Manager instance;
	
	//Classes for handling nodes at let them communicate with each other
	private Network network;
	
	//UI classes
	private Console console;
	private GUI gui;
	
	//To create nodes in ascending order
	private int newNodeCounter = -1;
	
	public static void main(String[] args) {
		new Manager();
	}
	
	public Manager getInstance() {
		return instance;
	}
	
	//Singleton class
	private Manager() {
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
	
	public void addNode() {
		//No bootstrapping!
		addNode(null);
	}
	
	public void addNode(String bootstrapAddress) {
		Communication comm;
		Node node;
		
		//Add node with communication interface adopted from MediaSense
		comm = new Communication(network,new Integer(++newNodeCounter).toString());
		node = new Node(comm,bootstrapAddress);

		//Give control to the network
		network.addNode(comm);
		
		//Start
		//TODO allow to create nodes with delayed starting capability
		comm.start(node);
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
}
