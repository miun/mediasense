package se.miun.mediasense.disseminationlayer.disseminationcore;

import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.communication.GetMessage;
import se.miun.mediasense.disseminationlayer.communication.NotifyMessage;
import se.miun.mediasense.disseminationlayer.communication.SetMessage;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;
import se.miun.mediasense.interfacelayer.MediaSensePlatform;



public class DisseminationCore {
	

	//Parent platform
	private MediaSensePlatform platform = null;
	
	//Interfaces
	private LookupServiceInterface lookupService = null;
	private CommunicationInterface communication = null;
	
	
	//Response Listeners
	private ResolveResponseListener resolveResponseListener = null;	
	private GetResponseListener getResponseListener = null;
		
	//Event Listeners
	private SetEventListener setEventListener = null;
	private GetEventListener getEventListener = null;
	
	/**
	 * Creates the Dissemination core, takes the parent platform as argument
	 * 
	 * @param platform the parent platform
	 */
	public DisseminationCore(MediaSensePlatform platform) {
		this.platform = platform;		
	
	}	
	
	/**
	 * Makes so that the dissemination core uses a specified lookup service
	 * 
	 * @param lookupService the lookup service to be used
	 */
    public void useLookupService(LookupServiceInterface lookupService){
    	this.lookupService = lookupService;
    }
    
    /**
     * Makes so that the dissemination core uses a specified communication type
     * 
     * @param communication the communication type to be used
     */
    public void useCommunication(CommunicationInterface communication){
    	this.communication = communication;
    }
	
	/**
	 * Check if both lookup service and communication is running
	 * 
	 * @return true if everything is initialized
	 */
	public boolean isInitalized(){
		
		if(lookupService != null && communication != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Closes down the communication and the lookup service
	 * 
	 */
	public void shutdown(){
		
		communication.shutdown();
		lookupService.shutdown();
	}
	
	/**
	 * Returns the parent MediaSensePlatform
	 * 
	 * @return the parent platform
	 */
	public MediaSensePlatform getMediaSensePlatform(){
		return platform;
	}
	
	/**
	 * Returns the communication which currently is in use
	 * 
	 * @return the communcation in use
	 */
	public CommunicationInterface getCommunicationInterface(){
		return communication;
	}
	
	/**
	 * Returns the lookup service which currently is in use
	 * 
	 * @return the lookup service in use
	 */
	public LookupServiceInterface getLookupServiceInterface(){
		return lookupService;
	}

	
	//Listener interfaces towards the user application	
	/**
	 * Sets the ResolveResponseListener 
	 * @param listener ResolveResponseListener
	 */
	public void setResolveResponseListener(ResolveResponseListener listener){
        this.resolveResponseListener = listener;
    }
	/**
	 * Sets the GetResponseListener 
	 * @param listener GetResponseListener
	 */
    public void setGetResponseListener(GetResponseListener listener){
        this.getResponseListener = listener;
    }
	/**
	 * Sets the SetEventListener 
	 * @param listener SetEventListener
	 */
    public void setSetEventListener(SetEventListener listener){
        this.setEventListener = listener;
    }
	/**
	 * Sets the GetEventListener 
	 * @param listener GetEventListener
	 */
    public void setGetEventListener(GetEventListener listener){
    	this.getEventListener = listener;
    }
        
    //Create events by calling these
    /**
     * Used to call the listener and create a callback
     * 
     * @param uci the UCI which have been resolved
     * @param ip the IP which the UCI has been resolved to
     */
    public void callResolveResponseListener(String uci, String ip){
        if(resolveResponseListener != null){
            resolveResponseListener.resolveResponse(uci, ip);
        }
    }    
    
    /**
     * Used to call the listener and create a callback
     * 
     * @param uci the UCI which have been fetched
     * @param value the value of the UCI
     */
    public void callGetResponseListener(String uci, String value){
        if(getResponseListener != null){
            getResponseListener.getResponse(uci, value);
        }
    }    
    /**
     * Used to call the listener and create a callback
     * 
     * @param uci the UCI which is being set
     * @param value the value which the UCI should be set to
     */
    public void callSetEventListener(String uci, String value){
        if(setEventListener != null){
            setEventListener.setEvent(uci, value);
        }
    }    
    /**
     * Used to call the listener and create a callback
     * 
     * @param ip the IP end point which is trying to fetch a value
     * @param uci the UCI which is trying to be fetched
     */
    
    public void callGetEventListener(String ip, String uci){
        if(getEventListener != null){
            getEventListener.getEvent(ip, uci);
        }
    }
       
    
    //Primitive functions!
    /**
     * The RESOLVE primitive action, which resolves an UCI in the lookupService.
     * Fires off a resolveReponse callback with the answer.
     * 
     * @param uci the UCI to be resolved
     */
    public void resolve(String uci){
    	if(lookupService != null){    		
    		lookupService.resolve(uci);    		
    	}
    }    
    /**
     * The REGISTER primitive action, which registyers an UCI in the lookupService.
     * 
     * @param uci the UCI to be registered
     */
    public void register(String uci){
    	if(lookupService != null){
    		lookupService.register(uci);    		
    	}    	
    }
    /**
     * The GET primitive action, which fetches the value from another entity.
     * Fires off a getReponse callback with the answer.
     * 
     * @param uci the UCI to be fetched
     * @param ip the IP end point which has been previously been resolved to manage the UCI
     */
    public void get(String uci, String ip){
    	if(communication != null){    		
    		GetMessage message = new GetMessage(uci, ip, communication.getLocalIp());    		
    		communication.sendMessage(message);
    		}
    }    
    /**
     * The SET primitive action, which pushes a value to another entity.
     * 
     * @param uci the UCI to be set on the remote entity
     * @param value the value which the UCI shall be set to
     * @param ip the IP end point which has been previously been resolved to manage the UCI
     */
    public void set(String uci, String value, String ip){
    	if(communication != null){
    		SetMessage message = new SetMessage(uci, value, ip, communication.getLocalIp());
    		communication.sendMessage(message);
    	}
    }    
    /**
     * The NOTIFY primitive action, which sends a value back to a previously asking entity.
     * This is the return call for the GetEvent callback.
     * 
     * @param ip the IP which the value should be sent to
     * @param uci the UCI of the value
     * @param value the actual value of the UCI
     */
    public void notify(String ip, String uci, String value){
    	
    	if(communication != null){
    		NotifyMessage  message = new NotifyMessage(uci, value, ip, communication.getLocalIp());
    		communication.sendMessage(message);    		
    	}  
    }
}
