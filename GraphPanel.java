import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphPanel extends JPanel implements MouseListener{
	
	int graphSize = 11;
	Double[] xVal = new Double[graphSize];
	Double[] yVal = new Double[graphSize];
	String expression;
	GraphingCalculator gc;
	
	JPanel graphPanel = new JPanel();
	JFrame graphWindow = new JFrame();
	Graphics g;
	
	public GraphPanel(GraphingCalculator gc, Double[] xval, Double[] yval, String expr) throws IllegalArgumentException {
		this.xVal = xval;
		this.yVal = yval;
		this.expression = expr;
		
		//build the GUI
		graphWindow.setVisible(true);
		graphWindow.setTitle(expression);
		graphWindow.getContentPane().add((Component)this, "Center");
		graphWindow.setSize(400, 450);
		graphWindow.setLocation(450, 0);
		graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		g = graphPanel.getGraphics();
		this.addMouseListener(this);
//		paint(g);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
			
	}

	@Override
	public void mouseReleased(MouseEvent e) {
			
	}

	@Override
	public void mouseEntered(MouseEvent e) {
			
	}

	@Override
	public void mouseExited(MouseEvent e) {
			
	}
	
	@Override
	public void paint(Graphics g){ // overrides paint() in JPanel
		int windowWidth  = graphPanel.getWidth();  // get the panel's   
	    int windowHeight = graphPanel.getHeight(); // *CURRENT* size!
	    System.out.println("Current graph size is " + windowWidth + " x " + windowHeight);
	    // Now use the instance variables and current window size to draw the graph.
	}
}
