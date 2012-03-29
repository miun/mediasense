package se.miun.mediasense.disseminationlayer.lookupservice.distributed;

import java.util.HashMap;

import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;

public class DistributedLookup extends Thread implements LookupServiceInterface{

	
	String bootstrapIp = "127.0.0.1";
	
	DisseminationCore disseminationCore = null;
	CommunicationInterface communication = null;
	
	
	public Node node = new Node("127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1", "127.0.0.1");
	
	//Lookup table
    public static HashMap<String, String> hashTable = new HashMap<String, String>();
	
	
	boolean runLookUpService = true;
	boolean connected = false;
	

	public DistributedLookup(DisseminationCore disseminationCore, CommunicationInterface communication) {
		//Used when the node is the bootstrap
		
		bootstrapIp = communication.getLocalIp();
		
		node = new Node(bootstrapIp, bootstrapIp, bootstrapIp, bootstrapIp, bootstrapIp, bootstrapIp, bootstrapIp);

		this.disseminationCore = disseminationCore;
		this.communication = communication;		
		
		//Start the Thread!
		this.start();		
	}
	
	public DistributedLookup(DisseminationCore disseminationCore, CommunicationInterface communication, String bootstrapIp) {
		//Used when there is a normal node connecting
		
		this.bootstrapIp = bootstrapIp;
		
		this.disseminationCore = disseminationCore;
		this.communication = communication;		
		
	
		//Start the Thread!
		this.start();		
	}
	
	
	
	@Override
	public void resolve(String uci) {
		
		//Send resolve to child		
		ResolveMessage resolveMessage = new ResolveMessage(uci, "100", node.child, communication.getLocalIp());
		communication.sendMessage(resolveMessage);		
		
	}

	@Override
	public void register(String uci) {
		
		//Register it locally!		
		hashTable.put(uci, communication.getLocalIp());		
		
		//Send to all parents and childs!
		RegisterMessage registerAtGrandGrandParent = new RegisterMessage(uci, node.grandGrandParent, communication.getLocalIp());
		RegisterMessage registerAtGrandParent = new RegisterMessage(uci, node.grandParent, communication.getLocalIp());
		RegisterMessage registerAtParent = new RegisterMessage(uci, node.parent, communication.getLocalIp());
		RegisterMessage registerAtChild = new RegisterMessage(uci, node.child, communication.getLocalIp());
		RegisterMessage registerAtGrandChild = new RegisterMessage(uci, node.grandChild, communication.getLocalIp());
		RegisterMessage registerAtGrandGrandChild = new RegisterMessage(uci, node.grandGrandChild, communication.getLocalIp());
		communication.sendMessage(registerAtGrandGrandParent);
		communication.sendMessage(registerAtGrandParent);
		communication.sendMessage(registerAtParent);
		communication.sendMessage(registerAtChild);
		communication.sendMessage(registerAtGrandChild);
		communication.sendMessage(registerAtGrandGrandChild);
	}

