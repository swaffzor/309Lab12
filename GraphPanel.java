import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphPanel extends JPanel implements MouseListener{
	
	int graphSize = 11;
	Double[] xVal = new Double[graphSize];
	Double[] yVal = new Double[graphSize];
	String expression;
	GraphingCalculator gc;
	String [] xValString = new String [xVal.length];
	JFrame graphWindow = new JFrame();
	Graphics g;
	
	public GraphPanel(GraphingCalculator gc, Double[] xval, Double[] yval, String expr) throws IllegalArgumentException {
		this.xVal = xval;
		this.yVal = yval;
		this.expression = expr;
		double yHighest = yval[0];
		double yLowest = yval[0];
		int yLowestint;
		int yinc;
		int ystart = 0;
		List<String> yList = new ArrayList<String>();
		
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
		// 
		for(int i=1; i <yval.length;i++){
			if (yval[i] > yHighest){
				yHighest = yval[i];
			}
			if (yval[i] < yLowest){
				yLowest = yval[i];
			}
		}
		yinc = (int) Math.round((yHighest - yLowest)/10);
		yLowestint = (int) yLowest;
		
		if (yinc > 1000){
			yinc = yinc/1000*1000;
		}
		else if(yinc > 100){
			yinc = yinc/100*100;
		}
		else if (yinc > 10){
			yinc = yinc/10*10;
		}
		if (yLowestint > 0){
			if (Math.abs(yLowestint) > 1000){
				yLowestint = yLowestint/1000*1000;
			}
			else if(Math.abs(yLowestint) > 100){
				yLowestint = yLowestint/100*100;
			}
			else if(Math.abs(yLowestint) > 10){
				yLowestint = yLowestint/10*10;
			}
			else if (Math.abs(yLowestint) > 0){
				yLowestint = 0;
			}
		}
		else{
			if ((yLowestint) < -1000){
				yLowestint = yLowestint/100*100 - yinc;
			}
			else if((yLowestint) < -100){
				yLowestint = yLowestint/10*10 - yinc;
			}
			else if((yLowestint) < -10){
				yLowestint = yLowestint/10*10 - yinc;
			}
			else if ((yLowestint) < 0){
				yLowestint = 0;
			}
		}
		
		ystart = yLowestint;
		int i = 0;
		do{
			yList.add(Integer.toString(ystart));
			ystart+=yinc;
			i++;
		}while(ystart <= yHighest+yinc);
		
		System.out.println("sy");
		
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
