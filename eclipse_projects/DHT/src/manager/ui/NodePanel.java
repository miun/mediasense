package manager.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JPanel;

import manager.Communication;
import manager.dht.FingerEntry;

@SuppressWarnings("serial")
public class NodePanel extends JPanel implements MouseListener {
	
	private Communication com;
	
	private CirclePanel myFingers;
	private HashMap<FingerEntry,Arrow> fingerData;
	
	private CircleGUI cg;

	public NodePanel(Communication com, Point p, CirclePanel myFingers, CircleGUI cg){
		this.com = com;
		this.myFingers = myFingers;
		this.fingerData = new HashMap<FingerEntry, Arrow>();
		this.cg = cg;
		
		this.setToolTipText("{" + com.getNodeID() +"}" + " (" + com.getLocalIp() +")");
		
		//Set Dimension and Color
		int x = (int) p.getX();
		int y = (int) p.getY();
		this.setBounds(x-2,y-2,5,5);
		this.setBackground(Color.WHITE);
		
		//Listen to Mouse Events
		this.addMouseListener(this);
	}
	
	public void addFinger(FingerEntry finger) {
		Arrow a = new Arrow(myFingers.getPosOnCircle(this.com.getNodeID()),
				myFingers.getPosOnCircle(finger.getNodeID()), 
				(myFingers.getCircleRadius()+CircleGUI.BORDER)*2, 
				Color.GRAY);
		myFingers.add(a);
		fingerData.put(finger, a);
		
		myFingers.validate();
		myFingers.repaint();
	}
	
	public void removeFinger(FingerEntry finger) {
		Arrow a = fingerData.get(finger);
		if(a!=null) {
			myFingers.remove(a);
		}
		myFingers.validate();
		myFingers.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {	
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		setBackground(Color.WHITE);
		cg.hideFingers(myFingers);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		setBackground(Color.RED);
		cg.showFingers(myFingers);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
		
	
}
