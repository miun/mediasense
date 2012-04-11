package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Arrow extends JComponent {
	private final int ARR_SIZE = 4;
	
	//Coordinates
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	//Color
	Color color;
	
	public Arrow(Point start, Point end, Color color) {
		//super(); //NEVER CALL THE SUPER CONSTRUCTOR BECAUSE THEN WE HAVE A NULL PARENTNTNTNTNTNTN....night
		int sx = (int) start.getX();
		int sy = (int) start.getY();
		int ex = (int) end.getX();
		int ey = (int) end.getY();
		
		int bx1,by1,bx2,by2;
		
		if(sx<ex && sy<ey) {
			//line from up left to down right
			bx1 = sx;
			by1 = sy;
			bx2 = ex;
			by2 = ey;
			this.x1 = 0;
			this.y1 = 0;
			this.x2 = bx2-bx1;
			this.y2 = by2-by1;
		}
		else if(sx<ex && sy>ey) {
			//line from down left to up right
			bx1 = sx;
			by1 = ey;
			bx2 = ex;
			by2 = sy;
			this.x1 = 0;
			this.y1 = by2-by1;
			this.x2 = bx2-bx1;
			this.y2 = 0;
		}
		else if(sx>ex && sy<ey) {
			//line from up right to down left
			bx1 = ex;
			by1 = sy;
			bx2 = sx;
			by2 = ey;
			this.x1 = bx2-bx1;
			this.y1 = 0;
			this.x2 = 0;
			this.y2 = by2-by1;
		}
		else if(sx>ex && sy>ey) {
			//line from down right to up left
			bx1 = ex;
			by1 = ey;
			bx2 = sx;
			by2 = sy;
			this.x1 = bx2-bx1;
			this.y1 = by2-by1;
			this.x2 = 0;
			this.y2 = 0;
		}
		else if(sx<ex && sy==ey) {
			//horizontal line from left to right
			bx1 = sx;
			by1 = sy;
			bx2 = ex;
			by2 = ey+1;
			this.x1 = 0;
			this.y1 = 0;
			this.x2 = bx2-bx1;
			this.y2 = 0;
		}
		else if(sx>ex && sy==ey) {
			//horizontal line from right to left
			bx1 = ex;
			by1 = sy;
			bx2 = sx;
			by2 = ey+1;
			this.x1 = bx2-bx1;
			this.y1 = 0;
			this.x2 = 0;
			this.y2 = 0;
		}
		else if(sx==ex && sy<ey) {
			//vertical line up to down
			bx1 = sx;
			by1 = sy;
			bx2 = ex+1;
			by2 = ey;
			this.x1 = 0;
			this.y1 = 0;
			this.x2 = 0;
			this.y2 = by2-by1;
		}
		else {
			//vertical down to up
			bx1 = sx;
			by1 = ey;
			bx2 = ex+1;
			by2 = sy;
			this.x1 = 0;
			this.y1 = by2-by1;
			this.x2 = 0;
			this.y2 = 0;
		}
		
		
		System.out.println("Line Bounds: "+ bx1 +" "+ + by1 +" " + bx2 + " " + by2);
		this.setBounds(bx1,by1,bx2-bx1,by2-by1);
		this.color = color;
		setOpaque(false);
		//this.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gLocal = (Graphics2D) g.create();
		gLocal.setColor(color);
		gLocal.drawLine(x1, y1, x2, y2);
		gLocal.drawOval(x2-x1-5, y2-y1-5, 10, 10);
		gLocal.fillOval(x2-x1-5, y2-y1-5, 10, 10);
	}

}
