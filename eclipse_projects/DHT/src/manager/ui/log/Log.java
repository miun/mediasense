package manager.ui.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import manager.Communication;
import manager.Manager;
import manager.Message;
import manager.dht.FingerEntry;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;
import manager.listener.KeepAliveListener;
import manager.listener.NodeListener;
import manager.listener.NodeMessageListener;

public class Log implements FingerChangeListener,KeepAliveListener,NodeMessageListener,NodeListener {
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
		manager.addNodeMessageListener(Message.JOIN_FINALIZE, this);
		manager.addNodeMessageListener(Message.DUPLICATE_NODE_ID, this);
		
		manager.addNodeMessageListener(Message.KEEPALIVE, this);
		
		manager.addNodeMessageListener(Message.NODE_JOIN_NOTIFY, this);
		manager.addNodeMessageListener(Message.NODE_LEAVE_NOTIFY, this);

		manager.addNodeMessageListener(Message.FIND_PREDECESSOR, this);
		manager.addNodeMessageListener(Message.FIND_PREDECESSOR_RESPONSE, this);

		manager.addNodeMessageListener(Message.CHECK_PREDECESSOR, this);
		manager.addNodeMessageListener(Message.CHECK_PREDECESSOR_RESPONSE, this);
		manager.addNodeMessageListener(Message.CHECK_SUCCESSOR, this);
		manager.addNodeMessageListener(Message.CHECK_SUCCESSOR_RESPONSE, this);
		
		manager.addNodeMessageListener(Message.REGISTER, this);
		manager.addNodeMessageListener(Message.REGISTER_RESPONSE, this);
		manager.addNodeMessageListener(Message.RESOLVE, this);
		manager.addNodeMessageListener(Message.RESOLVE_RESPONSE, this);

		manager.addNodeMessageListener(Message.NODE_SUSPICIOUS, this);
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
		write(new SimpleDateFormat("HH:mm:ss.SSS").format(timeStamp) + " | "  + msg.toString());
	}

	@Override
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		write(new SimpleDateFormat("HH:mm:ss.SSS").format(date) + " | Node: " + key.toString() + " Addr: " + networkAddress + " initiated KEEP-ALIVE");
		
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
	
	public void close() {
		//Close file
		try {
			bufferedWriter.close();
			fileWriter.close();
		}
		catch (IOException e) {
			//We don't care. Just report
			System.out.println("ERROR closing log " + e.getMessage());
		}
	}

	@Override
	public void onNodeAdd(Date timeStamp, Communication com) {
		write(new SimpleDateFormat("HH:mm:ss.SSS").format(timeStamp) + " | Node " + com.getNode().toString() + " ADDED");
	}

	@Override
	public void onNodeRemove(Date timeStamp, Communication com) {
		write(new SimpleDateFormat("HH:mm:ss.SSS").format(timeStamp) + " | Node " + com.getNode().toString() + " REMOVED");
	}

	@Override
	public void onKillNode(Date timeStamp, Communication com) {
		write(new SimpleDateFormat("HH:mm:ss.SSS").format(timeStamp) + " | Node " + com.getNode().toString() + " KILLED");
	}
}
