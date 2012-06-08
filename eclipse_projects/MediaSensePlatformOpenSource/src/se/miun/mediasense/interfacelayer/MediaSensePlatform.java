package se.miun.mediasense.interfacelayer;

import se.miun.mediasense.addinlayer.AddInManager;
import se.miun.mediasense.disseminationlayer.communication.AbstractCommunication;
import se.miun.mediasense.disseminationlayer.communication.rudp.RUDPCommunication;
import se.miun.mediasense.disseminationlayer.communication.tcp.TcpCommunication;
import se.miun.mediasense.disseminationlayer.communication.tcpproxy.TcpProxyCommunication;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;
import se.miun.mediasense.disseminationlayer.lookupservice.distributed.Node;
import se.miun.mediasense.disseminationlayer.lookupservice.server.ServerLookup;



/**
 *
 * The MediaSense platform itself, which exposes all functionality towards the application developers.
 *
 * @author stefor
 */
public class MediaSensePlatform {
   
    private DisseminationCore disseminationCore= null;    
    private AddInManager addInManager= null;
        
    /**
     * Constructor, only instantiates the add-in manager
     */
    public MediaSensePlatform() {
    	
    	this.addInManager = new AddInManager(this);
    	
	}
   
    
    /**
     * 
     * Closes down the MediaSense platform.
     * It unloads all addIn's, disconnects from the LookupService, and lastly closes the Communication 
     */
    public void shutdown(){
    	
    	addInManager.unloadAllAddIns();
    	
    	if(disseminationCore != null){
    		disseminationCore.shutdown();
    	}
    	
    }

    /**
     * Initializes the MediaSense platform. Must be called before using
     * 
     * @param lookupServiceType Takes a Lookup Service Type, ex. LookupService.DHT
     * @param communicationType Takes a Communication Type, ex. Communication.RUDP
     * @return true if successfully started
     */
    public boolean initalize(int lookupServiceType, int communicationType){
    	
    	AbstractCommunication communication = null;
    	LookupServiceInterface lookupService = null;
    	disseminationCore = new DisseminationCore(this);
    	    	
		switch (communicationType) {

		case AbstractCommunication.TCP:
			communication = new TcpCommunication(disseminationCore);
			break;

		case AbstractCommunication.UDP:
			// Not Impl.
			break;
			
		case AbstractCommunication.RUDP:
			communication = new RUDPCommunication(disseminationCore);
			break;
			
		case AbstractCommunication.SCTP:
			// Not Impl.
			break;

		case AbstractCommunication.TCP_PROXY:
			try{			

				//Uses our public proxy machine, change this IP and port if you are using your own proxy server.
				communication = new TcpProxyCommunication(disseminationCore,"193.10.119.33", 45321);

			} catch (Exception e) {
				communication = null;
			}
			break;
			
		default:
			break;
		}

		
		switch (lookupServiceType) {

		case LookupServiceInterface.SERVER:
			lookupService = new ServerLookup(disseminationCore, communication);
			break;
			
		case LookupServiceInterface.DISTRIBUTED:			
			//Uses the public bootstrap node
			//lookupService = new DistributedLookup(disseminationCore, communication, "193.10.119.33"); //Public 
			//lookupService = new DistributedLookup(disseminationCore, communication, "10.14.1.76"); //Andra datorn
			lookupService = new Node(communication,disseminationCore, "10.13.1.122"); //Null to be a bootstrap node

			break;
			
			
		case LookupServiceInterface.DISTRIBUTED_BOOTSTRAP:
			lookupService = new Node(communication,disseminationCore, null); //Min dator
			break;

		case LookupServiceInterface.DHT_CHORD:
			// Not Impl.
			break;

		case LookupServiceInterface.DHT_PGRID:
			// Not Impl.
			break;

		default:
			break;
		}		
		
    	if (communication != null && lookupService != null) {
    		disseminationCore.useLookupService(lookupService);
    		disseminationCore.useCommunication(communication);    				
		}
    	
    	if(disseminationCore.isInitalized()){
    		return true;
    	} else {
        	return false;    		
    	}
    }
    
    /**
     * Returns the dissemination core, which is used to call primitive functions
     * 
     * @return the running dissemination core
     */
    public DisseminationCore getDisseminationCore(){
    	    	
    	return disseminationCore;
    }
    
	/**
	 *  Returns the add-in manager, which handles loading and unloading of add-ins. 
	 * 
	 * @return the add-in manager
	 */
    public AddInManager getAddInManager(){
    	
    	return addInManager;    			
    }
    
    /**
	 *  Returns true if the platform is sucessfully initalized, started, and running
	 * 
	 * @return true if the platform is running
	 */
    public boolean isInitalized(){
    	
    	return disseminationCore.isInitalized();
    }
       
}
