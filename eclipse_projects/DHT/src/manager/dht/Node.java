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
	private FingerEntry finger;

	public Node(CommunicationInterface communication,String bootstrapAddress) {
		this.communication = communication;

		//TODO there might be a better way for the generation of a random SHA key
		//nodeID = SHA1Generator.SHA1(String.valueOf(new Random().nextInt()));
		//generate a Random byte Array as ID later SHA1Key ?!
		byte[] rb = new byte[NodeID.ADDRESS_SIZE];
		new Random().nextBytes(rb);
		identity = new FingerEntry(new NodeID(rb),communication.getLocalIp());
		
		//Initialize finger table
		//finger = new TreeSet<FingerEntry>();
		
		//Save bootstrap address
		if(bootstrapAddress == null) {
			//No bootstrap means, WE are the DHT
			//finger.add(identity);
			finger = identity;
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
				JoinMessage jm = (JoinMessage) message;
				Message answer=null;
				if(finger.equals(identity) || (jm.key.compareTo(finger.getNodeID())<0)){
					//extra case for startphase - If Iam my only finger
					answer = new JoinResponseMessage(finger.getNetworkAddress(), jm.fromIp, finger.getNodeID());
					this.finger = new FingerEntry(jm.key, jm.fromIp);
				}
				else if(jm.key.compareTo(finger.getNodeID())>0) {
					//if the new node is bigger than my finger
					jm.toIp = finger.getNetworkAddress();
					answer = jm;
				} else {
					answer = new KeyNotAllowedMessage(identity.getNetworkAddress(), jm.fromIp, jm.key);
				}
				
				/*
				JoinMessage join_msg = (JoinMessage) message;
				FingerEntry successor = findSuccessor(new FingerEntry(new NodeID(join_msg.key.getID()),message.fromIp));
				Message answer;
				
				//Forward or answer?
				if(successor.equals(identity)) {
					//Its us => reply on JOIN
					answer = new JoinResponseMessage(successor.getNetworkAddress(),message.fromIp,successor.getNodeID());
				}
				else {
					//Forward to successor
					message.toIp = successor.getNetworkAddress();
					answer = message;
				}
				*/
				//Send message
				communication.sendMessage(answer);
				break;
			case Message.JOIN_RESPONSE:
				JoinResponseMessage jrm = (JoinResponseMessage) message;
				this.finger = new FingerEntry(jrm.getNodeID(), jrm.fromIp);
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
	/*
	private FingerEntry findSuccessor(FingerEntry fingerEntry) {
		//in the first place we are the successor
		FingerEntry successor;// = identity;
		NodeID dist,current_dist;
		FingerEntry f1,f2;
		
		f1 = finger.higher(identity);
		f2 = finger.higher(fingerEntry);
		
		
		
/*		for(FingerEntry current: finger) {
			current_dist = fingerEntry.getNodeID().distanceTo(current); 
			if(current_dist.compareTo(dist) > ) { 
				//New nearer finger found
				dist = current_dist;
				successor = current;
			}
		}
		
		successor = finger.higher(fingerEntry);
		if(fingerEntry)*/
		/*
		
		return null;
	}*/
}
