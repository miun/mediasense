package manager.ui.console;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import manager.Manager;
import manager.Message;
import manager.dht.FingerEntry;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeMessageListener;
import manager.ui.gfx.CircleGUI;
import manager.ui.gfx.NodeInfo;

public class Console implements NodeMessageListener,FingerChangeListener,KeepAliveListener {
	private Manager manager;
	
	//Watch switches
	private boolean watchKeepAlive = false;
	private boolean watchFingerChange = false;
	
	public Console(Manager manager) {
		//Set objects
		this.manager = manager;
	}
	
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String in;
		
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
			
			//Quit then
			if(in == null) break;
			
			//Handle command
			if(handleCommand(in) == false) break;
		}
	}
	
	private boolean handleCommand(String in) {
		Command cmd;
		
		//Get,log, extract line
		if(in == null) return false;
		cmd = extractCmd(in);
		
		//Analyse input
		try {
			if(cmd.cmd.toLowerCase().equals("exit")) {
				//Exit
				return false;
			}
			else if(cmd.cmd.toLowerCase().equals("node_add")) {
				if(cmd.param == null) {
					manager.addNode(null);
				} 
				else {
					manager.addNode(cmd.param[0]);
				}
			}
			else if(cmd.cmd.toLowerCase().equals("node_add_n")) {
				if(cmd.param == null || cmd.param.length > 1) {
					throw new InvalidParamAmountException();
				} 
				else {
					try {
						int count = Integer.parseInt(cmd.param[0]);
						manager.addNodeN(count);
					}
					catch(NumberFormatException e) {
						System.out.println("Invalid number!");
					}
				}
			}
			else if(cmd.cmd.toLowerCase().equals("node_kill")) {
				if(cmd.param == null) {
					throw new InvalidParamAmountException();
				} 
				else {
					//Kill all specified nodes
					for(int i = 1; i < cmd.param.length; i++) {
						manager.killNode(cmd.param[i]);
					}
				}
			}
			else if(cmd.cmd.toLowerCase().equals("node_kill_n")) {
				if(cmd.param == null || cmd.param.length > 1) {
					throw new InvalidParamAmountException();
				} 
				else {
					try {
						//Kill specified amount of nodes
						int count = Integer.parseInt(cmd.param[0]);
						manager.killNodeN(count);
					}
					catch(NumberFormatException e) {
						System.out.println("Invalid number!");
					}
				}
			}
			else if(cmd.cmd.toLowerCase().equals("node_remove")) {
				if(cmd.param == null) {
					throw new InvalidParamAmountException();
				}
				else {
					for(int i = 1; i < cmd.param.length; i++) {
						manager.removeNode(cmd.param[i]);
					}
				}
			}
			else if(cmd.cmd.toLowerCase().equals("node_remove_n")) {
				if(cmd.param == null || cmd.param.length > 1) {
					throw new InvalidParamAmountException();
				} 
				else {
					try {
						//Kill specified amount of nodes
						int count = Integer.parseInt(cmd.param[0]);
						manager.removeNodeN(count);
					}
					catch(NumberFormatException e) {
						System.out.println("Invalid number!");
					}
				}
			}
			else if(cmd.cmd.toLowerCase().equals("g")) {
				if(cmd.param == null) {
					new CircleGUI(manager,0);
				}
				else {
					try {
						int radius = Integer.valueOf(cmd.param[0]);
						new CircleGUI(manager, radius);
					} catch(NumberFormatException e){
						new CircleGUI(manager,0);
					}					
				}
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
				for(String node: cmd.param) {
					new NodeInfo(node, manager);
				}
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
			else if(cmd.cmd.toLowerCase().equals("finger_watch")) {
				//Listen to finger changes
				watchFingerChange = !watchFingerChange;
				
				if(watchFingerChange) {
					manager.addFingerChangeListener(this);
				}
				else {
					manager.removeFingerChangeListener(this);
				}
				
				System.out.println("Watching FINGER-CHANGE: " + (watchFingerChange ? "ON" : "OFF"));
			}
			else if(cmd.cmd.toLowerCase().equals("finger")) {
				//List all fingers
				if(cmd.param == null || cmd.param.length > 1) throw new InvalidParamAmountException();
				System.out.println(manager.showFinger(cmd.param[0]));
			}
			else if(cmd.cmd.toLowerCase().equals("msg_watch")) {
				if(cmd.param == null) throw new InvalidParamAmountException();
				String[] types = null;
				
				//Check if it is only one parameter, then might be ! or all
				if(cmd.param.length == 1){
					//Only one parameter, check for ! or all
					if(cmd.param[0].equals("!")) {
						//remove all
						types = new String[]{"!join","!join_ack","!join_busy","!join_response","!duplicate","!predecessor","!predecessor_response","!keepalive","!keepalive_response","!notify_join","!notify_leave","!join_finalize","!find_successor","!find_successor_response","!find_precedessor","!find_predecessor_response","!node_suspicious"};
					}
					else if(cmd.param[0].equals("all")) {
						//add all
						types = new String[]{"join","join_ack","join_busy","join_response","duplicate","predecessor","predecessor_response","keepalive","keepalive_response","notify_join","notify_leave","join_finalize","find_successor","find_successor_response","find_precedessor","find_predecessor_response","node_suspicious"};
					}
					else if(cmd.param[0].equals("!broadcast")) {
						types = new String[]{"!keepalive","!keepalive_response","!notify_join","!notify_leave","!node_suspicious"};
					}
					else if(cmd.param[0].equals("broadcast")) {
						types = new String[]{"keepalive","keepalive_response","notify_join","notify_leave","node_suspicious"};
					}
					else {
						//Just forward all parameters as they are
						types = cmd.param;
					}
				}
				else {
					//Just forward all parameters as they are
					types = cmd.param;
				}
				
				StringBuffer answer = new StringBuffer("Applied following msg_watch: ");
				//Handle each parameter
				for(String type: types) {
					boolean remove = false;
					//Check if add or remove
					if(type.charAt(0)=='!') {
						//this is a remove
						remove = true;
						type = type.substring(1).toLowerCase();
					}else type = type.toLowerCase();
					
					int msgType = -1;
					//Get the Message Type
					if(type.equals("join")) {
						msgType = Message.JOIN;
					} else if(type.equals("join_response")) {
						msgType = Message.JOIN_RESPONSE;
					} else if(type.equals("join_ack")) {
						msgType = Message.JOIN_ACK;
					} else if(type.equals("join_busy")) {
						msgType = Message.JOIN_BUSY;
					} else if(type.equals("duplicate")) {
						msgType = Message.DUPLICATE_NODE_ID;
					} else if(type.equals("predecessor")) {
						msgType = Message.FIND_PREDECESSOR;
					} else if(type.equals("predecessor_response")) {
						msgType = Message.FIND_PREDECESSOR_RESPONSE;
					} else if(type.equals("keepalive")) {
						msgType = Message.KEEPALIVE;
					} else if(type.equals("keepalive_response")) {
						msgType = Message.KEEPALIVE_RESPONSE;
					} else if(type.equals("notify_join")) {
						msgType = Message.NODE_JOIN_NOTIFY;
					} else if(type.equals("notify_leave")) {
						msgType = Message.NODE_LEAVE_NOTIFY;
					} else if(type.equals("join_finalize")) {
						msgType = Message.JOIN_FINALIZE;
					} else if(type.equals("find_predecessor")) {
						msgType = Message.FIND_PREDECESSOR;
					} else if(type.equals("find_predecessor_response")) {
						msgType = Message.FIND_PREDECESSOR_RESPONSE;
					} else if(type.equals("check_predecessor")) {
						msgType = Message.CHECK_PREDECESSOR;
					} else if(type.equals("check_predecessor_response")) {
						msgType = Message.CHECK_PREDECESSOR_RESPONSE;
					} else if(type.equals("node_suspicious")) {
						msgType = Message.NODE_SUSPICIOUS;
					}
					
					//Call the function for every valid message type
					if(msgType >= 0) {
						if(remove) {
							manager.removeNodeMessageListener(msgType, this);
							answer.append("!"+type+",");
						}else {
							manager.addNodeMessageListener(msgType, this);
							answer.append(type+",");
						}
					}
				}
				System.out.println(answer.toString());
			}
			else if(cmd.cmd.toLowerCase().equals("ka_watch")) {
				//Check parameters
				if(cmd.param != null) throw new InvalidParamAmountException();
				
				//Toggle keep-alive watching
				watchKeepAlive = !watchKeepAlive;
				
				if(watchKeepAlive) {
					manager.addKeepAliveListener(this);
				}
				else {
					manager.removeKeepAliveListener(this);
				}
				
				//Print status
				System.out.println("Watching KEEP-ALIVE: " + (watchKeepAlive ? "ON" : "OFF"));
			}
			else if(cmd.cmd.toLowerCase().equals("health")) {
				//Print health
				if(cmd.param != null && cmd.param[0].toLowerCase().equals("m")) {
					System.out.println("DHT health: " + manager.calculateHealthOfDHT(true) * 100.0 + "%");
				}
				else {
					System.out.println("DHT health: " + manager.calculateHealthOfDHT(false) * 100.0 + "%");
				}
			}
			else if(cmd.cmd.toLowerCase().equals("wait")) {
				//Wait for the specified time in ms
				if(cmd.param == null || cmd.param.length > 2) throw new InvalidParamAmountException();
				
				long wait,wait_rand;
				
				//Wait
				try {
					wait = Long.parseLong(cmd.param[0]);
					wait_rand = cmd.param.length > 1 ? Long.parseLong(cmd.param[1]) : 0;
				}
				catch (NumberFormatException e) {
					System.out.println("Invalid number!");
					return true;
				}
				
				if(wait > 0) {
					if(wait_rand > 0) {
						System.out.println("Wait between " + wait + " and " + (wait + wait_rand) + " ms...");
						
						try {
							Thread.sleep(wait + (new Random()).nextInt((int)wait_rand));
						}
						catch (InterruptedException e) {
							System.out.println("Sleep interrupted by exception!");
							return false;
						}

						System.out.println("Wait done");
					}
					else {
						System.out.println("Wait for " + wait + " ms...");
						
						try {
							Thread.sleep(wait);
						}
						catch (InterruptedException e) {
							System.out.println("Sleep interrupted by exception!");
							return false;
						}
	
						System.out.println("Wait done");
					}
				}
			}
			else if(cmd.cmd.toLowerCase().equals("statistic")) {
				String filename = "";
				
				if(cmd.param == null) {
					//Stop statistic
					manager.stopStatistic();
				}
				else {
					//Combine params to filename, as it can contain ","
					for(String s: cmd.param) filename += s;

					//Start new one
					manager.startStatistic(filename);
				}
			}
			else if(cmd.cmd.toLowerCase().equals("exec")) {
				String filename = "";
				
				if(cmd.param == null) {
					//No filename specified
					throw new InvalidParamAmountException();
				}
				else {
					//Combine params to filename, as it can contain ","
					for(String s: cmd.param) filename += s;

					//Exec script
					execScript(filename);
				}
			}
			else if(cmd.cmd.toLowerCase().equals("break")) {
				if(cmd.param == null || cmd.param.length > 1) {
					//No filename specified
					throw new InvalidParamAmountException();
				}
				else {
					//Stop node for inspection
					//Works only in debug mode
					manager.breakNode(cmd.param[0]);
				}
			}
			else if(!cmd.cmd.equals("")) { 
				System.out.println("Invalid command!");
			}
		} catch (InvalidParamAmountException e) {
			//Show error msg
			System.out.println("Invalid amount of parameters!");
		}
		
		//TRUE means, do not exit
		return true;
	}
	
	private Command extractCmd(String str) {
		//Split cmd from params
		String[] line =  str.split(" ",2);
		String[] params = new String[0];
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
		if(temp.size() > 0) {
			params = temp.toArray(params);
		}
		else {
			params = null;
		}
		
		return new Command(cmd,params);
	}
	
	public void notifyExit() {
		//TODO try to exit the loop and exit application
	}

	@Override
	public void OnNodeMessage(Date timeStamp,Message msg) {
		System.out.println(new SimpleDateFormat().format(timeStamp) + " | "  + msg.toString());
	}

	@Override
	public void OnFingerChange(int changeType, FingerEntry node, FingerEntry finger) {
		String result;
		
		//Which action???
		if(changeType == FingerChangeListener.FINGER_CHANGE_ADD) {
			result = "ADD-NEW finger: ";
		}
		else if(changeType == FingerChangeListener.FINGER_CHANGE_REMOVE) {
			result = "REMOVE-OLD finger: ";
		}
		else if(changeType == FingerChangeListener.FINGER_CHANGE_ADD_BETTER) {
			result = "ADD-BETTER finger: ";
		}
		else if(changeType == FingerChangeListener.FINGER_CHANGE_REMOVE_WORSE) {
			result = "REMOVE-WORSE finger: ";
		}
		else {
			result = "UNKNOWN CHANGE TYPE! ";
		}
		
		result = result + finger.toString() + " @NODE: " + node.toString() + "";
		System.out.println(result);
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key,String networkAddress) {
		System.out.println(new SimpleDateFormat().format(date) + " | Node: " + key.toString() + " Addr: " + networkAddress + " initiated KEEP-ALIVE");
	}
	
	private void execScript(String filename) {
		FileInputStream fstream;
		BufferedReader reader;
		String line = null;
		
		List<String> lines = new ArrayList<String>();
		HashMap<String,Integer> gotos = new HashMap<String,Integer>();

		try {
			//Open file
			fstream = new FileInputStream(filename);
			reader = new BufferedReader(new InputStreamReader(fstream));
			
			//GO!
			System.out.println("Loading script file " + filename);

			//Execute each line
			while((line = reader.readLine()) != null) {
				//Remove trailing spaces etc.
				line = line.trim();
				
				//Ignore comments
				if(line.length() > 0 && !line.substring(0,1).equals("#")) {
					if(line.substring(0,1).equals(":")) {
						//Goto mark
						gotos.put(line.substring(1,line.length()).trim().toLowerCase(), lines.size() - 1);
					}
					else {
						//Normal script line
						lines.add(line.trim());
					}
				}
			}
			
			//Close file
			reader.close();
			fstream.close();
		}
		catch (IOException e) {
			System.out.println("Cannot load script " + filename);
			return;
		}
		
		//Execute
		System.out.println("Executing script file...");
		for(int i = 0; i < lines.size(); i++) {
			//Get current line
			line = lines.get(i);
			
			//Special handling of goto lines
			if(line.length() >= 4 && line.substring(0,4).toLowerCase().equals("goto")) {
				//Get line link
				String link = line.substring(4,line.length()).trim().toLowerCase();
				Integer line_number = gotos.get(link);
				
				if(line_number == null) {
					System.out.println("Invalid goto reference " + link + "! Ignoring...");
				}
				else {
					System.out.println("GOTO line " + line_number.toString());
					i = line_number < 0 ? 0 : line_number;
				}
			}
			else {
				//Handle normal command
				System.out.println(line);
				if(handleCommand(line) == false) break;
			}
		}
		
		System.out.println(" --- script done ---");
	}
}
