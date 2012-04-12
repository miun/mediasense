package manager.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.QuadCurve2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Arrow extends JComponent {
	public static final int ADD = 0;
	public static final int ADD_BETTER = 1;
	public static final int REMOVE = 2;
	public static final int REMOVE_WORSE = 3;
	public static final int PREVIEW = 4;
	
	//Coordinates
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	
	//Color and dash
	private Color color;
	private float dash[];
	
	
	public Arrow(Point start, Point end, int bounds,int type) {
		this.x1 = (int) start.getX();
		this.y1 = (int) start.getY();
		this.x2 = (int) end.getX();
		this.y2 = (int) end.getY();
		
		this.setBounds(0, 0, bounds, bounds);
				
		switch(type) {
			case ADD:
				this.color = Color.YELLOW;
				this.dash = new float[1];
				dash[0] = 2.0f;
				break;
			case ADD_BETTER:
				this.color = Color.GREEN;
				this.dash = new float[1];
				dash[0] = 2.0f;
				break;
			case REMOVE:
				this.color = Color.ORANGE;
				this.dash = new float[1];
				dash[0] = 2.0f;
				break;
			case REMOVE_WORSE:
				this.color = Color.RED;
				this.dash = new float[1];
				dash[0] = 2.0f;
				break;
			case PREVIEW:
				this.color = Color.MAGENTA;
				this.dash = null;
				break;
			default:
				this.color = Color.LIGHT_GRAY;
				this.dash = null;
				break;
		}
		
		this.setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D gLocal = (Graphics2D) g.create();
		gLocal.setColor(color);
		//gLocal.drawLine(x1, y1, x2, y2);
		gLocal.drawOval(x2-5, y2-5, 10, 10);
		gLocal.fillOval(x2-5, y2-5, 10, 10);
		
		if(dash!=null) {
		    BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
		                        10.0f, dash, 0.0f);
			gLocal.setStroke(dashed);
		}
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