	@Override
	public void shutdown() {
		
		runLookUpService = false;
		
		//Perform garceful shutdown!
		//Send join_response to child and parent
		
		//Or not.. hope that it solves itself! :)
		
		
	}
	
	
	@Override
	public void run() {
		
		//Connect to bootstrap
		
		//Send Join message
		JoinMessage joinMessage = new JoinMessage(bootstrapIp, communication.getLocalIp());
		communication.sendMessage(joinMessage);
		
		while(!connected){
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
				
		while(runLookUpService){
			
			try {

				//Send out the keepalive to keep the system stable								
				//Send to child, grandchild
				KeepAliveMessage keepAliveMessageGrandGrandChild = new KeepAliveMessage(node.grandGrandChild, communication.getLocalIp());
				communication.sendMessage(keepAliveMessageGrandGrandChild);
				KeepAliveMessage keepAliveMessageGrandChild = new KeepAliveMessage(node.grandChild, communication.getLocalIp());
				communication.sendMessage(keepAliveMessageGrandChild);	
				KeepAliveMessage keepAliveMessageChild = new KeepAliveMessage(node.child, communication.getLocalIp());
				communication.sendMessage(keepAliveMessageChild);							
				//Send to parent, grandparent
				KeepAliveMessage keepAliveMessageParent = new KeepAliveMessage(node.parent, communication.getLocalIp());
				communication.sendMessage(keepAliveMessageParent);
				KeepAliveMessage keepAliveMessageGrandParent = new KeepAliveMessage(node.grandParent, communication.getLocalIp());
				communication.sendMessage(keepAliveMessageGrandParent);
				KeepAliveMessage keepAliveMessageGrandGrandParent = new KeepAliveMessage(node.grandGrandParent, communication.getLocalIp());
				communication.sendMessage(keepAliveMessageGrandGrandParent);

				

				//Check for if someone is dead!
				if(node.grandGrandParentTtl <= 0){					
					//Recover!
					node.grandGrandParentTtl = 5;					
					node.grandGrandParent = node.grandParent;
				}
				if(node.grandParentTtl <= 0){					
					//Recover!
					node.grandParentTtl = 5;					
					node.grandParent = node.grandGrandParent;
				}
				if(node.parentTtl <= 0){					
					//Recover!
					node.parentTtl = 5;					
					node.parent = node.grandParent;
				}	
				if(node.childTtl <= 0){					
					//Recover!
					node.childTtl = 5;					
					node.child = node.grandChild;
				}	
				if(node.grandChildTtl <= 0){					
					//Recover!
					node.grandChildTtl = 5;					
					node.grandChild = node.grandGrandChild;
				}
				if(node.grandGrandChildTtl <= 0){					
					//Recover!
					node.grandGrandChildTtl = 5;					
					node.grandGrandChild = node.grandChild;
				}
					
				//Decrease the TTL for each of the nodes				
				node.grandGrandParentTtl--;
				node.grandParentTtl--;
				node.parentTtl--;				
				node.selfTtl--;
				node.childTtl--;
				node.grandChildTtl--;
				node.grandGrandChildTtl--;
				
				//Do not do this too often
				Thread.sleep(2000);			
				
				//Print the world according to me
				/*
				System.out.println("---- ======== ----");
		        System.out.println("GGP: " + node.grandGrandParent + ", GP: " + node.grandParent + ", P: "+ node.parent);
		        System.out.println("SELF: " + node.self); 
		        System.out.println("C: "+ node.child + ", GC: " + node.grandChild + ", GGC: " + node.grandGrandChild);
		        System.out.println("---- ========= ----");		            
		        */  
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}



	@Override
	public void handleMessage(Message message) {
		
		
		switch (message.type) {

		case Message.JOIN:
			//System.out.println("JOIN Message Recieved");			
			JoinMessage joinMessage = (JoinMessage) message;

			//Send the JOIN answer back			
			JoinResponseMessage joinResponse = new JoinResponseMessage(node.grandParent, node.parent, node.self, joinMessage.fromIp, node.child, node.grandChild, node.grandGrandChild, joinMessage.fromIp, communication.getLocalIp());
			communication.sendMessage(joinResponse);

			//Let the other Node join the system			
			node.grandGrandChild = node.grandChild;
			node.grandChild = node.child;
			node.child = joinMessage.fromIp;
			
			break;

			
			
		case Message.JOIN_RESPONSE:
			//System.out.println("JOIN_RESPONSE Message Recieved");
			JoinResponseMessage joinResponseMessage = (JoinResponseMessage) message;
			
			// Join the network, on the specified place
			node.grandGrandParent = joinResponseMessage.grandGrandParent;
			node.grandParent = joinResponseMessage.grandParent;
			node.parent = joinResponseMessage.parent;
			
			//self.self = joinResponseMessage.self;
			node.self = communication.getLocalIp();

			node.child = joinResponseMessage.child;
			node.grandChild = joinResponseMessage.grandChild;
			node.grandGrandChild = joinResponseMessage.grandGrandChild;

			connected = true;
			
			
			break;
		
		case Message.REGISTER:
			//System.out.println("REGISTER Message Recieved");			
			RegisterMessage registerMessage = (RegisterMessage) message;
						
			//Register in local cache
			hashTable.put(registerMessage.uci, registerMessage.fromIp);					

			break;
			
		case Message.RESOLVE:
			//System.out.println("RESOLVE Message Recieved");			
			ResolveMessage resolveMessage = (ResolveMessage) message;
						
			//Look if exists in the list		
	       	String resolvedIp = hashTable.get(resolveMessage.uci);
	       
	       	if(resolvedIp != null){
				//If it does, sent RESOLVE_ANSWER back
	       		
	       		ResolveResponseMessage resolveResponse = new ResolveResponseMessage(resolveMessage.uci, resolvedIp, resolveMessage.fromIp, communication.getLocalIp());
	       		communication.sendMessage(resolveResponse);
	       	}
	       	else {
	       		//Otherwise
	       		
				//Subtract TTL	       		
	       		resolveMessage.ttl--;

				//IF TTL < 0			
	       		if(resolveMessage.ttl <= 0){

					//forward to child
	       			communication.sendMessage(resolveMessage);
	       		}
	       		//Else Drop it!
	       	}
	       	
	       	

			break;
			
		case Message.RESOLVE_RESPONSE:
			//System.out.println("RESOLVE_RESPONSE Message Recieved");			
			ResolveResponseMessage resolveResponseMessage = (ResolveResponseMessage) message;
			
			//Call the resolve resonse callback to the user!
			disseminationCore.callResolveResponseListener(resolveResponseMessage.uci, resolveResponseMessage.resolvedIp);
		
			break;
			
		case Message.KEEPALIVE:
			//System.out.println("KEEPALIVE Message Recieved");			
			KeepAliveMessage keepAliveMessage = (KeepAliveMessage) message;

			
			//Send back one's localNode data
			KeepAliveResponseMessage keepAliveRespons = new KeepAliveResponseMessage(node.grandGrandParent, node.grandParent, node.parent, node.self, node.child, node.grandChild, node.grandGrandChild, keepAliveMessage.fromIp, communication.getLocalIp());
			communication.sendMessage(keepAliveRespons);
			
			break;
			
		case Message.KEEPALIVE_RESPONSE:
			KeepAliveResponseMessage keepAliveResponseMessage = (KeepAliveResponseMessage) message;
			//System.out.println("KEEPALIVE_RESPONSE Message Recieved from: " + keepAliveResponseMessage.fromIp);			
			
			
			if(keepAliveResponseMessage.fromIp.equalsIgnoreCase(communication.getLocalIp())){
				//It is from myself...
				//Dont bother!!!
			} 
			else {
				//System.out.println("KEEPALIVE_RESPONSE Message Recieved, from someone else! " + keepAliveResponseMessage.fromIp +  "->" + communication.getLocalIp());

				
				//Find who it is an answer from
				//Reset TTL for that parent/child
				if(node.grandGrandParent.equalsIgnoreCase(keepAliveResponseMessage.fromIp)){				
					node.grandGrandParentTtl = 5;				
				}
				if(node.grandParent.equalsIgnoreCase(keepAliveResponseMessage.fromIp)){				
					node.grandParentTtl = 5;				
				}
				if(node.parent.equalsIgnoreCase(keepAliveResponseMessage.fromIp)){				
					node.parentTtl = 5;				
				}
				if(node.child.equalsIgnoreCase(keepAliveResponseMessage.fromIp)){				
					node.childTtl = 5;				
				}
				if(node.grandChild.equalsIgnoreCase(keepAliveResponseMessage.fromIp)){				
					node.grandChildTtl = 5;				
				}
				if(node.grandGrandChild.equalsIgnoreCase(keepAliveResponseMessage.fromIp)){				
					node.grandGrandChildTtl = 5;				
				}
				
				//Find oneself in the list
				//Update one's own list
				//Not sure if reset TTL also...			
				
				
				if(node.self.equalsIgnoreCase(keepAliveResponseMessage.grandGrandChild)){						
					node.grandGrandParent = keepAliveResponseMessage.self;
					node.grandParent = 	keepAliveResponseMessage.child;
					node.parent = keepAliveResponseMessage.grandChild;				
					//node.self = keepAliveResponseMessage.grandGrandChild
					//node.child = N/A
					//node.grandChild = N/A
					//node.grandGrandChild = N/A 
				}
				else if(node.self.equalsIgnoreCase(keepAliveResponseMessage.grandChild)){					
					node.grandGrandParent = keepAliveResponseMessage.parent;
					node.grandParent = 	keepAliveResponseMessage.self;
					node.parent = keepAliveResponseMessage.child;				
					//node.self = keepAliveResponseMessage.grandChild
					node.child = keepAliveResponseMessage.grandGrandChild;
					//node.grandChild = N/A
					//node.grandGrandChild = N/A 
				}
				else if(node.self.equalsIgnoreCase(keepAliveResponseMessage.child)){
					node.grandGrandParent = keepAliveResponseMessage.grandParent;
					node.grandParent = 	keepAliveResponseMessage.parent;
					node.parent = keepAliveResponseMessage.self;				
					//node.self = keepAliveResponseMessage.child
					node.child = keepAliveResponseMessage.grandChild;
					node.grandChild = keepAliveResponseMessage.grandGrandChild;
					//node.grandGrandChild = N/A
				}
				else if(node.self.equalsIgnoreCase(keepAliveResponseMessage.parent)){	
					//node.grandGrandParent = N/A	
					node.grandParent = 	keepAliveResponseMessage.grandGrandParent;
					node.parent = keepAliveResponseMessage.grandParent;
					//node.self = keepAliveResponseMessage.parent
					node.child = keepAliveResponseMessage.self;
					node.grandChild = keepAliveResponseMessage.child;
					node.grandGrandChild = keepAliveResponseMessage.grandChild;	
				} 
				else if(node.self.equalsIgnoreCase(keepAliveResponseMessage.grandParent)){		
					//node.grandGrandParent = N/A
					//node.grandParent = N/A
					node.parent = keepAliveResponseMessage.grandGrandParent;
					//node.self = keepAliveResponseMessage.grandParent
					node.child = keepAliveResponseMessage.parent;
					node.grandChild = keepAliveResponseMessage.self;
					node.grandGrandChild = keepAliveResponseMessage.child;	
				}
				else if(node.self.equalsIgnoreCase(keepAliveResponseMessage.grandGrandParent)){				
					//node.grandGrandParent = N/A				
					//node.grandParent = N/A				
					//node.parent = N/A
					//node.self = keepAliveResponseMessage.grandGrandParent
					node.child = keepAliveResponseMessage.grandParent;
					node.grandChild = keepAliveResponseMessage.parent;
					node.grandGrandChild = keepAliveResponseMessage.self;	
				}
				else {
					// if I am not on it, bootstrap again!				
					//Send Join message
					JoinMessage reJoinMessage = new JoinMessage(bootstrapIp, communication.getLocalIp());
					communication.sendMessage(reJoinMessage);									
				}
				
			}
			
			
			break;
		
			
			
		}
	}

}
