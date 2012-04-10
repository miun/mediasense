package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import manager.Communication;
import manager.dht.NodeID;
import manager.listener.FingerChangeListener;

@SuppressWarnings("serial")
public class CirclePanel extends JPanel {
	public static final byte[] MAXNUMBER = {-1,-1,-1,-1};
	public static final int RADIUS = 250;
	public static final int RADIUSNUMBER = 270;
	public static final int BORDER = 60;
	
	private HashMap<NodeID, NodePanel> nodes;
	
	/*Timestamp with Message and List of Arrows representing all fingers changed during the
	last keep Alive initiation*/
	private String lastKeepAliveInitiation;
	private List<Arrow> changedFingersSinceLastKeepAlive;
	
	private NodePanel activeNode;
	
	private Graphics2D g2D;
	
	//That defines how far two nodes can be away from each other
	private double rangeOnCircle;
	
	public CirclePanel() {
		lastKeepAliveInitiation = "Did not receive Keepalive yet";
		nodes = new HashMap<NodeID, NodePanel>();
		changedFingersSinceLastKeepAlive = new ArrayList<Arrow>();
		rangeOnCircle = 2*Math.PI/bAtoLong(MAXNUMBER);
		this.setLayout(null);
		this.setVisible(true);
	}
	
	public void addNode(Communication com) {
		//Get Point on the circle and add border and radius
		Point pNum = getPosOnCircle(com.getNodeID(), RADIUSNUMBER);
		int xNum = (int) (pNum.getX());
		int yNum = (int) (pNum.getY());
		NumberPanel nb = new NumberPanel(com.getLocalIp(), xNum, yNum);
		
		//Get Point on the circle and add border and radius
		Point pCir = getPosOnCircle(com.getNodeID(), RADIUS);
		int xCir = (int) (pCir.getX());
		int yCir = (int) (pCir.getY());
		NodePanel np = new NodePanel(nb, xCir, yCir, this);
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
	
	private Point getPosOnCircle(NodeID nodeID, int radius) {
		//Get the most valuable bytes of the hash
		byte[] hash = nodeID.getID();
		byte[] node = new byte[MAXNUMBER.length];
		for(int i=0;i<node.length;i++) {
			node[i] = hash[i];
		}
		//Get long value
		long longNode = bAtoLong(node);
		
		//Determine cos and sin regarding to the circle
		double cos = -Math.cos(longNode*rangeOnCircle);
		double sin = Math.sin(longNode*rangeOnCircle);
		
		int x = new Double(sin*radius).intValue()+BORDER+RADIUS;
		int y = new Double(cos*radius).intValue()+BORDER+RADIUS;
		return new Point(x, y);
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent(g);
		this.g2D = (Graphics2D) g.create();
		Graphics gLocal = g.create();
		gLocal.drawString(lastKeepAliveInitiation, 10, 25);
		gLocal.setColor(Color.CYAN);
		gLocal.drawOval(BORDER, BORDER, RADIUS*2, RADIUS*2);
		gLocal.setColor(Color.BLACK);
		if(activeNode!=null){
			for(Point p: activeNode.getFingers()) {
				gLocal.drawLine(activeNode.getX(), activeNode.getY(), p.x, p.y);
			}
		}
		/*synchronized (changedFingersSinceLastKeepAlive) {
			for(Arrow a: changedFingersSinceLastKeepAlive) {
				a.paint(gLocal);
			}
		}*/
	}
	
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		NodePanel np = nodes.get(node);
		if(np==null) return;
		
		//Get the relevant points on the circle
		Point pf = getPosOnCircle(finger, RADIUS);
		Point pn = getPosOnCircle(node, RADIUS);
		
		Arrow a = null;
		if(changeType == FingerChangeListener.FINGER_CHANGE_ADD) {
						
			
			int x = (int) (pf.getX());
			int y = (int) (pf.getY());
						
			np.addFinger(finger, x, y);
			
			a = new Arrow(pn, pf, Arrow.ADD);
		}else if(changeType == FingerChangeListener.FINGER_CHANGE_REMOVE) {		
			np.removeFinger(finger);
			a = new Arrow(pn, pf, Arrow.REMOVE);
		}
		//Add KeepAlive Arrow
		synchronized (changedFingersSinceLastKeepAlive) {
			changedFingersSinceLastKeepAlive.add(a);
		}
		//Add also to this
		this.add(a);
		this.validate();
		this.repaint();
	}
	
	public void setActiveNode(NodePanel np) {
		activeNode = np;
		repaint();
	}
	
	public void OnKeepAliveEvent(Date date, NodeID key, String networkAddress) {
		synchronized (changedFingersSinceLastKeepAlive) {
			for(Arrow a: changedFingersSinceLastKeepAlive) {
				this.remove(a);
			}
		}
		lastKeepAliveInitiation = "This changed since last KA on:" + date + key + " {" + networkAddress + "}";
		this.validate();
		//this.repaint();
	}
		
}
