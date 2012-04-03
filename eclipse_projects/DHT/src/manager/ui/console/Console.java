package manager.ui.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import manager.Manager;
import manager.Message;
import manager.Network;
import manager.listener.NodeMessageListener;

public class Console implements NodeMessageListener {
	private Manager manager;
	
	public Console(Manager manager) {
		//Set objects
		this.manager = manager;
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
					if(cmd.param == null) {
						manager.addNode();
					} 
					else {
						manager.addNode(cmd.param[0]);
					}
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_del")) {
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_info")) {
					if(cmd.param == null || cmd.param.length > 1) throw new InvalidParamAmountException();
					System.out.println(manager.showNode(cmd.param[0]));
				}
				else if(cmd.cmd.toLowerCase().equals("node_watch")) {
					//Add node to watcher
					if(cmd.param == null) throw new InvalidParamAmountException();
					//communication.addNodeMessageListener(this);
				}
				else if(cmd.cmd.toLowerCase().equals("msg_delay")) {
					//Set delay for message
					if(cmd.param == null || cmd.param.length > 2) throw new InvalidParamAmountException();
					
					boolean result;
					
					if(cmd.param.length == 1) {
						result = manager.setMessageDelay(Integer.parseInt(cmd.param[0]),null);
					}
					else {
						result = manager.setMessageDelay(Integer.parseInt(cmd.param[0]),cmd.param[1]);
					}
					
					//Print result
					System.out.println("setMessageDelay: " + (result ? "SUCCESSFUL" : "FAILED"));
					
				}
				else if(cmd.cmd.toLowerCase().equals("circle")) {
					if(cmd.param == null || cmd.param.length > 1) throw new InvalidParamAmountException();
					System.out.println(manager.showCircle(cmd.param[0]));
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
	public void OnNodeMessage(Date timeStamp,Message msg) {
		System.out.println(new SimpleDateFormat().format(timeStamp) + " | "  + msg.toString());
	}
}
