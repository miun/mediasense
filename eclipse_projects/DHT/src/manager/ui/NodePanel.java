package manager.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NodePanel extends JPanel implements MouseListener {
	private NumberPanel numberPanel;
	private HashSet<JPanel> fingers;
	public NodePanel(NumberPanel numberPanel, int x, int y){
		this.numberPanel = numberPanel;
		this.setBounds(x-2,y-2,5,5);
		this.setBackground(Color.BLACK);
		this.addMouseListener(this);
	}
	
	public NumberPanel getNumberPanel() {
		return numberPanel;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {	
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		setBackground(Color.BLACK);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		setBackground(Color.RED);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
		
	
}
