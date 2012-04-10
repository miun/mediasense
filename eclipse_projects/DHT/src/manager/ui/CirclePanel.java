package manager.ui;

import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JPanel;

import manager.dht.NodeID;
import manager.dht.SHA1Generator;

@SuppressWarnings("serial")
public class CirclePanel extends JPanel {
	public static final byte[] MAXNUMBER = {-1,-1,-1,-1};
	public static final int RADIUS = 180;
	
	private HashMap<String, NodePanel> nodes;
	
	public CirclePanel() {
		nodes = new HashMap<String, NodePanel>();
	}
	
	public void addPoint(String networkAddress) {
		byte hash[] = SHA1Generator.SHA1(networkAddress);
		
		double winkel = 2*Math.PI/bAtoLong(MAXNUMBER);
		
		byte[] node = new byte[MAXNUMBER.length];
		for(int i=0;i<node.length;i++) {
			node[i] = hash[i];
		}
		long longNode = bAtoLong(node);
		
		double cos = Math.cos(longNode*winkel);
		double sin = Math.sin(longNode*winkel);
		int x = new Double(cos*RADIUS).intValue();
		int y = new Double(sin*RADIUS).intValue();
		
		System.out.println("x: "+x+" y: "+y);
		NodePanel np = new NodePanel(x+RADIUS+50, y+RADIUS+50); 
		np.setToolTipText(networkAddress);
		this.add(np);
		nodes.put(networkAddress, np);
		
		//repaint
		this.repaint();
		
	}
	
	private long bAtoLong(byte[] bytes) {
		long result = 0;
		for (int i = 0; i < bytes.length; i++){
			result = (result << 8) + (bytes[i] & 0xff);
		}
		return result;
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		g.drawOval(50, 50, RADIUS*2, RADIUS*2);
	}
	
	
}
