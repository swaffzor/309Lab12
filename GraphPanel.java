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
	
	JFrame graphWindow = new JFrame();
	Graphics g;
	
	public GraphPanel(GraphingCalculator gc, Double[] xval, Double[] yval, String expr) throws IllegalArgumentException {
		this.xVal = xval;
		this.yVal = yval;
		this.expression = expr;
		String [] xValString = new String [xval.length];
		
		//build the GUI
		graphWindow.setVisible(true);
		graphWindow.setTitle(expression);
		graphWindow.getContentPane().add((Component)this, "Center");
		graphWindow.setSize(400, 450);
		graphWindow.setLocation(450, 0);
		graphWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		g = graphWindow.getGraphics();
		this.addMouseListener(this);
		
		// X values as string array
		for(int i=0; i < xval.length;i++){
			xValString[i] = Double.toString(xval[i]);
		}
		
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
		int windowWidth  = graphWindow.getWidth();  // get the panel's   
	    int windowHeight = graphWindow.getHeight(); // *CURRENT* size!
	    System.out.println("Current graph size is " + windowWidth + " x " + windowHeight);
	    // Now use the instance variables and current window size to draw the graph.
	}
}
