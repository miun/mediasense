
import java.util.Random;


import se.miun.mediasense.addinlayer.AddInManager;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.PublishSubscribeExtension;
import se.miun.mediasense.addinlayer.extensions.publishsubscribe.SubscriptionEventListener;
import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;
import se.miun.mediasense.disseminationlayer.disseminationcore.GetEventListener;
import se.miun.mediasense.disseminationlayer.disseminationcore.GetResponseListener;
import se.miun.mediasense.disseminationlayer.disseminationcore.ResolveResponseListener;
import se.miun.mediasense.disseminationlayer.disseminationcore.SetEventListener;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;



public class ClientApplication implements GetResponseListener, SetEventListener, SubscriptionEventListener, ResolveResponseListener, GetEventListener {

		//MediaSense Platform Application Interfaces
	    MediaSensePlatform platform;
	    DisseminationCore core;
	    AddInManager addInManager;
	    PublishSubscribeExtension ps;
	    
	    

	    
	    //Main Entry Point////////////////////////	    
	    public static void main(String[] args) {

	        //Create TestPlatform
	        ClientApplication application = new ClientApplication();
	        //Run some tests
	        application.runTests();

	    }
	    //////////////////////////////////////////	    
	    
	    
    
	    public ClientApplication() {

	        //Create the platform itself
	        platform = new MediaSensePlatform();
	        	       
	        //Initialize the platform with chosen LookupService type and chosen Communication type. 
	        //platform.initalize(LookupServiceInterface.SERVER, CommunicationInterface.TCP); //For Server Lookup and TCP P2P communication
	        //platform.initalize(LookupServiceInterface.SERVER, CommunicationInterface.TCP_PROXY); //For server Lookup and Proxy TCP P2P communication (Which penetrates NAT)
	        //platform.initalize(LookupServiceInterface.SERVER, CommunicationInterface.RUDP); //For Server Lookup and RUDP P2P communication
	        
	        //platform.initalize(LookupServiceInterface.DISTRIBUTED, CommunicationInterface.TCP); //For Distributed Lookup and TCP P2P communication	        
	        //platform.initalize(LookupServiceInterface.DISTRIBUTED_BOOTSTRAP, CommunicationInterface.TCP); //For being the Bootstrap and TCP P2P communication	        
	        
	        platform.initalize(LookupServiceInterface.DISTRIBUTED, CommunicationInterface.RUDP); //For Distributed Lookup and RUDP P2P communication
	        //platform.initalize(LookupServiceInterface.DISTRIBUTED_BOOTSTRAP, CommunicationInterface.RUDP); //For being the Bootstrap and RUDP P2P communication
	        
	        //platform.initalize(LookupServiceInterface.DISTRIBUTED, CommunicationInterface.TCP_PROXY); //For Distributed Lookup and TCP_PROXY P2P communication
	        //platform.initalize(LookupServiceInterface.DISTRIBUTED_BOOTSTRAP, CommunicationInterface.TCP_PROXY); //For being the bootstrap and TCP_PROXY P2P communication

        	
	        	        
	       	//Extract the core for accessing the primitive functions
	        core = platform.getDisseminationCore();
	        
	        //Set the response listeners
	        core.setGetResponseListener(this);
	        core.setResolveResponseListener(this);
	        
	        //Set the event listeners
	        core.setGetEventListener(this);
	        core.setSetEventListener(this);	        
	       
	        //Extract the add in manager, which handles all extended functionality
	        addInManager = platform.getAddInManager();

	        //Start the publish subscribe add-in, which enables publish subscribe functionality
	        ps = new PublishSubscribeExtension(); //Create the add in
	        ps.setSubscriptionEventListener(this); //Set the new listener  
	        addInManager.loadAddIn(ps); //Load and start the add in

	        
	    }

