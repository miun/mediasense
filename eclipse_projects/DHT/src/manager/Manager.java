package manager;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import manager.dht.Node;
import manager.dht.SHA1Generator;
import manager.ui.GUI;
import manager.ui.console.Console;


public final class Manager {
	private static Manager instance;
	private static int adressCounter = 0;
	
	//Class for handling nodes at let them communicate with each other
	private Communication communication;
	
	//UI classes
	private Console console;
	private GUI gui;
	
	public static void main(String[] args) {
		instance = new Manager();
	}
	
	public Manager getInstance() {
		return instance;
	}
	
	public Communication getCommunication() {
		return communication;
	}
	
	//Singleton class
	private Manager() {
		//Set own instance
		instance = this;
		
		//Create communication object
		communication = new Communication();

		//Create UI classes
		console = new Console(this.getInstance());
		gui = new GUI(this.getInstance());

		//Add listener
		communication.addNodeMessageListener(console);
		
		console.run();
		System.out.println("Good bye!");
	}
	
	public void stopManager() {
		//Stop everything
		console.notifyExit();
	}
	
	public void addNode() {
		//Add node with the next address 
		communication.addNode(String.valueOf(adressCounter++), new Node());
	}
}
