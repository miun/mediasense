package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.tools.JavaCompiler;

@SuppressWarnings("serial")
public class Arrow extends JComponent {
	public static final Color ADD = Color.GREEN;
	public static final Color REMOVE = Color.RED;
	
	//Coordinates
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	//Color
	Color color;
	
	public Arrow(Point start, Point end, Color color) {
		super();
		this.x1 = (int) start.getX();
		this.y1 = (int) start.getY();
		this.x2 = (int) end.getX();
		this.y2 = (int) end.getY();
		//this.setBounds(x1, y1, x2-x1, y2-y1);
		this.color = color;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics gLocal = g.create();
		gLocal.setColor(color);
		gLocal.drawLine(x1, y1, x2, y2);
		
		
	}

}
