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
	private boolean bConnected = false;

	public Node(CommunicationInterface communication,String bootstrapAddress) {
		this.communication = communication;
						
		//TODO there might be a better way for the generation of a random SHA key
		//nodeID = SHA1Generator.SHA1(String.valueOf(new Random().nextInt()));
		//generate a Random byte Array as ID later SHA1Key ?!
		byte[] rb = new byte[NodeID.ADDRESS_SIZE];
		new Random().nextBytes(rb);
		identity = new FingerEntry(new NodeID(rb),communication.getLocalIp());
		
		//Initialize finger table
		//Always add ourselves to the finger table
		finger = new TreeSet<FingerEntry>();
		finger.add(identity);
		
		//Save bootstrap address
		//No bootstrap means, WE are the beginning of the DHT
		//If we are a bootstrapping node, that means bootstrapping address is null or is our address,
		//we are always connected !!
		if(bootstrapAddress == null || bootstrapAddress.equals(communication.getLocalIp())) {
			bConnected = true;
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
				JoinMessage join_msg = (JoinMessage) message;
				FingerEntry successor = findSuccessor(new NodeID(join_msg.key.getID()));
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
		while(bConnected == false) {
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
	
	public FingerEntry getDirectSuccessor() {
		FingerEntry successor = null;
		
		successor = finger.higher(identity);
		
		if(successor == null) {
			successor = finger.higher(FingerEntry.MIN_POS_FINGER);
		}
		
		return successor;
	}
	
	private FingerEntry findSuccessor(NodeID nodeID) {
		FingerEntry successor;
		
		//Find responsible node
		successor = finger.lower(new FingerEntry(nodeID,null));
		if(successor == null) successor = finger.lower(FingerEntry.MAX_POS_FINGER);
		return successor;
	}
}
