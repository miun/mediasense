package manager;

import manager.ui.GUI;
import manager.ui.console.Console;


public final class Manager {
	private static Manager instance;
	
	//Class for handling nodes at let them communicate with each other
	Communication communication;
	
	//UI classes
	Console console;
	GUI gui;
	
	public static void main(String[] args) {
		instance = new Manager();
		new GUI(instance);
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
}
