package manager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import manager.dht.FingerEntry;
import manager.dht.messages.broadcast.BroadcastMessage;
import manager.listener.FingerChangeListener;
import manager.listener.NodeMessageListener;

public class Statistic implements FingerChangeListener,NodeMessageListener {

	//Different trigger types
	public static final int TRIGGER_SECOND = 1; 
	public static final int TRIGGER_CONNECT = 2; 
	
	//Flush the data every X ms to file
	private static final long FLUSH_PERIOD = 3000;
	
	private Manager manager;
	private FileWriter fileWriter;
	private BufferedWriter fileBufferedWriter;
	
	//Current trigger type
	private int triggerType;
	
	//Statistic data
	long secondCounter = 0;
	long txPkt = 0;
	long txPktD = 0;
	long txData = 0;
	long txDataD = 0;
	long fingerChanges = 0;
	long fingerChangesD = 0;
	long connected = 0;
	long connectedD = 0;
	TreeMap<Integer,Long> txPktDetail;
	TreeMap<Integer,Long> txPktDetailD;
	
	//Timer, as you can see
	Timer timer;
	
	public Statistic(Manager manager,String filename,int triggerType) throws IOException {
		this.manager = manager;
		
		//Open / create file
		try {
			fileWriter = new FileWriter(filename);
			fileBufferedWriter = new BufferedWriter(fileWriter);

			//Write header
			fileBufferedWriter.write("# TriggerType: ");
			switch(triggerType) {
			case TRIGGER_SECOND:
				fileBufferedWriter.write("TRIGGER_SECOND\n#-----\n");
				break;
			case TRIGGER_CONNECT:
				fileBufferedWriter.write("TRIGGER_CONNECT\n#-----\n");
				break;
			}
			
			//Write column headers
			fileBufferedWriter.write("#TimeStamp\tSec\tHth\tCon\tConD\tFin\tFinD\tData\tDataD\tPkt\tPktD\n");
			
		}
		catch (IOException e) {
			//TODO what shall happen here
			//Forward at the moment
			throw e;
		}
		
		this.triggerType = triggerType;
		timer = new Timer();

		//Create map for packet details
		txPktDetail = new TreeMap<Integer,Long>();
		txPktDetailD = new TreeMap<Integer,Long>();
		
		//Prepare delta map and copy all elements to counter map
		resetDeltaMap();
		txPktDetail.putAll(txPktDetailD);
		
		//Register listener
		manager.addFingerChangeListener(this);
		manager.addNodeMessageListener(null, this); //Listen to ALL nodes
		
		System.out.println("Statistic started for " + filename);
	}
	
	public synchronized void start() {
		//Start the Timer!
		
		timer = new Timer();
		
		//Start second trigger
		if(triggerType == TRIGGER_SECOND) {
			timer.schedule(new TimerTask() {
	
				@Override
				public void run() {
					triggerSecond();
				}
				
			}, 1000L, 1000L);
		}
		
		//Schedule flush every 3 seconds
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				triggerFlush();
			}
			
		}, FLUSH_PERIOD, FLUSH_PERIOD);
		
	}
	
	public void stop() {
		synchronized(timer) {
			//Stop all tasks
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}
	
	@Override
	public void OnNodeMessage(Date timeStamp, Message msg) {
		Message handleMessage = msg;
		boolean broadcast = false;
		
		if(msg.getType() == Message.BROADCAST) {
			//handle broadcast and inner message
			countMessage(msg,false);
			handleMessage = ((BroadcastMessage)msg).extractMessage();
			broadcast = true;
		}
		
		countMessage(handleMessage,broadcast);
	}
	
	private void countMessage(Message msg,boolean broadcast) {
		Long count;

		synchronized (this) {
			//Increment packet count
			count = txPktDetail.get(msg.getType());
			if(count != null) txPktDetail.put(msg.getType(), count + 1);

			count = txPktDetailD.get(msg.getType());
			if(count != null) txPktDetailD.put(msg.getType(), count + 1);
			
			//Count all messages (do not count broadcasts because they are counted as normal messages
			if(msg.getType() != Message.BROADCAST) {
				txPkt += 1;
				txPktD += 1;
			}

			//Count amount of sent data
			//Don't count bcast
			if(!broadcast) {			
				txData += msg.getDataAmount();
				txDataD += msg.getDataAmount();
			}
		}
		
		//Trigger connect event
		if(msg.getType() == Message.JOIN_ACK) {
			//Increment connected counter
			connected++;
			connectedD++;

			//Trigger write event
			if(triggerType == TRIGGER_CONNECT) {
				writeDataSet();
			}
		}
	}

	@Override
	public void OnFingerChange(int changeType, FingerEntry node, FingerEntry finger) {
		//Increment finger changes
		synchronized (this) {
			fingerChanges++;
			fingerChangesD++;
		}
	}
	
	private void triggerSecond() {
		//Trigger writeData
		writeDataSet();
		
		//Increment seconds
		synchronized(this) {
			secondCounter++;
		}
	}
	
	private void triggerFlush() {
		//Flush buffers
		try {
			synchronized(this) {
				if(fileBufferedWriter != null) {
					fileBufferedWriter.flush();
				}
			}
		}
		catch (IOException e) {
			System.out.println("Error flushing buffer" + e.getMessage());
			stop();
		}
	}

	
	private void writeDataSet() {
		//Write one data set to file
		try {
			//Write statistic
			synchronized(this) {
				fileBufferedWriter.write(
						new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()) + "\t" +
						secondCounter + "\t" + 
						manager.calculateHealthOfDHT(false) + "\t" +
						connected + "\t" +
						connectedD + "\t" +
						fingerChanges + "\t" +
						fingerChangesD + "\t" +
						txData + "\t" + 
						txDataD + "\t" +
						txPkt + "\t" + 
						txPktD + "\t"
				);
				
				//Write packet data
				for(Long l: txPktDetail.values()) {
					fileBufferedWriter.write(l.toString() + "\t");
				}

				//Write delta packet data
				for(Long l: txPktDetailD.values()) {
					fileBufferedWriter.write(l.toString() + "\t");
				}
				
				//Write newline
				fileBufferedWriter.write("\n");
			}
			
			//Reset the delta data
			txDataD = 0;
			txPktD = 0;
			fingerChangesD = 0;
			connectedD = 0;
			resetDeltaMap();
		}
		catch (IOException e) {
			//Message and stop
			System.out.println("Error writing statistic data " + e.getMessage());
			stop();
		}
	}
	
	private void resetDeltaMap() {
		//Add the messages you like!!
		
		/*txPktDetailD.put(Message.JOIN,0L);
		txPktDetailD.put(Message.BROADCAST,0L);
		txPktDetailD.put(Message.JOIN_RESPONSE,0L);
		txPktDetailD.put(Message.JOIN_ACK,0L);
		txPktDetailD.put(Message.JOIN_BUSY,0L);
		txPktDetailD.put(Message.DUPLICATE_NODE_ID,0L);
		txPktDetailD.put(Message.FIND_PREDECESSOR,0L);
		txPktDetailD.put(Message.FIND_PREDECESSOR_RESPONSE,0L);
		txPktDetailD.put(Message.NODE_JOIN_NOTIFY,0L);
		txPktDetailD.put(Message.NODE_LEAVE_NOTIFY,0L);
		txPktDetailD.put(Message.KEEPALIVE,0L);
		*/
	}
}
 