package manager.ui.console;

public class ConsoleRunnable implements Runnable {
	private boolean bRun = true;
	
	
	@Override
	public void run() {
		while(bRun) {
			System.out.println("Test");
			
			try {
				Thread.sleep(500);
			}
			catch(Exception e) {
				
			}
		}
	}
	
	public void notifyStop() {
		bRun = false;
	}
}
