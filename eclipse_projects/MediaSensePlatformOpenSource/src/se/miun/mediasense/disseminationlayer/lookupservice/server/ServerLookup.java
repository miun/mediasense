package se.miun.mediasense.disseminationlayer.lookupservice.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import se.miun.mediasense.disseminationlayer.communication.CommunicationInterface;
import se.miun.mediasense.disseminationlayer.communication.Message;
import se.miun.mediasense.disseminationlayer.disseminationcore.DisseminationCore;
import se.miun.mediasense.disseminationlayer.lookupservice.LookupServiceInterface;



public class ServerLookup implements LookupServiceInterface{
	
	//This is the EXACT same code as before, to make it backwards compatible...
	//The Serverlookup is not intended for baseline usage either way...
	
	String serverIp = "193.10.119.33";
	int serverPort = 8008;
	
	DisseminationCore disseminationCore = null;	
	CommunicationInterface communication = null;
	
	public ServerLookup(DisseminationCore disseminationCore, CommunicationInterface communicationType) {

		this.communication = communicationType;
		this.disseminationCore = disseminationCore;
	
	}

	
	@Override
	public void resolve(final String uci) {
		Runnable r = new Runnable() {					
			@Override
			public void run() {
				try {				        	
										
					Socket s = new Socket(serverIp, serverPort);
                    PrintWriter out = new PrintWriter(s.getOutputStream());
                    out.println("RESOLVE");
                    out.println(uci);	                    	                    
                    out.flush();
                    
                    //SLEEP?
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String response = in.readLine();		                    
                    disseminationCore.callResolveResponseListener(uci, response);
                    out.close();
                    in.close();
                    s.close();
                                        
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		};	        	
		Thread t = new Thread(r);
		t.start();	    
		
	}


	@Override
	public void register(final String uci) {
		//Perform the REGISTER in the Overlay
    	Runnable r = new Runnable() {					
			@Override
			public void run() {
				try {				        	
										
					Socket s = new Socket(serverIp, serverPort);
                    PrintWriter out = new PrintWriter(s.getOutputStream());
                    out.println("REGISTER");
                    out.println(uci);	                    
                    String localIp = communication.getLocalIp();	                    
                    out.println(localIp);
                    out.flush();
                    
                    //SLEEP?
                    
                    out.close();
                    s.close();
                                                            
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		};	        	
		Thread t = new Thread(r);
		t.start();	    	
		
	}


	@Override
	public void shutdown() {
		//Do nothing..
		//Should actually de-register later...
		
		
	}


	@Override
	public void handleMessage(Message message) {
		//Do nothing..
		
	}

}
