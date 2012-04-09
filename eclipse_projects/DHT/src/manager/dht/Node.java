package manager.dht;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import manager.CommunicationInterface;
import manager.LookupServiceInterface;
import manager.Message;
import manager.dht.messages.broadcast.BroadcastMessage;
import manager.dht.messages.broadcast.KeepAliveBroadcastMessage;
import manager.dht.messages.broadcast.NotifyJoinBroadcastMessage;
import manager.dht.messages.unicast.DuplicateNodeIdMessage;
import manager.dht.messages.unicast.JoinMessage;
import manager.dht.messages.unicast.JoinResponseMessage;
import manager.dht.messages.unicast.KeepAliveMessage;
import manager.dht.messages.unicast.NotifyJoinMessage;
import manager.dht.messages.unicast.NotifyLeaveMessage;
import manager.listener.FingerChangeListener;

public class Node extends Thread implements LookupServiceInterface {
	//Communication
	private CommunicationInterface communication;
	private String bootstrapAddress;

	//Own state in the DHT
	private TreeMap<FingerEntry,FingerEntry> finger;
	private FingerEntry identity;
	private FingerEntry successor;
	
	//Keep alive
	private static final int KEEP_ALIVE_PERIOD = 10000;
	private static final int KEEP_ALIVE_RANDOM_PERIOD = 10000;
	Timer keepAliveTimer = null;
	
	//Connection state
	private boolean bConnected = false;

