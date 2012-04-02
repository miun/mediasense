package manager.dht;

import java.util.Random;

import manager.Communication;
import manager.CommunicationInterface;
import manager.LookupServiceInterface;
import manager.Message;

public class Node extends Thread implements LookupServiceInterface {
	private CommunicationInterface communication;
	private byte[] nodeID;
	private String address;
	
	private Node finger;

	public Node(String address, CommunicationInterface communication) {
		this.start();
		this.address = address;
		this.communication = communication;
		//TODO there might be a better way for the generation of a rondom SHA key
		//nodeID = SHA1Generator.SHA1(String.valueOf(new Random().nextInt()));
		//generate a Random byte Array as ID later SHA1Key ?!
		nodeID = new byte[1];
		new Random().nextBytes(nodeID);
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
				//answer with Joinresponse;
				Message answer = new JoinResponseMessage(address, message.fromIp);
				communication.sendMessage(answer);
				break;
			default: 
				//TODO Throw a Exception for a unsupported message?!
		}
		
	}
	
	public void joinNetwork(String contactAddress) {
		JoinMessage jm = new JoinMessage(address, contactAddress, nodeID);
		communication.sendMessage(jm);
	}
	
	@Override
	public void run() {
		while(true);
	}
}
