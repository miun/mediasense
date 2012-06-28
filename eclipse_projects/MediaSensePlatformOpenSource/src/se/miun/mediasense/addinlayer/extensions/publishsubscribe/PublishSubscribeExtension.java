package se.miun.mediasense.addinlayer.extensions.publishsubscribe;


import java.util.HashMap;
import java.util.Vector;

import se.miun.mediasense.addinlayer.extensions.Extension;
import se.miun.mediasense.disseminationlayer.communication.AbstractCommunication;
import se.miun.mediasense.disseminationlayer.communication.DestinationNotReachableException;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;

public class PublishSubscribeExtension implements Extension {

	
	SubscriptionEventListener subscriptionEventListener = null;	
	MediaSensePlatform platform = null;
	
	private MultiMap subscriptions = new MultiMap();
		
	@Override
	public void loadAddIn(MediaSensePlatform platform) {
		this.platform = platform;
		
	}

	@Override
	public void startAddIn() {
		// No extra stuff to start		
	}

	@Override
	public void stopAddIn() {
		// No extra stuff to stop		
		
	}

	@Override
	public void unloadAddIn() {
		// Nothing to unload
		
	}
	
	@Override
	public void handleMessage(Message message) {
		
		switch (message.getType()) {

		case Message.STARTSUBSCRIBE:			
			//Start the subscription in the addIn
			StartSubscribeMessage startSubMessage = (StartSubscribeMessage) message;				
			subscriptions.put(startSubMessage.uci, startSubMessage.getFromIp());					
			break;
		
		case Message.ENDSUBSCRIBE:
			//End the subscription
			EndSubscribeMessage endSubMessage = (EndSubscribeMessage) message;						
	    	subscriptions.remove(endSubMessage.uci, endSubMessage.getFromIp());
			break;
			
		case Message.NOTIFYSUBSCRIBERS:
			//Call the listener!
			NotifySubscribersMessage notifydSubMessage = (NotifySubscribersMessage) message;
			subscriptionEventListener.subscriptionEvent(notifydSubMessage.uci, notifydSubMessage.value);
			break;

		default:
			//Unknown message (Or not for me)
			//Do Nothing			
		}
	}
	
	
	
	/**
	 * This will start a subscription for a specific UCI. 
	 * Given that the other end point is also running the publish/subscribe interface.
	 *  
	 * @param uci the UCI to be subscribed to
	 * @param ip the IP end point which handles the UCI, which has previously been resolved. 
	 */
	public void startSubscription(String uci, String ip){
				
		//Send out the startSubscribe Message
		AbstractCommunication communication = platform.getDisseminationCore().getCommunicationInterface();		
		StartSubscribeMessage message = new StartSubscribeMessage(uci, ip, communication.getLocalIp());

		try {
			communication.sendMessage(message);
		}
		catch(DestinationNotReachableException e) {
			//TODO handle if you like
		}
	}
	
	/**
	 * This will end the subscription for a specific UCI.
	 * All notify messages from that UCI should now be stopped.
	 * 
	 * @param uci the UCI which is not longer wanted
	 * @param ip the IP end point which handles the UCI, which has previously been resolved. 
	 */
	public void endSubscription(String uci, String ip){

		//Send out the endSubscribe Message
		AbstractCommunication communication = platform.getDisseminationCore().getCommunicationInterface();		
		EndSubscribeMessage message = new EndSubscribeMessage(uci, ip, communication.getLocalIp());

		try {
			communication.sendMessage(message);
		}
		catch(DestinationNotReachableException e) {
			//TODO handle if you like
		}
	}
	
	/**
	 * This is called to notify all subscribers of a new value.
	 * Should be called when a value is updated.
	 * 
	 * @param uci the UCI that was just updated
	 * @param value the new value, which will be sent to all subscribers
	 */
	public void notifySubscribers(String uci, String value){
		
		//Attend to the subscriptions
		String[] subsriberIp = subscriptions.get(uci);
		
		for(int i = 0; i != subsriberIp.length; i++){				
			AbstractCommunication communication = platform.getDisseminationCore().getCommunicationInterface();					
			NotifySubscribersMessage message = new NotifySubscribersMessage(uci, value, subsriberIp[i], communication.getLocalIp());

			try {
				communication.sendMessage(message);						
			}
			catch(DestinationNotReachableException e) {
				//TODO handle if you like
			}
		}
		
	}
	
	/**
	 * Sets the SubscriptionEventListener 
	 * @param listener SubscriptionEventListener
	 */
	public void setSubscriptionEventListener(SubscriptionEventListener listener){
		this.subscriptionEventListener = listener;		
	}
	/**
     * Used to call the listener and create a callback
     * 
     * @param uci the UCI which is being received
     * @param value the new value which the UCI has
     */
	public void callSubscriptionEventListener(String uci, String value){
		if(subscriptionEventListener != null){
			subscriptionEventListener.subscriptionEvent(uci, value);		
		}
	}

}


//MultiMap for handling the Subscriptions
class MultiMap{
	
	HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
	
	public void put(String key, String value){		
		Vector<String> v = map.get(key);		
		if(v == null){
			v = new Vector<String>();
		}
		v.add(value);
		map.put(key, v);
	}
	
	public String[] get(String key){		
		Vector<String> v = map.get(key);		
		if(v == null){
			return new String[0];
		} else {
			return v.toArray(new String[0]);
		}
	}
	
	public void remove(String key, String value){			
		Vector<String> v = map.get(key);		
		v.remove(value);
		map.put(key, v);
	}
	
}


