package manager;

import manager.ui.GUI;
import manager.ui.console.Console;


public final class Manager {
	private static Manager instance;
	private boolean bRun = true;
	
	//Class for handling nodes at let them communicate with each other
	Communication communication;
	
	//UI classes
	Console console;
	GUI gui;
	
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
		
		try {
			Thread.sleep(5000);
		}
		catch(Exception e) {
			
		}

		stopManager();
		
		while(bRun) {
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {
				
			}
		}

		System.out.println("Good bye!");
	}
	
	public void stopManager() {
		//Stop everything
		console.stop();
		
		//Finally stop ourselves
		bRun = false;
	}
}
