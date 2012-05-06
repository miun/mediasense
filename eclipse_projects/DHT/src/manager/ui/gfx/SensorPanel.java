package manager.ui.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SensorPanel extends JPanel {
	private Color color;
	private int x;
	private int y;
	
	
	public SensorPanel(Point p, Color color) {
		this.color = color;
		x = (int) p.getX();
		y = (int) p.getY();
		//this.setBounds(x-10,y-10,8*networkAddress.length(),20);
		this.setBounds(x-5,y-5,10,10);
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics gLocal = g.create();
		gLocal.setColor(color);
		gLocal.drawOval(0, 0, 8, 8);
	}
}
