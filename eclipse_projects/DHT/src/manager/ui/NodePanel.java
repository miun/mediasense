package manager.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JPanel;

import manager.dht.NodeID;

@SuppressWarnings("serial")
public class NodePanel extends JPanel implements MouseListener {

	public NodePanel(String tooltip, Point p){
		this.setToolTipText(tooltip);
		//Set Dimension and Color
		int x = (int) p.getX();
		int y = (int) p.getY();
		this.setBounds(x-2,y-2,5,5);
		this.setBackground(Color.WHITE);
		
		//Listen to Mouse Events
		this.addMouseListener(this);
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
