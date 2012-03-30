package manager;

import manager.dht.DistributedLookup;
import manager.dht.Node;
import manager.ui.Console;
import manager.ui.GUI;


public final class Manager {
	private static Manager instance;
	
	//Class for handling nodes at let them communicate with each other
	Communication communication;
	
	//UI classes
	Console console;
	GUI gui;
	
	//DHT instance
	DistributedLookup dht;
	
	public static void main(String[] args) {
		instance = new Manager();
		new GUI(instance);
	}
	
	public Manager getInstance() {
		return instance;
	}
	
	//Singleton class
	private Manager() {
		//Create communication object
		communication = new Communication();
		
		//Create UI classes
		console = new Console(this.getInstance());
		gui = new GUI(this.getInstance());
		while(true){
			System.out.println("test");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Create DHT instance
		//dht = new DistributedLookup();
		
	}
	
	public Node addNode() {
		return null;
	}
}
