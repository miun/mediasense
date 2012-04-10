package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Arrow {
	public static final Color ADD = Color.GREEN;
	public static final Color REMOVE = Color.RED;
	
	//Coordinates
	private int sx;
	private int sy;
	private int ex;
	private int ey;
	
	//Color
	Color color;
	
	public Arrow(Point start, Point end, Color color) {
		this.sx = (int) start.getX();
		this.sy = (int) start.getY();
		this.ex = (int) end.getX();
		this.ey = (int) end.getY();
		this.color = color;
	}
	
	public void draw(Graphics g) {
		Color temp = g.getColor();
		g.setColor(color);
		g.drawLine(sx, sy, ex, ey);
		g.setColor(temp);
	}

}
