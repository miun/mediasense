package manager;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;

import manager.dht.FingerEntry;
import manager.dht.Node;
import manager.dht.Sensor;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;
import manager.listener.NodeMessageListener;
import manager.ui.console.Console;
import manager.ui.log.Log;

public final class Manager {
	private static final String LOG_FILE = "../../../../media_sense.log";
	
	private static Manager instance;
	
	//Classes for handling nodes, and letting them communicate with each other
	private Network network;
	
	//Logging facility
	private Log log;
	
	//Statistic object
	private Statistic statistic = null;
	
	//UI classes
	private Console console;
	
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
		System.exit(0);
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
	
	//Add node function
	public String addNode(String bootstrap) {
		String address = Network.createSequentialAddress();
		if(bootstrap == null) bootstrap = network.getRandomClientAddress(true);
		Network.getInstance().addNode(address,bootstrap);
		return address;
	}
	
	public void removeNode(String networkAddress) {
		//Forward to network
		network.removeNode(networkAddress);
	}
	
	public void killNode(String networkAddress) {
		//Forward to network
		network.killNode(networkAddress);
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
	
	public void addNodeMessageListener(Integer msgType, NodeMessageListener listener) {
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
	
	//Add multiple nodes at once
	public void addNodeN(int count) {
		//Add count nodes
		for(int i = 0; i < count; i++) {
			addNode(network.getRandomClientAddress(true));
		}
	}
	
	public void killNodeN(int count) {
		String address;
		
		for(int i = 0; i < count; i++) {
			address = network.getRandomClientAddress(false);
			if(address != null) network.killNode(address);
		}
	}

	public void removeNodeN(int count) {
		String address;
		
		for(int i = 0; i < count; i++) {
			address = network.getRandomClientAddress(false);
			if(address != null) network.removeNode(address);
		}
	}
	
	public void breakNode(String address) {
		network.breakNode(address);
	}
	
	public void register(String node, String uci) {
		network.register(node,uci);
	}
	
	public void resolve(String node, String uci) {
		network.resolve(node,uci);
	}
	
	public Map<Sensor,FingerEntry> showSensors(String networkAddress) {
		return network.showSensors(networkAddress);
	}
}
