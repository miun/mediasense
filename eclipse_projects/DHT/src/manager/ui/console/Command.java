package manager.ui.console;

public class Command {
	public String cmd;
	public String[] param;
	
	public Command(String cmd, String[] param) {
		this.cmd = cmd;
		this.param = param;
	}
}
