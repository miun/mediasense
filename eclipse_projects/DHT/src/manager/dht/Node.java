package manager.dht;

import java.util.Random;

import manager.Communication;
import manager.LookupServiceInterface;
import manager.Message;

public class Node implements LookupServiceInterface {
	private Communication communication;
	private String nodeID;
	private String address;

	public Node(String address) {
		this.address = address;
		//TODO there might be a better way for the generation of a rondom SHA key
		nodeID = SHA1Generator.SHA1(String.valueOf(new Random().nextInt()));
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
		// TODO Auto-generated method stub
		
	}
	
	public void joinNetwork(String contactAddress) {
		JoinMessage jm = new JoinMessage(address, contactAddress);
		communication.sendMessage(jm);
	}
}
