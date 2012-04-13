package manager.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.math.BigInteger;

import javax.swing.JPanel;

import manager.dht.NodeID;

@SuppressWarnings("serial")
public class CirclePanel extends JPanel {
	public static final byte[] MAXNUMBER = {-1,-1,-1,-1};
	
	private int circleRadius;
	private int border;
	Color color;
	Color scale;
	
	private Graphics2D g2D;
	
	//That defines how far two nodes can be away from each other
	private double rangeOnCircle;
	
	public CirclePanel(int circleRadius, int border, Color circle, Color scale) {
		//init
		this.circleRadius = circleRadius;
		this.border = border;
		this.color = circle;
		this.scale = scale;
		
		//Calculation stuff
		rangeOnCircle = 2*Math.PI/bAtoLong(MAXNUMBER);
		
		//Gui stuff
		this.setLayout(null);
		Dimension d = new Dimension((circleRadius+border)*2, (circleRadius+border)*2);
		this.setBounds(border, border, d.width, d.height);
		//Make Elements behind this visible
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent(g);
		this.g2D = (Graphics2D) g.create();
		Graphics gLocal = g.create();
		//color = null means dont draw additional stuff
		if(color != null) {		
			//draw the circle
			
			gLocal.setColor(color);
			gLocal.drawOval(border, border, circleRadius*2, circleRadius*2);
		}
		
		if(scale != null) {
			gLocal.setColor(scale);
			
			//init positions
			NodeID pos_current = new NodeID(BigInteger.ZERO.toByteArray());
			NodeID pos_1 = new NodeID(BigInteger.ONE.shiftLeft(156).toByteArray());
			
			for(int i = 0; i < 16; i++) {
				//get point on the circle
				Point p = getPosOnCircle(pos_current);
				//get the caption
				String posText = Integer.toString(i, 16);
				//draw the string
				int h = gLocal.getFontMetrics().getHeight()/2;
				gLocal.drawString(posText, p.x, p.y+h);
				
				//Next position
				pos_current = pos_current.add(pos_1);
			}
		}
	
	}
	
	public Point getPosOnCircle(NodeID nodeID) {
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
		
		int x = new Double(sin*circleRadius).intValue()+border+circleRadius;
		int y = new Double(cos*circleRadius).intValue()+border+circleRadius;
		return new Point(x, y);
	}
	
	public int getCircleRadius() {
		return circleRadius;
	}
	
	private static long bAtoLong(byte[] bytes) {
		long result = 0;
		for (int i = 0; i < bytes.length; i++){
			result = (result << 8) + (bytes[i] & 0xff);
		}
		return result;
	}
}
