package se.miun.mediasense.addinlayer;

import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;

public interface AddIn {
	
	/**
	 * This will load and initialize the specific add in
	 * 
	 * @param platform The parent MediaSense platform
	 */
	public void loadAddIn(MediaSensePlatform platform);
	
	/**
	 * This will start the specific add in. Should be called after the loadAddIn().
	 */
	public void startAddIn();
	
	/**
	 * This will stop the specific add in. Should be called before the unloadAddIn().
	 */
	public void stopAddIn();
	
	/**
	 * This will unload the specific add in, removing all trace from it.
	 */
	public void unloadAddIn();

	/**
	 * This function is called by the add-in manager when the communication finds a message which it does know about.
	 * It is the extensions task to handle the message, if it was intended for that specific add in.
	 *  
	 * @param message the message to be handled
	 */
	public void handleMessage(Message message);
}
