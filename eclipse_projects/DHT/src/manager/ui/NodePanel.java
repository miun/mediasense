package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class NodePanel extends JPanel {
	public static final int RADIUS = 180;
	
	public NodePanel(int x, int y){
		this.setBounds(x-2,y-2,5,5);
		this.setBackground(Color.BLACK);
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(Color.BLACK);
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(Color.RED);
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
		g.drawOval(50, 50, RADIUS*2, RADIUS*2);
		
		//drawPoint(50+RADIUS, 50, g);
	}
}
