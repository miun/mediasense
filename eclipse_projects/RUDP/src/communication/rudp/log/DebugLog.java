package communication.rudp.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DebugLog {
	private static final int FLUSH_PERIOD = 3000;
	
	private FileWriter logFile;
	private BufferedWriter logBuffer;
	
	private Timer timer;
	private TimerTask flushTask;

	public DebugLog(String filename) {
		try {
			logFile = new FileWriter(filename);
			logBuffer = new BufferedWriter(logFile);
			
			timer = new Timer();
			flushTask = new FlushTask();
			timer.schedule(flushTask, FLUSH_PERIOD,FLUSH_PERIOD);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String string) {
		try {
			logBuffer.write(string);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private class FlushTask extends TimerTask {
		@Override
		public void run() {
			try {
				logBuffer.flush();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
