package manager.ui;

import java.awt.Graphics;
import java.awt.Point;

public class Arrow {
	private int sx;
	private int sy;
	private int ex;
	private int ey;
	public Arrow(Point start, Point end) {
		this.sx = (int) start.getX();
		this.sy = (int) start.getY();
		this.ex = (int) end.getX();
		this.ey = (int) end.getY();
	}
	
	public void draw(Graphics g) {
		g.drawLine(sx, sy, ex, ey);
	}

}
