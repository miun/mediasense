package manager.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PaintingSurface extends JPanel {
	
	public PaintingSurface() {
		this.setLayout(null);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		super.paintComponent(g);
	}
}
