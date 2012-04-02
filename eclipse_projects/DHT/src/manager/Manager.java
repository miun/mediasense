package manager;

import java.util.ArrayList;
import java.util.List;

import manager.dht.Node;
import manager.listener.NodeMessageListener;
import manager.ui.GUI;
import manager.ui.console.Console;


public final class Manager {
	private static Manager instance;
	private static int addressCounter = 0;
	
	//Classes for handling nodes at let them communicate with each other
	private List<Communication> communications;
	private List<NodeMessageListener> nodeMessageListeners; //This listeners listen to all messages
	private Network network;
	
	//UI classes
	private Console console;
	private GUI gui;
	
	public static void main(String[] args) {
		instance = new Manager();
	}
	
	public Manager getInstance() {
		return instance;
	}
	
	//Singleton class
	private Manager() {
		//Set own instance
		instance = this;
		
		//Create objects for node communication
		communications = new ArrayList<Communication>();
		nodeMessageListeners = new ArrayList<NodeMessageListener>();
		network = new Network();

		//Create UI classes
		console = new Console(this.getInstance());
		gui = new GUI(this.getInstance());

		//Add listener that is listening to all messages
		this.addNodeMessageListener(console);
		
		console.run();
		System.out.println("Good bye!");
	}
	
	public void stopManager() {
		//Stop everything
		console.notifyExit();
	}
	
	public void addNode() {
		//Create a communicatoin object for the node (With all listeners)
		Communication nodesCommunicator = new Communication(network, nodeMessageListeners);
		//Make a node Object
		Node joining = new Node(String.valueOf(addressCounter), nodesCommunicator);
		//Add node with the next address
		network.addNode(String.valueOf(addressCounter), joining);
		//Not very good but simple and good enough for the beginning
		//Join the network
		if(addressCounter>0)
			joining.joinNetwork("0");
		addressCounter++;
	}
	
	/**
	 * Add a listener that is listening to all me
	 * @param listener
	 */
	public void addNodeMessageListener(NodeMessageListener listener) {
		this.nodeMessageListeners.add(listener);
		for(Communication com: communications)
			com.addNodeMessageListener(listener);
	}
}
