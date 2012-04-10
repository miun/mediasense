package manager.ui;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class CircleGUI extends JFrame {

	private CirclePanel drawPanel;

	/**
	 * Create the frame.
	 */
	public CircleGUI() {
		setBounds(100, 100, 450, 450);
		drawPanel = new CirclePanel();
		add(drawPanel);
		
		this.setVisible(true);
	}
	
	public void addPoint(byte[] hash) {
		drawPanel.addPoint(hash);
	}

}
