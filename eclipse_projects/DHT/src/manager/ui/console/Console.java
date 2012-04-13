package manager.ui.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
			
			//Get,log, extract line
			if(in == null) break;
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
						for(String bsa: cmd.param) {
							manager.addNode(bsa);
						}
					}
					
				}
				else if(cmd.cmd.toLowerCase().equals("node_del")) {
					
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
							types = new String[]{"!join","!join_ack","!join_busy","!join_response","!duplicate","!predecessor","!predecessor_response","!keepalive","!keepalive_response","!notify_join","!notify_leave"};
						}
						else if(cmd.param[0].equals("all")) {
							//add all
							types = new String[]{"join","join_ack","join_busy","join_response","duplicate","predecessor","predecessor_response","keepalive","keepalive_response","notify_join","notify_leave"};
						}
						else if(cmd.param[0].equals("!broadcast")) {
							types = new String[]{"!keepalive","!keepalive_response","!notify_join","!notify_leave"};
						}
						else if(cmd.param[0].equals("broadcast")) {
							types = new String[]{"keepalive","keepalive_response","notify_join","notify_leave"};
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
					if(cmd.param == null || cmd.param.length > 1) throw new InvalidParamAmountException();
					
					//Wait
					long wait = Long.parseLong(cmd.param[0]);
					if(wait > 0) {
						System.out.println("Wait for " + wait + " ms...");
						
						try {
							Thread.sleep(wait);
						}
						catch (InterruptedException e) {
							System.out.println("Sleep interrupted by exception!");
							break;
						}

						System.out.println("Wait done");
					}
					
				}
				else if(cmd.cmd.toLowerCase().equals("stat")) {
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
}
