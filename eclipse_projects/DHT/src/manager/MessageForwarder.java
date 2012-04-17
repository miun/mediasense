package manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TimerTask;

import manager.dht.messages.broadcast.BroadcastMessage;
import manager.listener.NodeMessageListener;

public class MessageForwarder extends TimerTask {
	private Communication receiver;
	private Message message;
	private HashMap<Integer,Set<NodeMessageListener>> nodeMessageListener;
	private Set<NodeMessageListener> nodeMessageListenerAll;
	
	public MessageForwarder(Communication receiver, Message message, HashMap<Integer,Set<NodeMessageListener>> nodeMessageListener,Set<NodeMessageListener> nodeMessageListenerAll) {
		this.receiver = receiver;
		this.message = message;
		this.nodeMessageListener =  nodeMessageListener;
		this.nodeMessageListenerAll = nodeMessageListenerAll;
	}
	
	@Override
	public void run() {
		receiver.handleMessage(message);
		//Print to console that the Message has been forwarded
		int messageType = message.getType();
		
		//Check whether it is a Broadcast message
		if (messageType == Message.BROADCAST) {
			//Extract the broadcast message
			messageType = ((BroadcastMessage)message).extractMessage().getType();
		}
		
		//Inform nodes that listen to all messages
		for(NodeMessageListener nml: nodeMessageListenerAll) {
			nml.OnNodeMessage(new Date(),message);
		}
		
		//Inform all NodeMessageListeners listening to that type of message
		if(nodeMessageListener.containsKey(messageType)) {
			for(NodeMessageListener nml: nodeMessageListener.get(messageType)) {
				nml.OnNodeMessage(new Date(),message);
			}
		}
	}

}
