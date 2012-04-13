package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NumberPanel extends JPanel {
	private static final int LABELHEIGHT = 15;
	private String networkAddress;
	
	private int labelLength;
	
	public NumberPanel(String networkAddress, Point p, int radiusDifference) {
		this.networkAddress = networkAddress;
		int x = (int) p.getX();
		int y = (int) p.getY();
		//this.setBounds(x-10,y-10,8*networkAddress.length(),20);
		this.labelLength = 8*networkAddress.length();
		this.setBounds(x-radiusDifference-labelLength/2,y-LABELHEIGHT/2-radiusDifference,labelLength,LABELHEIGHT);
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics gLocal = g.create();
		gLocal.setColor(Color.white);
		int h = gLocal.getFontMetrics().getHeight();
		gLocal.drawString(networkAddress,0,h);
	}
}
