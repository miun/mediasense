package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.JPanel;

import manager.Communication;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;
import manager.listener.FingerChangeListener;

@SuppressWarnings("serial")
public class CirclePanel extends JPanel {
	public static final byte[] MAXNUMBER = {-1,-1,-1,-1};
	public static final int RADIUS = 360;
	public static final int RADIUSNUMBER = 380;
	public static final int BORDER = 50;
	
	private HashMap<NodeID, NodePanel> nodes;
	
	private NodePanel activeNode;
	
	private double circumference;
	
	public CirclePanel() {
		nodes = new HashMap<NodeID, NodePanel>();
		circumference = 2*Math.PI/bAtoLong(MAXNUMBER);
		this.setLayout(null);
	}
	
	public void addNode(Communication com) {
		//Get the most valuable bytes of the hash of the joining node			
		long longNode = getNodeIDAsLong(com.getNodeID());
		
		//Determine cos and sin regarding to the circle
		double cos = -Math.cos(longNode*circumference);
		double sin = Math.sin(longNode*circumference);
		
		//Calculate X and Y values
		int numberX = new Double(sin*(RADIUSNUMBER)).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		int numberY =  new Double(cos*(RADIUSNUMBER)).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		int circleX = new Double(sin*RADIUS).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		int circleY = new Double(cos*RADIUS).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		
		NumberPanel nb = new NumberPanel(com.getLocalIp(), numberX, numberY);
		
		NodePanel np = new NodePanel(nb, circleX, circleY,this);
		np.setToolTipText(com.getLocalIp());
		
		//Add the panels as childs of this panel
		this.add(nb);
		this.add(np);
		this.repaint();
		
		//Add it to the list with nodes
		nodes.put(com.getNodeID(), np);
	}
	
	public void removeNode(Communication com) {
		//Remove from the HashMap and from the panel
		NodePanel toRemove = nodes.remove(com.getNodeID());
		if(toRemove != null)
			remove(toRemove.getNumberPanel());
			remove(toRemove);
		//Repaint the P
		this.repaint();
	}
	
	public static long bAtoLong(byte[] bytes) {
		long result = 0;
		for (int i = 0; i < bytes.length; i++){
			result = (result << 8) + (bytes[i] & 0xff);
		}
		return result;
	}
	
	private long getNodeIDAsLong(NodeID nodeID){
		byte[] hash = nodeID.getID();
		byte[] node = new byte[MAXNUMBER.length];
		for(int i=0;i<node.length;i++) {
			node[i] = hash[i];
		}
		return bAtoLong(node);
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		g.setColor(Color.CYAN);
		g.drawOval(BORDER, BORDER, RADIUS*2, RADIUS*2);
		g.setColor(Color.BLACK);
		if(activeNode!=null){
			for(Point p: activeNode.getFingers()) {
				g.drawLine(activeNode.getX(), activeNode.getY(), p.x, p.y);
			}
		}
	}
	
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		NodePanel np = nodes.get(node);
		if(np==null) return;
		if(changeType == FingerChangeListener.FINGER_CHANGE_ADD) {
			//Get the most valuable bytes of the hash of the new finger as long			
			long longNode = getNodeIDAsLong(finger);
			
			//Determine cos and sin regarding to the circle
			double cos = -Math.cos(longNode*circumference);
			double sin = Math.sin(longNode*circumference);
			
			int x = new Double(sin*RADIUS).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
			int y = new Double(cos*RADIUS).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
			
			np.addFinger(finger, x, y);
		}else if(changeType == FingerChangeListener.FINGER_CHANGE_REMOVE) {
			np.removeFinger(finger);
		}
	}
	
	public void setActiveNode(NodePanel np) {
		activeNode = np;
		repaint();
	}
		
}
