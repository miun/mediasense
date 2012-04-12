package manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.NodeMessageListener;

public class Statistic implements FingerChangeListener,NodeMessageListener {

	private Manager manager;
	private File file;
	private FileOutputStream fileOutputStream;
	
	//Statistic data
	long txData = 0;
	long txPkt = 0;
	long fingerChanges = 0;
	
	//Detailed packet statistic
	HashMap<Integer,Long> txPktDetail;
	
	public Statistic(Manager manager,String filename) throws IOException {
		this.manager = manager;
		
		//Open/create file
		try {
			file = new File(filename);
			fileOutputStream = new FileOutputStream(file);
		}
		catch (IOException e) {
			//TODO what shall happen here
			//Forward at the moment
			throw e;
		}

		//Create map for packet details
		txPktDetail = new HashMap<Integer,Long>();
		
		//Register listener
		manager.addFingerChangeListener(this);
		manager.addNodeMessageListener(Message.JOIN, this);
		manager.addNodeMessageListener(Message.JOIN_RESPONSE, this);
		manager.addNodeMessageListener(Message.DUPLICATE_NODE_ID, this);
		manager.addNodeMessageListener(Message.FIND_PREDECESSOR, this);
		manager.addNodeMessageListener(Message.FIND_PREDECESSOR_RESPONSE, this);
		manager.addNodeMessageListener(Message.NODE_JOIN_NOTIFY, this);
		manager.addNodeMessageListener(Message.NODE_LEAVE_NOTIFY, this);
	}
	
	@Override
	public void OnNodeMessage(Date timeStamp, Message msg) {
		Long count;
		
		//Increment packet count and put
		count = txPktDetail.get(msg.getType());
		if(count == null) count = 0L;
		txPktDetail.put(msg.getType(), count + 1);
		
		//Increment data counter
		//TODO later
	}

	@Override
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		//Increment finger changes
		fingerChanges += 1;
	}
}