	public Node(CommunicationInterface communication,String bootstrapAddress) {
		this.communication = communication;
		
		//Init fingertable
		finger = new TreeMap<FingerEntry, FingerEntry>();
		
		//Generate hash from the local network address
		//TODO ask stefan if inclusion of port address is reasonable
		byte[] hash = SHA1Generator.SHA1(communication.getLocalIp());

		//Set identity
		setIdentity(hash);
		
		//Save bootstrap address
		//No bootstrap means, WE are the beginning of the DHT
		//If we are a bootstrapping node, that means bootstrapping address is null or is our address,
		//we are always connected !!
		if(bootstrapAddress == null || bootstrapAddress.equals(communication.getLocalIp())) {
			//We are connected and we are our own successor
			bConnected = true;
			this.successor = identity;
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
		//Don't process message if it was not for us!!
		//TODO probably not necessary 
		//TODO check message for null ?!?!?!?!
		if(!message.toIp.equals(identity.getNetworkAddress())) return;
		
		//Safe performance for node
		//if(finger.))
		
		switch (message.type) {
			//react on a Join message
			case Message.JOIN:
				JoinMessage join_msg = (JoinMessage) message;
				Message answer = null;

				FingerEntry predecessor = getPredecessor(join_msg.getKey());
				//FingerEntry successor = getSuccessor(identity.getNodeID());
				
				//Forward or answer?
				if(predecessor.equals(identity)) {
					//It's us => reply on JOIN

					//Check if it exists
					FingerEntry newFingerEntry = new FingerEntry(new NodeID(join_msg.getKey().getID()),join_msg.getOriginatorAddress());
					FingerEntry tempFinger = finger.get(newFingerEntry);
					
					//If another node tried to enter the DHT with the same key, send duplicate message
					//Skip, if the same node tried again!
					if(tempFinger != null) {
						if(!tempFinger.getNetworkAddress().equals(newFingerEntry.getNetworkAddress())) {
							//Key not allowed message
							answer = new DuplicateNodeIdMessage(identity.getNetworkAddress(), join_msg.fromIp,join_msg.getKey());
						}
					}
					else {
						//Prepare answer
						answer = new JoinResponseMessage(identity.getNetworkAddress(), join_msg.getOriginatorAddress(),join_msg.getKey(), successor.getNetworkAddress(),successor.getNodeID(),identity.getNodeID());
						communication.sendMessage(answer);

						//Notify everybody of the new node
						sendBroadcast(new NotifyJoinBroadcastMessage(join_msg.getOriginatorAddress(),join_msg.getKey()),identity.getNodeID(),identity.getNodeID().sub(1));
						
						//Set successor to new node and update finger-table with old successor
						FingerEntry old_successor = successor;
						successor = newFingerEntry;
						updateFingerTableEntry(old_successor);
						//updateFingerTableEntry(newFingerEntry);

						//Repair finger count
						// checkFingerTable();
					}
				}
				else {
					//Forward to the best fitting predecessor
					message.fromIp = identity.getNetworkAddress();
					message.toIp = getPredecessor(join_msg.getKey()).getNetworkAddress();
					answer = message;
					communication.sendMessage(answer);
				}
				break;
			case Message.JOIN_RESPONSE:
				JoinResponseMessage jrm = (JoinResponseMessage) message;

				//Ignore JOIN_RESPONSE message if the node is already connected!
				if(!bConnected) {
					if(jrm.getJoinKey().equals(identity.getNodeID())) {
						//Add finger
						FingerEntry newFingerEntry = new FingerEntry(jrm.getSuccessor(), jrm.getSuccessorAddress());
						successor = newFingerEntry;
						bConnected = true;
						
						//Check
						updateFingerTableEntry(new FingerEntry(jrm.getPredecessor(),jrm.fromIp));
						
						//Create finger table the first time
						//buildFingerTable();
						//checkFingerTable();
					}
					else {
						//Ignore this because the key does not match!!!
						//TODO react on this
					}
				}
				
				break;
			case Message.DUPLICATE_NODE_ID:
				DuplicateNodeIdMessage dupMsg = (DuplicateNodeIdMessage)message;
				
				//If the node is not connected allow the change of the identity
				//Check the duplicate id also
				if(!bConnected && dupMsg.getDuplicateKey().equals(identity.getNodeID())) {
					//TODO what shall we do here?????
					assert(true);
				}

				break;
			case Message.BROADCAST:
				BroadcastMessage bcast_msg = (BroadcastMessage)message;
				
				//Forward broadcast
				bcast_msg.fromIp = identity.getNetworkAddress();
				sendBroadcast(bcast_msg,bcast_msg.getStartKey(),bcast_msg.getEndKey());
				
				//Process broadcast
				System.out.println(identity.getNetworkAddress() + "BB");
				handleMessage(bcast_msg.extractMessage());
				break;
			case Message.KEEPALIVE:
				KeepAliveMessage keep_alive_msg = (KeepAliveMessage)message;
				
				System.out.println(identity.getNetworkAddress() + "KA");
				
				//Reset timer
				resetKeepAliveTimer();

				//Handle keep-alive message
				updateFingerTableEntry(new FingerEntry(keep_alive_msg.getAdvertisedID(),keep_alive_msg.getAdvertisedNetworkAddress()));
				
				break;
			case Message.NODE_JOIN_NOTIFY:
				NotifyJoinMessage njm = (NotifyJoinMessage)message;
				
				//Check if this node can use the newly added node
				//for the finger table
				updateFingerTableEntry(new FingerEntry(njm.getHash(),njm.getNetworkAddress()));
					
				//Check finger table
				//checkFingerTable();
				
				break;
			case Message.NODE_LEAVE_NOTIFY:
				NotifyLeaveMessage nlm = (NotifyLeaveMessage)message;
				
				//Remove finger from finger table and exchange by successor
				removeFingerTableEntry(new FingerEntry(nlm.getHash(),nlm.getNetworkAddress()),new FingerEntry(nlm.getSuccessorHash(),nlm.getSuccessorNetworkAddress()));
				
			default:
				//TODO Throw a Exception for a unsupported message?!
		}
	}
	
	@Override
	public void run() {
		//Connect DHT node
		while(bConnected == false) {
			//Try to connect to DHT
			communication.sendMessage(new JoinMessage(identity.getNetworkAddress(),bootstrapAddress,identity.getNetworkAddress(),identity.getNodeID()));
			
			try {
				//Wait for connection and try again
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				//Exit thread
				break;
			}
		}
		
		//Connected => Set Keep alive timer
		resetKeepAliveTimer();
		
		//Wait for nothing
		while(true) {
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
	
	public FingerEntry getIdentity() {
		return identity;
	}
	
	private FingerEntry getPredecessor(NodeID nodeID) {
		//Add identity and successor to the fingertable - IMPORTANT: remove them before return
		finger.put(identity, identity);
		finger.put(successor, successor);
		
		FingerEntry hash = new FingerEntry(nodeID,null);
		FingerEntry result;
		
		//Find predecessor of a node
		result = finger.lowerKey(hash);
		if(result == null) {
			//There is no lower key in the finger tree
			result = finger.lastKey();
		}
		
		//Remove identity and successor from the fingertable
		finger.remove(identity);
		finger.remove(successor);
		
		return result;
	}
	
	public FingerEntry getSuccessor(NodeID nodeID) {
		//Add identity and successor to the fingertable - IMPORTANT: remove them before return
		finger.put(identity, identity);
		finger.put(successor, successor);
		
		FingerEntry hash = new FingerEntry(nodeID,null);
		FingerEntry result;
		
		//Get successor of us
		result = finger.higherKey(hash);
		if(result == null) { 
			//There is no higher key in the finger tree
			result = finger.firstKey();
		}
		
		//Remove identity and successor from the finger-table
		finger.remove(identity);
		finger.remove(successor);
		
		return result;
	}
	
	private void setIdentity(byte[] hash) {
		//Set identity
		identity = new FingerEntry(new NodeID(hash),communication.getLocalIp());
		
		//(Re-)initialize finger table
		//Always add ourselves to the finger table
		//finger = new TreeMap<FingerEntry,FingerEntry>();
		//finger.put(identity,identity);
	}
	
	//Check if new node can be inserted into finger table
	public void updateFingerTableEntry(FingerEntry newFinger) {
		FingerEntry suc;
		NodeID hash_finger;
		NodeID hash_suc;
		NodeID hash_log2;
		int log2floor;
		
		//Check for dont's
		if(newFinger.equals(identity)) return;
		if(newFinger.equals(successor)) return;
		if(finger.containsKey(newFinger)) return;
		
		//1 - Rotate hash to the "origin"
		//2 - Then get the logarithm of base 2, rounded up
		//3 - Calculate the new hash
		
		hash_finger = newFinger.getNodeID().sub(identity.getNodeID());
		log2floor = NodeID.logTwoFloor(hash_finger);
		hash_log2 = NodeID.powerOfTwo(log2floor);

		//Get previous successor - Shift to original position first
		suc = getSuccessor(hash_log2.add(identity.getNodeID()));
		hash_suc = suc.getNodeID().sub(identity.getNodeID());
		
		if(suc.equals(identity)) {
			//In this case, there is no successor => just add the new finger
			finger.put(newFinger,newFinger);
			fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD, identity.getNodeID(), newFinger.getNodeID());
		}
		//Check if the new finger is smaller than the successor
		else if(hash_finger.compareTo(hash_suc) < 0) {
			//Also add the new node in this case...
			finger.put(newFinger,newFinger);
			fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_ADD, identity.getNodeID(), newFinger.getNodeID());
			
			//...but also check if the successor was the old successor
			//and, if so, remove it
			//Old successor means, that it is between [log2floor,log2floor + 1)
			if(log2floor == ((NodeID.ADDRESS_SIZE * 8) - 1) || hash_suc.compareTo(NodeID.powerOfTwo(log2floor + 1)) < 0) {
				finger.remove(suc);
				fireFingerChangeEvent(FingerChangeListener.FINGER_CHANGE_REMOVE, identity.getNodeID(), suc.getNodeID());
			}
		}
	}
	
	public void removeFingerTableEntry(FingerEntry remove,FingerEntry suc) {
		//TODO create this :-)
	}
	
	public void buildFingerTable() {
		//TODO and this too
	}
	
	//TODO figure out where and how to use
	private void checkFingerTable() {
	}
	
	private void sendBroadcast(BroadcastMessage bcast_msg, NodeID startKey,NodeID endKey) {
		FingerEntry suc,next;
				
		//Dont do...
		if(!bConnected) return;
		
		//Prepare packet
		bcast_msg.fromIp = identity.getNetworkAddress();

		//Get first successor
		suc = getSuccessor(identity.getNodeID());
		if(suc.equals(identity)) return;
		next = getSuccessor(suc.getNodeID());

		//For each finger
		do {
			//Check and set range
			if(suc.getNodeID().between(startKey,endKey)) {
				bcast_msg.setStartKey(suc.getNodeID());
				
				//Set endKey to next NodeID or the end of the range, whatever is smallest
				if(next.getNodeID().between(startKey,endKey)) {
					bcast_msg.setEndKey(next.getNodeID().sub(1));
				}
				else {
					bcast_msg.setEndKey(endKey);
				}
				
				//Send message
				bcast_msg.toIp = suc.getNetworkAddress();
				communication.sendMessage(bcast_msg);
			}

			//Move to next range
			suc = next;
			next = getSuccessor(suc.getNodeID());
		} while(!suc.equals(identity));
	}
	
	//TODO for DEBUG
	private void fireFingerChangeEvent(int eventType,NodeID node,NodeID finger) {
		communication.fireFingerChangeEvent(eventType,node,finger);
	}
	
	//TODO for DEBUG
	//Remove later
	public TreeMap<FingerEntry,FingerEntry> getFingerTable() {
		return finger;
	}
	
	private void triggerKeepAliveTimer() {
		KeepAliveBroadcastMessage msg;
		
		//Send broadcast
		msg = new KeepAliveBroadcastMessage(identity.getNodeID(),identity.getNetworkAddress());
		sendBroadcast(msg, identity.getNodeID(),identity.getNodeID().sub(1));
		
		//Reset time
		resetKeepAliveTimer();
	}

	private void resetKeepAliveTimer() {
		int time = KEEP_ALIVE_PERIOD + new Random().nextInt(KEEP_ALIVE_RANDOM_PERIOD);
		System.out.println(identity.getNetworkAddress() + " - " + time);
		
		//Cancel and reschedule timer
		if(keepAliveTimer != null) keepAliveTimer.cancel();
		keepAliveTimer = new Timer();
		keepAliveTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				//Trigger keep alive
				triggerKeepAliveTimer();
			}
		}, time);
	}
}
