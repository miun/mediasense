package manager.ui;

import java.awt.Graphics;

import javax.swing.JPanel;

import manager.dht.NodeID;

@SuppressWarnings("serial")
public class CirclePanel extends JPanel {
	public static final byte[] MAXNUMBER = {Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE,Byte.MAX_VALUE};
	public static final int RADIUS = 180;
	
	public CirclePanel() {
	
	}
	
	public void addPoint(byte[] hash) {
		double winkel = 2*Math.PI/bAtoLong(MAXNUMBER);
		
		byte[] node = new byte[MAXNUMBER.length];
		for(int i=0;i<node.length;i++) {
			node[i] = hash[i];
		}
		long longNode = bAtoLong(node);
		
		int x = new Double(Math.cos(longNode*winkel)*RADIUS).intValue();
		int y = new Double(Math.sin(longNode*winkel)*RADIUS).intValue();
		
		System.out.println("x: "+x+" y: "+y);
		this.add(new NodePanel(x+RADIUS+50, y+RADIUS+50));
		
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
