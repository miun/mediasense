package se.miun.mediasense.disseminationlayer.communication.rudp;

import java.net.InetSocketAddress;

import se.miun.mediasense.addinlayer.AddInManager;
import se.miun.mediasense.disseminationlayer.communication.AbstractCommunication;
import se.miun.mediasense.disseminationlayer.communication.DestinationNotReachableException;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.communication.MessageSerializer;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.RUDPSocket;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.datagram.RUDPDatagram;
import se.miun.mediasense.disseminationlayer.communication.rudp.socket.exceptions.RUDPDestinationNotReachableException;
import se.miun.mediasense.disseminationlayer.communication.serializer.BinaryMessageSerializer;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;

public class RUDPCommunication extends AbstractCommunication implements Runnable {

	private static final int communicationPort = 9009;

	private DisseminationCore disseminationCore = null;
	private MessageSerializer messageSerializer = new BinaryMessageSerializer();
	
	private RUDPSocket socket;
	
	private boolean runCommunication = true;
	
	public RUDPCommunication(DisseminationCore disseminationCore) {		
		try {
			this.disseminationCore = disseminationCore;
			this.socket = new RUDPSocket(communicationPort);
			
			//Start the Listener!
			Thread t = new Thread(this);
			t.start();
		} catch (Exception e){
			e.printStackTrace();			
		}		
	}
	
	@Override
	public void shutdown() {
		runCommunication = false;
		socket.shutdown();					
	}
	

	@Override
	public void sendMessage(Message message) throws DestinationNotReachableException {
		InetSocketAddress address = new InetSocketAddress(message.getToIp(),communicationPort);
		RUDPDatagram dgram;
		
		//Prepare datagram
		byte[] data = messageSerializer.serializeMessage(message);
		dgram  = new RUDPDatagram(address,data);

		//Send
		try {
			socket.send(dgram);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		RUDPDatagram dgram;
		
		while (runCommunication) {
            try {
            	//Receive and handle data
            	dgram = socket.receive();
            	handleData(dgram);
            } catch (RUDPDestinationNotReachableException e) {
            	System.out.println(e.getMessage());
            	
            	//Rehabilitate socket and try again
            	socket.rehabilitateLink(e.getInetSocketAddress());
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
        }				
	}
	
	private void handleData(RUDPDatagram dgram) {
		try {
			Message message = messageSerializer.deserializeMessage(dgram.getData(),dgram.getSocketAddress().getAddress().getHostAddress(),getLocalIp());

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
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