	    //Example of some basic functionality testing
	    public void runTests(){
	        try {
		        
	            Thread.sleep(2000);   
	            
		        //Test REGISTER, will REGISTER a UCI in the lookup service
	        	System.out.println("Testing Register");
	            core.register("test@miun.se/temperature");
	
	            Thread.sleep(2000);   
	          	                        
	            //Test RESOLVE, will RESOLVE a UCI in the lookup service and return a ReoslveResponse call
	        	System.out.println("Testing Resolve");
	            core.resolve("test@miun.se/temperature");
	            
	                   
	            Thread.sleep(20000);       

	    	    boolean runProgram = true;
	            while(runProgram){
	            	Thread.sleep(1000);
	            }
	            
	            //Test Shutdown, will turn off the whole system
	            System.out.println("Testing Shutdown");
	            platform.shutdown();
	            
	        } catch (Exception e){
	            e.printStackTrace();
	        }   
	    	
	    }
	    
	    
	    
	    
	    //Response Listeners///////////////////////////////////
		//I.E., Answers from function calls////////////////////
	    @Override
		public void resolveResponse(String uci, String ip) {
		    //This is called as a response the register function call	    	
			try{
				System.out.println("[ResolveResponse] " + uci + ": " + ip);
		
				//We can only test GET, SET, and SUBSCRIBE after a resolve is successful, 
	            Thread.sleep(2000);   
	            
				//Test GET, will return a call to get response listener with the value 
		        System.out.println("Testing Get");
		        core.get(uci, ip);
		        
		        //Test SET, will trigger a set event on the remote entity
		        Thread.sleep(2000);  
		        System.out.println("Testing Set");
		        core.set(uci, "20.0", ip);
		               
		        
	            //Begin testing the Publish/Subscribe add in
	            Thread.sleep(5000);     
		        
	            //Test START_SUBSCRIBE, will create a subscription on the remote entity. If it has a Publish/Subscribe extension running.		        
		        System.out.println("Testing Subscribe");
		        ps.startSubscription(uci, ip);
		        
		        //Test NOTIFY_SUBSCRIBERS, which will trigger a subscription event on each remote entity which subscribes to the UCI
		        Thread.sleep(1000); 
		        System.out.println("Notifying subscribers of new value");
		        ps.notifySubscribers(uci, "43.4");
	        
	            //Test END_SUBSCRIBE, will remove the subscription on the remote entity 		        
		        Thread.sleep(2000);  
		        System.out.println("Testing UnSubscribe");
		        ps.endSubscription(uci, ip);
		        
	            //A test that the END_SUBSCRIBE, was actually removed on the remote entity 		        
		        Thread.sleep(2000);  		        
		        System.out.println("Notifying subscribers of new value (should not return a new value)");
		        ps.notifySubscribers(uci, "45.4");
		        
	        } catch(Exception e){
	        	e.printStackTrace();
	        }       
		
		}
	    
		@Override
	    public void getResponse(String uci, String value) {
			//This is called as a response the get function call
	        System.out.println("[GetResponse] " + uci + ": " + value);
	    }
		///////////////////////////////////////////

		
	    //Event Listeners///////////////////////////////////
		//I.E., Incoming events!//////////////////////////		
		@Override
		public void getEvent(String source, String uci) {
			//This is called on the remote entity as a response the get function call
			//The idea is that this function shall answer back with the newest value from the sensors			
			System.out.println("[GetEvent] " + source + ": " + uci);
			
			//But right now we send back a random number to simulate a sensor value
			Random r = new Random(System.currentTimeMillis());
			int randomInt = r.nextInt(100);
			platform.getDisseminationCore().notify(source, uci, "" + randomInt);			
		}
		@Override
	    public void setEvent(String uci, String value) {
			//This is called on the remote entity as a response the set function call
			System.out.println("[SetEvent] " + uci + ": " + value);			
		}		
		@Override
	    public void subscriptionEvent(String uci, String value) {
			//This is called on the remote entity as a response the notify subscribers function call
			System.out.println("[SubscriptionEvent] " + uci + ": " + value);
	    }

		///////////////////////////////////////////
		
		

}
