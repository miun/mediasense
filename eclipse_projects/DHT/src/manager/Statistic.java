package manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import manager.dht.FingerEntry;
import manager.listener.FingerChangeListener;
import manager.listener.NodeMessageListener;

public class Statistic implements FingerChangeListener,NodeMessageListener {

	private static final int TIMER_PERIOD = 1000;
	
	private Manager manager;
	private FileWriter fileWriter;
	private BufferedWriter fileBufferedWriter;
	
	//Statistic data
	long secondCounter = 0;
	long txData = 0;
	long fingerChanges = 0;
	
	//Timer, as you can see
	Timer timer;
	
	//Detailed packet statistic
	HashMap<Integer,Long> txPktDetail;
	
	public Statistic(Manager manager,String filename) throws IOException {
		this.manager = manager;
		
		//Open / create file
		try {
			fileWriter = new FileWriter(filename);
			fileBufferedWriter = new BufferedWriter(fileWriter);
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
		
		System.out.println("Statistic started for " + filename);
	}
	
	public void start() {
		//Start the Timer!
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				triggerEvent();
			}
			
		}, 1000, 1000);
	}
	
	public void stop() {
		//Stop all tasks
		timer.cancel();
		timer.purge();
		timer = null;
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
	public void OnFingerChange(int changeType, FingerEntry node, FingerEntry finger) {
		//Increment finger changes
		fingerChanges += 1;
	}
	
	private void triggerEvent() {
		//TODO try to find out if the task is to slow!!!
		
		synchronized(this) {
			try {
				//Write...
				writeDataSet();
				
				//...and reset counter
				txData = 0;
				fingerChanges = 0;
				txPktDetail = new HashMap<Integer,Long>();
			}
			catch (IOException e) {
				System.out.println("ERROR WRITING STATISTIC !!!");
			}
		}
	}
	
	private void writeDataSet() throws IOException {
		//Write one data set to file
		try {
			//TODO add other data
			fileBufferedWriter.write(
					secondCounter + "\t" +
					manager.calculateHealthOfDHT(false) + "\n");
			
			//Flush buffer immediately
			fileBufferedWriter.flush();
		}
		catch (IOException e) {
			//Forward
			throw e;
		}
	}
}
