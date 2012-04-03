package manager.dht;

import java.util.Random;

import manager.CommunicationInterface;
import manager.LookupServiceInterface;
import manager.Message;

public class Node extends Thread implements LookupServiceInterface {
	private CommunicationInterface communication;
	private byte[] nodeID;
	
	private String bootstrapAddress;
	private Node finger = null;

	private boolean connected = false;

	public Node(CommunicationInterface communication,String bootstrapAddress) {
		this.communication = communication;

		//TODO there might be a better way for the generation of a random SHA key
		//nodeID = SHA1Generator.SHA1(String.valueOf(new Random().nextInt()));
		//generate a Random byte Array as ID later SHA1Key ?!
		nodeID = new byte[1];
		new Random().nextBytes(nodeID);
		
		//Save bootstrap address
		this.bootstrapAddress = bootstrapAddress;
		
		//Start thread
		this.start();
	}

	@Override
	public void resolve(String uci) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void register(String uci) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.type) {
			//react on a Join message
			case Message.JOIN:
				//answer with Join response;
				Message answer = new JoinResponseMessage(communication.getLocalIp(), message.fromIp);
				communication.sendMessage(answer);
				break;
			case Message.JOIN_RESPONSE:
				//Yeeha
				connected = true;
				break;
			default: 
				//TODO Throw a Exception for a unsupported message?!
		}
		
	}
	
	public void joinNetwork(String contactAddress) {
		JoinMessage jm = new JoinMessage(communication.getLocalIp(), contactAddress, nodeID);
		communication.sendMessage(jm);
	}
	
	@Override
	public void run() {
		while(finger==null) {
		//while(!connected) {
			//Try to connect to DHT#
			communication.sendMessage(new JoinMessage(communication.getLocalIp(),bootstrapAddress,nodeID));
			
			try {
				//Wait for connection and try again
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				//Exit thread
				break;
			}
		}
		
/*		while(true) {
			//TODO Do what ever... Check TTL
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				//Exit requested!
				//Probably shutdown things
				break;
			}
		}*/
	}
}
