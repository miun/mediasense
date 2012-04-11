package manager.ui;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {
	private JCheckBox clearOnKeepAlive;
	
	public ControlPanel() {
		this.setLayout(new FlowLayout());
		
		clearOnKeepAlive = new JCheckBox("clear on KA");
		this.add(clearOnKeepAlive);
	}
	
	public boolean isClearOnKeepalive() {
		return clearOnKeepAlive.isSelected();
	}
}
