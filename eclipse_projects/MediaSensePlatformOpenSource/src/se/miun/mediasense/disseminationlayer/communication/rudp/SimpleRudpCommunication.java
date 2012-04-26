package se.miun.mediasense.disseminationlayer.communication.rudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import se.miun.mediasense.addinlayer.AddInManager;
import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.communication.serializer.EnterSeparatedMessageSerializer;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;

public class SimpleRudpCommunication extends Thread implements CommunicationInterface {

	// This is the Simple Reliable UDP implementation!
	// Send & Ack, no sequence!

	// Hardcoded Settings/////////////
	private int overlayPort = 9009;
	private int numResends = 10;
	// Hardcoded Settings/////////////

	ArrayList<RudpMessageContainer> sendMessageList = new ArrayList<RudpMessageContainer>();

	private DatagramSocket sendSocket = null;
	private DatagramSocket recieveSocket = null;

	private DisseminationCore disseminationCore = null;

	private EnterSeparatedMessageSerializer messageSerializer = new EnterSeparatedMessageSerializer();

	private boolean runCommuncation = true;

	private ReSender reSender;

	// private MessageHandler messageHandler = null;

	public SimpleRudpCommunication(DisseminationCore disseminationCore) {
		try {
			this.disseminationCore = disseminationCore;

			// Init the Socket
			sendSocket = new DatagramSocket();
			recieveSocket = new DatagramSocket(overlayPort);

			// Start the ReSender
			reSender = new ReSender();
			reSender.start();

			// Start the packet listener thread
			this.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		// Handle incoming packets

		while (runCommuncation) {
			try {

				// Start receiving incoming packets
				byte[] buf = new byte[1024];
				final DatagramPacket packet = new DatagramPacket(buf,
						buf.length);
				recieveSocket.receive(packet);

				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						handleIncomingPacket(packet);
					}

				});
				t.start();

			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	private void handleIncomingPacket(DatagramPacket packet) {
		try {
			
			//Make it drop a few packets just to test! :D

			/*
			Random r = new Random(System.currentTimeMillis());
			if(r.nextBoolean()){
				System.out.println("oops, dropped a packet...");
				return;				
			}
			*/

			
			// Get the data
			String stringPacket = new String(packet.getData());
			
			//Read the seqNr
			String[] split = stringPacket.split(";");
						
			int seqNr = -1;
			seqNr= Integer.parseInt(split[0]);
			
			// Parse the rest of the packet into a Message
			Message message = messageSerializer.deserializeMessage(split[1]);
				
			if (seqNr != 0){			
				
				//Send back the Ack message!				
				AcknowledgementMessage ack = new AcknowledgementMessage(seqNr + "", message.getFromIp(), getLocalIp());				
				RudpMessageContainer msg = new RudpMessageContainer(ack);
				msg.seqNr = 0; //To not get acks on the acks!				
				sendRudpMessage(msg);		
				
				// Handle the message content as usual				
				switch (message.getType()) {
				
				case Message.GET:				
					//Fire off the getEvent!
					GetMessage getMessage = (GetMessage) message;
					disseminationCore.callGetEventListener(getMessage.getFromIp(), getMessage.uci);				
					break;
											
				case Message.SET:				
					//Fire off the SetEvent!
					SetMessage setMessage = (SetMessage) message;
					disseminationCore.callSetEventListener(setMessage.uci, setMessage.value);				
					break;


				case Message.NOTIFY:
					//Fire off the getResponseEvent!
					NotifyMessage notifyMessage = (NotifyMessage) message;
					disseminationCore.callGetResponseListener(notifyMessage.uci, notifyMessage.value);
					break;
					
				default:
					
					//This forwards any unknown messages to the lookupService
					disseminationCore.getLookupServiceInterface().handleMessage(message);
					
					//This forwards any unknown messages to the AddInManager and the addIns					
					AddInManager addInManager = disseminationCore.getMediaSensePlatform().getAddInManager();
					addInManager.forwardMessageToAddIns(message);		

					
					break;
				}
				
			}
			else {
				
				AcknowledgementMessage ackMsg = (AcknowledgementMessage) message;
				handleAck(ackMsg);
			
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(Message message) {


		RudpMessageContainer queueMessage = new RudpMessageContainer(message);

		// Put it into queue
		sendMessageList.add(queueMessage);

		// But also send it directly!
		sendRudpMessage(queueMessage);
	}
	

	private void sendRudpMessage(RudpMessageContainer queuedMessage) {
		try {

			//Serialize
			String data = messageSerializer.serializeMessage(queuedMessage.message);
			
			//Add the seqNr 
			data = queuedMessage.seqNr + ";" + data;
			
			//Send!
			DatagramPacket packet = new DatagramPacket(data.getBytes(),	data.length(), InetAddress.getByName(queuedMessage.message.getToIp()), overlayPort);
			sendSocket.send(packet);

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private void handleAck(AcknowledgementMessage ackMessage) {

		//Sync to ensure mutex
		synchronized (sendMessageList) {
			// Iterate the list
			for (int i = 0; i < sendMessageList.size(); i++) {
				RudpMessageContainer queuedMesssage = sendMessageList.get(i);

				// Find the right seqnr
				if (ackMessage.seqNr == queuedMesssage.seqNr) {

					// Remove from queue
					sendMessageList.remove(queuedMesssage);
					return;
				}
			}			
		}
	}

	@Override
	public void shutdown() {
		sendSocket.close();
		recieveSocket.close();
		runCommuncation = false;
	}

	@Override
	public String getLocalIp() {
		try {			
			//Workaround because Linux is stupid...		    	
	    	Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
	    	
	    	while (ni.hasMoreElements()) {
	    		NetworkInterface networkInterface = ni.nextElement();
	    			    		
	    		Enumeration<InetAddress> ias = networkInterface.getInetAddresses();
		    	while (ias.hasMoreElements()) {
		    		 InetAddress address = ias.nextElement();
		    		 
		    		 if(!address.isLoopbackAddress()){
		        		return address.getHostAddress();
		        	}		    		 
		    	}	    		
	    	}  

	    	//In windows it is this simple...
			return InetAddress.getLocalHost().getHostAddress();
			
		} catch (Exception e) {
			return "127.0.0.1";
		}
	}

	// /////////RESENDER CLASS///////////////////
	private class ReSender extends Thread {
		@Override
		public void run() {

			// Run
			while (runCommuncation) {
				try {

					//Sync to ensure mutex
					synchronized (sendMessageList) {
						// For each queued message
						for (int i = 0; i < sendMessageList.size(); i++) {
							RudpMessageContainer msg = sendMessageList.get(i);

							// Try to resend after 1000ms time
							if ((System.currentTimeMillis() - msg.life) > 1000) {

								sendRudpMessage(msg);

								msg.numResends++;
								msg.life = System.currentTimeMillis();
							}

							// Remove if taken to many resends
							if (msg.numResends > numResends) {
								sendMessageList.remove(msg);								
							}
						}
					}
					// Sleep or whatever
					Thread.sleep(250);

				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}
	// /////////RESENDER CLASS///////////////////

}
