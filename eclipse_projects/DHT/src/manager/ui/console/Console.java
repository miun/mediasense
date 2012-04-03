package manager.ui.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import manager.Manager;
import manager.Message;
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
					if(cmd.param == null) {
						System.out.println(manager.showNodeInfo());
					}
					else {
						for(String p: cmd.param) {
							System.out.println(manager.showNodeInfo(p));
						}
					}
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
		String[] params = null;
		String cmd = line[0].trim();
		
		//No parameters
		if(line.length < 2)
			return new Command(cmd,null);

		//Split the parameters and drop empty ones
		ArrayList<String> temp = new ArrayList<String>(Arrays.asList(line[1].split(",")));
		for(int i = 0; i < temp.size(); i++) {
			if(temp.get(i).trim().length() == 0) {
				temp.remove(i);
			}
			else {
				temp.set(i, temp.get(i).trim()); 
			}
		}

		//Return list if it contains elements
		if(temp.size() > 0) params = temp.toArray(params);
		return new Command(cmd,params);
	}
	
	public void notifyExit() {
		//TODO try to exit the loop and exit application
	}

	@Override
	public void OnNodeMessage(Date timeStamp,Message msg) {
		System.out.println(new SimpleDateFormat().format(timeStamp) + " | "  + msg.toString());
	}
}
