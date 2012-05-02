package communication;

public class DisseminationCore {
	
	private CommunicationInterface communication = null;
	
    public void useCommunication(CommunicationInterface communication){
    	this.communication = communication;
    }
	
	public void shutdown(){
		communication.shutdown();
	}
	
	public void handleMessage(Message msg) {
		
	}
}
 