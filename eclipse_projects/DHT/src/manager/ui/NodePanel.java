package manager.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JPanel;

import manager.dht.NodeID;

@SuppressWarnings("serial")
public class NodePanel extends JPanel implements MouseListener {
	private int x;
	private int y;
	private NumberPanel numberPanel;
	private CirclePanel cp;
	private HashMap<NodeID, Point> fingers;
	public NodePanel(NumberPanel numberPanel, int x, int y, CirclePanel cp){
		//Initialize objects
		this.numberPanel = numberPanel;
		this.x = x;
		this.y = y;
		this.cp = cp;
		this.fingers = new HashMap<NodeID, Point>();
		
		//Set Dimension and Color
		this.setBounds(x-2,y-2,5,5);
		this.setBackground(Color.BLACK);
		
		//Listen to Mouse Events
		this.addMouseListener(this);
	}
	
	public NumberPanel getNumberPanel() {
		//The Panel that shows the networkaddress, which belongs to this node
		return numberPanel;
	}
	
	public void addFinger(NodeID nodeID, int x, int y) {
		//Add finger with x and y 
		fingers.put(nodeID, new Point(x, y));
	}
	
	public void removeFinger(NodeID nodeID) {
		//Remove finger
		fingers.remove(nodeID);
	}
	
	public Collection<Point> getFingers() {
		return fingers.values();
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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
		//cp.setActiveNode(null);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		setBackground(Color.RED);
		cp.setActiveNode(this);
		//TODO why isnt that working?
		//this.numberPanel.repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
		
	
}
