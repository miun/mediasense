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
	
	public MessageForwarder(Communication receiver, Message message, HashMap<Integer,Set<NodeMessageListener>> nodeMessageListener) {
		this.receiver = receiver;
		this.message = message;
		this.nodeMessageListener =  nodeMessageListener;
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
		
		//Inform all NodeMessageListeners listening to that type of message
		if(nodeMessageListener.containsKey(messageType)) {
			for(NodeMessageListener nml: nodeMessageListener.get(messageType)) {
				nml.OnNodeMessage(new Date(),message);
			}
		}
	}

}
