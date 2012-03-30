package manager.ui.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import manager.Communication;
import manager.Manager;

public class Console {
	private Manager manager;
	private Communication com;
	
	public Console(Manager manager) {
		//Set objects
		this.manager = manager;
		com = manager.getCommunication();
	}
		
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String in;
		Command cmd;

		while(true) {
			//Read line
			try {
				in = reader.readLine();
			}
			catch (Exception e) {
				System.out.println("ERROR - " + e.getMessage());
				break;
			}
			
			//Analyse input
			if((cmd = extractCmd(in)) == null) {
				System.out.println("Invalid command!");
			}
			else {
				if(cmd.cmd.toLowerCase().equals("exit")) {
					//Exit
					break;
				}
				else if(cmd.cmd.toLowerCase().equals("node_add")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_del")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_info")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("exit")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("exit")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("exit")) {
					
				}
			}
		}
	}
	
	private Command extractCmd(String str) {
		return null;
	}
	
	public void notifyExit() {
		//TODO try to exit the loop and exit application
	}
}
