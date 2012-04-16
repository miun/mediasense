package manager;

import java.io.IOException;

import manager.dht.Node;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;
import manager.listener.NodeMessageListener;
import manager.ui.console.Console;
import manager.ui.log.Log;


public final class Manager {
	private static final String LOG_FILE = "/home/timo/media_sense.log";
	
	private static Manager instance;
	
	//Classes for handling nodes, and letting them communicate with each other
	private Network network;
	
	//Logging facility
	Log log;
	
	//Statistic object
	Statistic statistic = null;
	
	//UI classes
	private Console console;
	
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
		
		//Create log file
		try {
			log = new Log(getInstance(),LOG_FILE);
		}
		catch (IOException e) {
			System.out.println("Cannot open log file " + e.getMessage());
			log = null;
		}

		//Create UI classes
		console = new Console(this.getInstance());
		console.run();
		
		//the famous last words...
		System.out.println("May the hash be with you!");
	}
	
	public void stopManager() {
		//Stop everything
		if(log != null) log.close();
		stopStatistic();
		
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
		
		//Give control to the network //Also starts the communication
		//TODO allow to create nodes with delayed starting capability
		network.addNode(comm,node);
		
		return newNodeCounter;
	}
	
	public boolean setMessageDelay(int delay,String networkAddress) {
		//Forward to network
		return network.setMessageDelay(delay, networkAddress);
	}
	
	public int getMessageDelay(String networkAddress) {
		//Forward to network
		return network.getMessageDelay(networkAddress);
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
	
	public void addNodeListener(NodeListener nl) {
		//forward to network
		network.addNodeListener(nl);
	}
	
	public void removeNodeListener(NodeListener nl) {
		network.removeNodeListener(nl);
	}
	
	public void addKeepAliveListener(KeepAliveListener listener) {
		//Forward
		network.addKeepAliveListener(listener);
	}
	
	public void removeKeepAliveListener(KeepAliveListener listener) {
		//Forward
		network.removeKeepAliveListener(listener);
	}
	
	public double calculateHealthOfDHT(boolean listMissingFinger) {
		return network.calculateHealthOfDHT(listMissingFinger);
	}
	
	public void startStatistic(String filename) {
		//Always stop before
		stopStatistic();
		
		//Start new one
		try {
			statistic = new Statistic(getInstance(),filename,Statistic.TRIGGER_SECOND);
			statistic.start();
		}
		catch (IOException e) {
			// :-(
			System.out.println("ERROR starting statistic " + e.getMessage());
			statistic = null;
		}
	}
	
	public void stopStatistic() {
		//Stop statistic if there is one
		if(statistic != null) {
			statistic.stop();
			statistic = null;
		}
	}
}
