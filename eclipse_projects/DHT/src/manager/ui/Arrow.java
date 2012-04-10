package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Arrow extends JComponent {
	private final int ARR_SIZE = 4;
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
		//super(); //NEVER CALL THE SUPER CONSTRUCTOR BECAUSE THEN WE HAVE A NULL PARENTNTNTNTNTNTN....night
		this.x1 = (int) start.getX();
		this.y1 = (int) start.getY();
		this.x2 = (int) end.getX();
		this.y2 = (int) end.getY();
		
		this.setBounds(0,0,600,600);
		this.color = color;
		setOpaque(false);
		this.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gLocal = (Graphics2D) g.create();
		gLocal.setColor(color);
		gLocal.drawLine(x1, y1, x2, y2);
	}

}
