package manager.ui.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import manager.Communication;
import manager.Manager;
import manager.Message;
import manager.listener.NodeMessageListener;

public class Console implements NodeMessageListener {
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
				System.out.print("# ");
				in = reader.readLine();
			}
			catch (Exception e) {
				System.out.println("ERROR - " + e.getMessage());
				break;
			}
			
			//Get line
			cmd = extractCmd(in);
			
			//Analyse input
			try {
				if(cmd.cmd.toLowerCase().equals("exit")) {
					//Exit
					break;
				}
				else if(cmd.cmd.toLowerCase().equals("node_add")) {
					manager.addNode();
				}
				else if(cmd.cmd.toLowerCase().equals("node_del")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_info")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_watch")) {
					//Add node to watcher
					if(cmd.param.length != 1) throw new InvalidParamAmountException();
					com.addNodeMessageListener(this);
				}
				else if(cmd.cmd.toLowerCase().equals("exit")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("exit")) {
					
				}
				else if(!cmd.cmd.equals("")) { 
					System.out.println("Invalid command!");
				}
			} catch (InvalidParamAmountException e) {
				//Show error msg
				System.out.println("Invalid amount of parameters!");
			}
		}
	}
	
	private Command extractCmd(String str) {
		//Split cmd from params
		String[] line =  str.split(" ",2);
		String cmd = line [0];
		
		if (line.length<2)
			return new Command(cmd,null);

		//Split the params
		String[] param = line[1].split(",");
		return new Command(cmd,param);
		
	}
	
	public void notifyExit() {
		//TODO try to exit the loop and exit application
	}

	@Override
	public void OnNodeMessage(Message msg) {
		System.out.println(msg.toString());
	}
}
