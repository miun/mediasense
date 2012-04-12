package manager.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Arrow extends JComponent {
		
	//Coordinates
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	//Color
	Color color;
	
	public Arrow(Point start, Point end, int bounds,Color color) {
		//super(); //NEVER CALL THE SUPER CONSTRUCTOR BECAUSE THEN WE HAVE A NULL PARENTNTNTNTNTNTN....night
		/*int sx = (int) start.getX();
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
			by2 = ey;
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
			by2 = ey;
			this.x1 = bx2-bx1;
			this.y1 = 0;
			this.x2 = 0;
			this.y2 = 0;
		}
		else if(sx==ex && sy<ey) {
			//vertical line up to down
			bx1 = sx;
			by1 = sy;
			bx2 = ex;
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
			bx2 = ex;
			by2 = sy;
			this.x1 = 0;
			this.y1 = by2-by1;
			this.x2 = 0;
			this.y2 = 0;
		}
		*/
		
		//this.setBounds(bx1,by1,bx2-bx1+1,by2-by1+1);
		this.setBounds(0, 0, bounds, bounds);
		this.x1 = (int) start.getX();
		this.y1 = (int) start.getY();
		this.x2 = (int) end.getX();
		this.y2 = (int) end.getY();
		this.color = color;
		this.setOpaque(false);
		//this.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gLocal = (Graphics2D) g.create();
		gLocal.setColor(color);
		//gLocal.drawLine(x1, y1, x2, y2);
		gLocal.drawOval(x2-5, y2-5, 10, 10);
		gLocal.fillOval(x2-5, y2-5, 10, 10);
		
		// create new QuadCurve2D.Float
		QuadCurve2D q = new QuadCurve2D.Float();
		// draw QuadCurve2D.Float with set coordinates
		
		q.setCurve(x1, y1, getBounds().getCenterX(), getBounds().getCenterY(), x2, y2);
		gLocal.draw(q);
		/*
		//gLocal.draw(createArrowShape(new Point(x1,y1), new Point(x2,y2)));
		AffineTransform tx = new AffineTransform();
		Line2D.Double line = new Line2D.Double(100,100,100,100);
		Polygon arrowHead = new Polygon();  
		arrowHead.addPoint(0 , 8);
		arrowHead.addPoint(-8 , -8);
		arrowHead.addPoint(8 , -8);
		double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
	    tx.translate(line.x2, line.y2);
	    tx.rotate((angle-Math.PI/2d));  
	    
	    gLocal.setTransform(tx);   
	    gLocal.fill(arrowHead);*/
	}
	/*
	public static Shape createArrowShape(Point fromPt, Point toPt) {
	    Polygon arrowPolygon = new Polygon();
	    arrowPolygon.addPoint(-6,1);
	    arrowPolygon.addPoint(3,1);
	    arrowPolygon.addPoint(3,3);
	    arrowPolygon.addPoint(6,0);
	    arrowPolygon.addPoint(3,-3);
	    arrowPolygon.addPoint(3,-1);
	    arrowPolygon.addPoint(-6,-1);


	    Point midPoint = midpoint(fromPt, toPt);

	    double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

	    AffineTransform transform = new AffineTransform();
	    transform.translate(midPoint.x, midPoint.y);
	    double ptDistance = fromPt.distance(toPt);
	    double scale = ptDistance / 12.0; // 12 because it's the length of the arrow polygon.
	    transform.scale(scale, scale);
	    transform.rotate(rotate);

	    return transform.createTransformedShape(arrowPolygon);
	}

	private static Point midpoint(Point p1, Point p2) {
	    return new Point((int)((p1.x + p2.x)/2.0), 
	                     (int)((p1.y + p2.y)/2.0));
	}*/

}
