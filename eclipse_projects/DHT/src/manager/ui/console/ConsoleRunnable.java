package manager.ui.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import manager.Manager;

public class ConsoleRunnable implements Runnable {
	private boolean bRun = true;
	private Manager manager;
	private BufferedReader reader;
	
	public ConsoleRunnable(Manager manager) {
		this.manager = manager;
	}
	
	@Override
	public void run() {
		String in;
		
		//Set up console reader
		reader = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			//Read line
			try {
				in = reader.readLine();
			}
			catch (Exception e) {
				break;
			}
			
			//Analyse input
			if(in.toLowerCase().equals("exit")) break;
		}
		
		//Exit manager
		manager.stopManager();
	}
	
	public void notifyStop() {
		bRun = false;

		try {
			System.in.close();
		}
		catch (Exception e) {
			System.out.println("bla");
		}

	}
}
