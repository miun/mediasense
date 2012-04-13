package manager.ui.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import manager.Manager;
import manager.Message;
import manager.dht.FingerEntry;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeMessageListener;

public class Log implements FingerChangeListener,KeepAliveListener,NodeMessageListener {
	private BufferedWriter bufferedWriter;
	private FileWriter fileWriter;
	private Timer flushTimer;
	
	public Log(Manager manager,String filename) throws IOException {
		try {
			//Open file
			fileWriter = new FileWriter(filename);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			//Write header
			bufferedWriter.write("Logging started " + (new Date()).toString() + "\n----------\n\n");
			
			//Create flush timer, to be efficient
			flushTimer = new Timer();
			flushTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					//Flash buffers
					flush();
				}
			}, 3000,3000);
		}
		catch (IOException e) {
			throw e;
		}
		
		//Register listener
		manager.addFingerChangeListener(this);
		manager.addKeepAliveListener(this);
		manager.addNodeMessageListener(Message.JOIN, this);
		manager.addNodeMessageListener(Message.JOIN_RESPONSE, this);
		manager.addNodeMessageListener(Message.JOIN_BUSY, this);
		manager.addNodeMessageListener(Message.JOIN_ACK, this);
		manager.addNodeMessageListener(Message.DUPLICATE_NODE_ID, this);
		manager.addNodeMessageListener(Message.KEEPALIVE, this);
		manager.addNodeMessageListener(Message.NODE_JOIN_NOTIFY, this);
		manager.addNodeMessageListener(Message.NODE_LEAVE_NOTIFY, this);

		//TODO more to come
		//manager.addNodeMessageListener(Message.NODE_JOIN_NOTIFY, this);
	}
	
	private void flush() {
		synchronized(bufferedWriter) {
			if(bufferedWriter != null) {
				try {
					bufferedWriter.flush();
				}
				catch (IOException e) {
					//Error
					System.out.println("ERROR flushing log file " + e.getMessage());
					bufferedWriter = null;
					fileWriter = null;
				}
			}
		}
	}
	
	private void write(String string)  {
		synchronized(bufferedWriter) {
			//Write string to log
			try {
				if(fileWriter != null) {
					bufferedWriter.write(string + "\n");
					bufferedWriter.flush();
				}
			}
			catch (IOException e) {
				//Error, reset
				System.out.println("Log file error " + e.getMessage());
				bufferedWriter = null;
				fileWriter = null;
			}
		}
	}

	@Override
	public void OnNodeMessage(Date timeStamp, Message msg) {
		write(new SimpleDateFormat().format(timeStamp) + " | "  + msg.toString());
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		write(new SimpleDateFormat().format(date) + " | Node: " + key.toString() + " Addr: " + networkAddress + " initiated KEEP-ALIVE");
		
	}

	@Override
	public void OnFingerChange(int changeType, FingerEntry node,FingerEntry finger) {
		String result;
		
		//Which action???
		if(changeType == FingerChangeListener.FINGER_CHANGE_ADD) {
			result = "ADD-NEW finger: ";
		}
		else if(changeType == FingerChangeListener.FINGER_CHANGE_REMOVE) {
			result = "REMOVE-OLD finger: ";
		}
		else if(changeType == FingerChangeListener.FINGER_CHANGE_ADD_BETTER) {
			result = "ADD-BETTER finger: ";
		}
		else if(changeType == FingerChangeListener.FINGER_CHANGE_REMOVE_WORSE) {
			result = "REMOVE-WORSE finger: ";
		}
		else {
			result = "UNKNOWN CHANGE TYPE! ";
		}
		
		result = result + finger.toString() + " @NODE: " + node.toString() + "";
		write(result);
	}
}
