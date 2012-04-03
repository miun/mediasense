package manager.dht;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import manager.CommunicationInterface;
import manager.LookupServiceInterface;
import manager.Message;

public class Node extends Thread implements LookupServiceInterface {
	private CommunicationInterface communication;
	private FingerEntry identity;
	
	private String bootstrapAddress;

	private TreeSet<FingerEntry> finger;

	public Node(CommunicationInterface communication,String bootstrapAddress) {
		this.communication = communication;

		//TODO there might be a better way for the generation of a random SHA key
		//nodeID = SHA1Generator.SHA1(String.valueOf(new Random().nextInt()));
		//generate a Random byte Array as ID later SHA1Key ?!
		byte[] rb = new byte[NodeID.ADDRESS_SIZE];
		new Random().nextBytes(rb);
		identity = new FingerEntry(new NodeID(rb),communication.getLocalIp());
		
		//Initialize finger table
		finger = new TreeSet<FingerEntry>();
		
		//Save bootstrap address
		if(bootstrapAddress == null) {
			//No bootstrap means, WE are the DHT
			finger.add(identity);
		}
		else {
			this.bootstrapAddress = bootstrapAddress;
		}
		
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
				
				break;
			case Message.JOIN_RESPONSE:
				JoinResponseMessage jrm = (JoinResponseMessage) message;
				//this.finger = new FingerEntry(jrm.getNodeID(), jrm.fromIp);
				break;
			case Message.KEYNOTALLOWED:
				byte[] rb = new byte[NodeID.ADDRESS_SIZE];
				new Random().nextBytes(rb);
				identity = new FingerEntry(new NodeID(rb),communication.getLocalIp());
				break;
			default:
				//TODO Throw a Exception for a unsupported message?!
		}
		
	}
	
	@Override
	public void run() {
		//while(finger.isEmpty()) {
		while(finger == null) {
			//Try to connect to DHT#
			communication.sendMessage(new JoinMessage(communication.getLocalIp(),bootstrapAddress,identity.getNodeID()));
			
			try {
				//Wait for connection and try again
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				//Exit thread
				break;
			}
		}
	}
	
	public NodeID getIdentity() {
		return identity.getNodeID();
	}
	
	public FingerEntry getFinger() {
		return finger;
	}
}
