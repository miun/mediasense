package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import manager.listener.NodeMessageListener;

/**
 * The communication simulates the communication layer between the nodes. To test our implementation of the DHT on its own this class
 * has been built. 
 * @author florianrueter
 *
 */
public class Communication implements CommunicationInterface{
	private List<NodeMessageListener> nodemessagelisteners;
	private Network network;
	
	public Communication(Network network, List<NodeMessageListener> nodemessagelistener) {
		this.network = network;
		nodemessagelisteners = nodemessagelistener;
	}
	
	/**
	 * Search the right client and trigger the handle event
	 * @param m message to the client
	 */
	public void sendMessage(Message m) {
		//Get the receiver of the message
		network.sendMessage(m);
		//Inform all NodeMessageListeners about the message
		//TODO maybe only if receiver is reachable? Or sign the message?!
		for(NodeMessageListener nml: nodemessagelisteners)
			nml.OnNodeMessage(m);
	}
	
	//Add listener for node messages
	//TODO parameter for message filtering ?!?!!?!?
	public void addNodeMessageListener(NodeMessageListener listener) {
		nodemessagelisteners.add(listener);
	}
	
	//Remove listener for node messages
	public void removeNodeMessageListener(NodeMessageListener listener) {
		nodemessagelisteners.remove(listener);
	}
}
