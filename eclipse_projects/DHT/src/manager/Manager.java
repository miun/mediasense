package manager;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import manager.dht.Node;
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
	
	//Timer for Random Events
	private Timer timer;
	
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
		
		//Create Timer object
		timer = new Timer();

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
	
	//Add node functions
	public String addNode() {
		String newAddress = Network.createSequentialAddress();
		Network.getInstance().addNode(newAddress,null);
		return newAddress;
	}
	
	public String addNode(String address) {
		Network.getInstance().addNode(address,null);
		return address;
	}
	
	public String addNode(String address,String bootstrap) {
		Network.getInstance().addNode(address, bootstrap);
		return address;
	}
	
	public void removeNode(String networkAddress) {
		//Forward to network
		network.removeNode(networkAddress);
	}
	
	public boolean killNode(String networkAddress) {
		//Forward to network
		return network.killNode(networkAddress);
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
	
	
	/**
	 * Kills random clients in a random time until there are minClients or less
	 * @param minClients
	 */
	public void startRandomKill(int minClients) {
		if(network.getNumberOfClients() > minClients) {
			timer.schedule(new RandomKillTimerTask(minClients), new Random().nextInt(10000));
			killNode(null);
		}
	}
	
	public void startRandomAdd(int maxClients) {
		if(network.getNumberOfClients() < maxClients) {
			timer.schedule(new RandomAddTimerTask(maxClients), new Random().nextInt(10000));
			addNode(null);
		}
	}
	
	private class RandomKillTimerTask extends TimerTask {
		private int minClients;
		
		public RandomKillTimerTask(int minClients) {
			this.minClients = minClients;
		}
		
		@Override
		public void run() {
			startRandomKill(minClients);
		}
	}
	
	private class RandomAddTimerTask extends TimerTask {
		private int maxClients;
		
		public RandomAddTimerTask(int maxClients) {
			this.maxClients = maxClients;
		}
		
		@Override
		public void run() {
			startRandomAdd(maxClients);
		}
	}
}
