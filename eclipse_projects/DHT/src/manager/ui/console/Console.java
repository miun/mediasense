package manager.ui.console;

import manager.Communication;
import manager.Manager;

public class Console {
	private Manager manager;
	private Communication com;
	
	ConsoleRunnable consoleRunnable;
	Thread consoleThread;
	
	public Console(Manager manager) {
		//Set objects
		this.manager = manager;
		com = manager.getCommunication();
		
		//Start thread
		consoleRunnable = new ConsoleRunnable();
		consoleThread = new Thread(consoleRunnable);
		consoleThread.start();
	}
	
	public void stop() {
		try {
			consoleRunnable.notifyStop();
			consoleThread.wait();
		}
		catch (Exception e) {
			
		}
	}
}
