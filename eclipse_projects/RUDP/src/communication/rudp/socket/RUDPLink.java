package communication.rudp.socket;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Timer;

public class RUDPLink {
	private int seq_own;
	private int seq_foreign;
	
	//Average
	private int avt_RTT;
	
	//Timeout listener
	private InetSocketAddress sa;
	private Timer timer;
	private RUDPLinkTimeoutListener listener_timeout;
	
	//Packet buffer
	private HashMap<RUDPDatagramPacket,Integer> packet_buffer;
	
	public RUDPLink(InetSocketAddress sa,RUDPLinkTimeoutListener listener,Timer timer) {
		//Set timer and listener
		this.listener_timeout = listener;
		
		//Set or create timer
		if(timer == null) {
			this.timer = new Timer();
		}
		else {
			this.timer = timer;
		}
		
		//Network address
		this.sa = sa;
	}
	
	public InetSocketAddress getSocketAddress() {
		return sa;
	}
}
