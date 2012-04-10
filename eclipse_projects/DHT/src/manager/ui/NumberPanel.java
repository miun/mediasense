package manager.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NumberPanel extends JPanel {
	private String networkAddress;
	private int x,y;
	public NumberPanel(String networkAddress, int x, int y) {
		this.networkAddress = networkAddress;
		this.x = x;
		this.y = y;
		this.setBounds(x,y,100,100);
		//this.setOpaque(false);
	}
	
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
			
		int h = g.getFontMetrics().getHeight();
		int w = g.getFontMetrics().charsWidth(networkAddress.toCharArray(), 0, networkAddress.length());
		setBounds(x-w/2,y-h/2,w,h);	
		
		g.drawString(networkAddress,0,h);
	}
}
