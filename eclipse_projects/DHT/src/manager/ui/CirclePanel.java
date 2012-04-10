package manager.ui;

import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JPanel;

import manager.Communication;
import manager.dht.NodeID;
import manager.dht.SHA1Generator;
import manager.listener.FingerChangeListener;

@SuppressWarnings("serial")
public class CirclePanel extends JPanel implements FingerChangeListener {
	public static final byte[] MAXNUMBER = {-1,-1,-1,-1};
	public static final int RADIUS = 360;
	public static final int RADIUSNUMBER = 380;
	public static final int BORDER = 50;
	
	private HashMap<NodeID, NodePanel> nodes;
	
	public CirclePanel() {
		nodes = new HashMap<NodeID, NodePanel>();
		this.setLayout(null);
	}
	
	public void addNode(Communication com) {
		//get the circumference from the circle
		double circumference = 2*Math.PI/bAtoLong(MAXNUMBER);
		
		//Get the most valuable bytes of the hash of the joining node
		byte hash[] = com.getNodeID().getID();
		byte[] node = new byte[MAXNUMBER.length];
		for(int i=0;i<node.length;i++) {
			node[i] = hash[i];
		}
		long longNode = bAtoLong(node);
		
		//Determine cos and sin regarding to the circle
		double cos = -Math.cos(longNode*circumference);
		double sin = Math.sin(longNode*circumference);
		
		//Calculate X and Y values
		int numberX = new Double(sin*(RADIUSNUMBER)).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		int numberY =  new Double(cos*(RADIUSNUMBER)).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		int circleX = new Double(sin*RADIUS).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		int circleY = new Double(cos*RADIUS).intValue()+CirclePanel.BORDER+CirclePanel.RADIUS;
		
		NumberPanel nb = new NumberPanel(com.getLocalIp(), numberX, numberY);
		
		NodePanel np = new NodePanel(nb, circleX, circleY);
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
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		g.drawOval(BORDER, BORDER, RADIUS*2, RADIUS*2);
	}

	@Override
	public void OnFingerChange(int changeType, NodeID node, NodeID finger) {
		System.out.println("Fingerchange ");
		
	}
	
	
}
