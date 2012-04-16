package manager.dht.messages.unicast;

import manager.Message;

public class JoinBusyMessage extends Message {

	public JoinBusyMessage(String from, String to) {
		super(from, to, Message.JOIN_BUSY);
	}
	
	public String toString() {
		//Return message info
		return super.toString("MSG-JOIN_BUSY"); 
	}

	//Return packet size for statistic
	public int getDataAmount() {
		return super.getDataAmount();
	}
}
