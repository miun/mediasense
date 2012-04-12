package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NumberPanel extends JPanel {
	private String networkAddress;
	
	public NumberPanel(String networkAddress, Point p) {
		this.networkAddress = networkAddress;
		int x = (int) p.getX();
		int y = (int) p.getY();
		this.setBounds(x-10,y-10,8*networkAddress.length(),20);
		this.setOpaque(false);
	}
	
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics gLocal = g.create();
		gLocal.setColor(Color.white);
		int h = gLocal.getFontMetrics().getHeight();
		gLocal.drawString(networkAddress,0,h);
	}
}
