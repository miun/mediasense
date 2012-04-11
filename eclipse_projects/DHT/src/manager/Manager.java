package manager;

import java.math.BigInteger;
import java.util.Random;

import manager.dht.Node;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;
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
	
	public static void main(String[] args) {
		new Manager();
	}
	
	public Manager getInstance() {
		return instance;
	}
	
	//Singleton class
	private Manager() {
		BigInteger bi1,bi2,bi_res;
		byte[] bytes1 = new byte[NodeID.ADDRESS_SIZE];
		byte[] bytes2 = new byte[NodeID.ADDRESS_SIZE];
		NodeID op1,op2,res;
		Random R = new Random();
		
		for(int i = 0; i < 100; i++) {
			//Add with NodeID's
			R.nextBytes(bytes1);
			op1 = new NodeID(bytes1);
			bi1 = new BigInteger(bytes1);
			
			R.nextBytes(bytes2);
			op2 = new NodeID(bytes2);
			bi2 = new BigInteger(bytes2);
			
			System.out.println(bi1.bitLength());
			System.out.println(bi2.bitLength());
			
			//Add with BigInteger's
			res = op1.add(op2);
			bi_res = bi1.add(bi2);
			System.out.println(bi_res.bitLength() + "\n");
			
			//Check result
			bytes1 = res.getID();
			bytes2 = bi_res.toByteArray();
			
			if(!bytes1.equals(bytes2)) {
				System.out.println("Calculation mistake!");
			}
		}

		//Set own class as instance
		instance = this;
		
		//Create objects for node communication
		network = Network.getInstance();

		//Create UI classes
		console = new Console(this.getInstance());
		
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
		
		//Give control to the network //Also starts the communication
		//TODO allow to create nodes with delayed starting capability
		network.addNode(comm,node);
		
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
	
	public void addNodeListener(NodeListener nl) {
		//forward to network
		network.addNodeListener(nl);
	}
	
	public void removeNodeListener(NodeListener nl) {
		network.removeNodeListener(nl);
	}
	
	public void showCircleGui() {
		new CircleGUI(this.getInstance());
	}
	
	public void addKeepAliveListener(KeepAliveListener listener) {
		//Forward
		network.addKeepAliveListener(listener);
	}
	
	public void removeKeepAliveListener(KeepAliveListener listener) {
		//Forward
		network.removeKeepAliveListener(listener);
	}
	
	public double calculateHealthOfDHT() {
		return network.calculateHealthOfDHT();
	}
}
