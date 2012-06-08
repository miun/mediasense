package se.miun.mediasense.disseminationlayer.communication;

import java.net.InetAddress;
import java.net.Socket;


public abstract class AbstractCommunication {
	
	public final static int TCP = 1;
	public final static int UDP = 2;
	public final static int RUDP = 3;
	public final static int SCTP = 4;	
	public final static int TCP_PROXY = 5;

	//Abstract functions
	public abstract void sendMessage(Message message) throws DestinationNotReachableException;
	public abstract void shutdown();
	
	//Return local address
	public String getLocalIp() {
		try {			
						
			InetAddress address = InetAddress.getLocalHost();			
			if(!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
				return address.getHostAddress();
			}
			else {				
				//Workaround because Linux is stupid...	
				Socket s = new Socket("www.google.com", 80);
				String ip = s.getLocalAddress().getHostAddress();
				s.close();
				return ip;
			}
		} catch (Exception e1) {
			return "127.0.0.1";
		}
	}
}
