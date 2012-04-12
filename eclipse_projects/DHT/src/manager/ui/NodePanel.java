package manager.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import manager.Communication;
import manager.dht.FingerEntry;

@SuppressWarnings("serial")
public class NodePanel extends JPanel implements MouseListener {
	
	private Communication com;
	
	private CirclePanel myFingers;

	public NodePanel(Communication com, Point p, CirclePanel myFingers){
		this.com = com;
		this.myFingers = myFingers;
		
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
		myFingers.add(new Arrow(myFingers.getPosOnCircle(this.com.getNodeID()),
						myFingers.getPosOnCircle(finger.getNodeID()), 
						(myFingers.getCircleRadius()+CircleGUI.BORDER)*2, 
						Color.GRAY));
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
