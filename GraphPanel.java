import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GraphPanel extends JPanel implements MouseListener{
	
	int graphSize = 11;
	Double[] xVal = new Double[graphSize];
	Double[] yVal = new Double[graphSize];
	String expression;
	GraphingCalculator gc;
    double xValueToPixelsConversionFactor;
    double yValueToPixelsConversionFactor;

	String[] xValString = new String [xVal.length];
	String[] yValString = new String [yVal.length-1];	
	JFrame graphWindow = new JFrame();
	JFrame xyWindow = new JFrame();
	JTextField xTextField = new JTextField();
	JTextField yTextField = new JTextField();
	JPanel xyPanel = new JPanel();
	Graphics g;
	
	int yMin;
	int yMax;
	
	public GraphPanel(GraphingCalculator gc, Double[] xval, Double[] yval, String expr) throws IllegalArgumentException {
		this.gc = gc;
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
		
		
		xyPanel.setLayout(new GridLayout(2,1));
		xyPanel.add(xTextField);
		xyPanel.add(yTextField);
		xyWindow.getContentPane().add(xyPanel);
		xyWindow.setSize(160, 80);
		
		g = graphWindow.getGraphics();
		this.addMouseListener(this);
		
		// X values as string array
		for(int i=0; i < xval.length;i++){
			if(checkForDecimal(xval[i])){
				xValString[i] = Double.toString(Math.round(xval[i] * 100.0) / 100.0);
			}
			else{
				xValString[i] = Integer.toString(xval[i].intValue());
			}
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
//				yLowestint = 0;
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
//				yLowestint = 0;
			}
		}
		
		ystart = yLowestint;
		this.yMin = yLowestint;
		this.yMax = (int) yHighest+yinc; 
		
		int i = 0;
		do{
			yList.add(Integer.toString(ystart));
			ystart+=yinc;
			i++;
		}while(ystart <= yHighest+yinc);
		
		yValString =  (String[]) yList.toArray(yValString);
		for(int ii=0; ii<yValString.length; ii++){
			if(yValString[ii] == null){
				yValString[ii] = "";
			}
		}
		
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		int windowWidth  = e.getX();  // get the panel's   
	    int windowHeight = e.getY(); // *CURRENT* size!
	    System.out.println("Current click   is at " + windowWidth + " x " + windowHeight);
	    
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// xTextField and yTextField are in the mini displayXYpairWindow
	    double xInPixels = me.getX() - 50;
	    double xValue = (xInPixels / xValueToPixelsConversionFactor)*(xVal[1]-xVal[0]) + xVal[0];
	    String xValueString = String.valueOf(xValue);
	    xTextField.setText("X = " + xValueString);
	  
	    String yValueString = this.gc.graphPoint(this.expression,xValueString); 
	    yTextField.setText("Y = " + yValueString);

	    // show mini x,y display window
	    xyWindow.setLocation(this.graphWindow.getX() + me.getX(), this.graphWindow.getY() + me.getY());
	    xyWindow.setVisible(true); 
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		// "erase" mini x,y display window	
	    xyWindow.setVisible(false);
		
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
	    int xPixelCoord[] = new int[11];
	    System.out.println("Current graph size is " + windowWidth + " x " + windowHeight);
	    // Now use the instance variables and current window size to draw the graph.
	    
	    int theWidth = this.getSize().width;
	    int theHeight = this.getSize().height;
	    g.drawLine(50, theHeight-50, theWidth-50, theHeight-50);	//horizontal axis
	    g.drawLine(50, theHeight-50, 50, 50);	//vertical axis
    	
//	    int xBump = (theWidth - 100)/10;
//    	int yBump = (theHeight- 100)/10;
//	    for(int i=0; i<xValString.length; i++){
//	    	xPixelCoord[i] = 50+i*xBump;
//	    	g.drawString("|", 50+i*xBump, theHeight-45);
//	    	g.drawString("-", 47, theHeight-45-(i*yBump));
//	    	g.drawString(xValString[i], 50+i*xBump, theHeight-30);
//	    	g.drawString(yValString[i], 32, theHeight-45-(i*yBump));
//	    }
	    int xBump = (theWidth - 100)/(xValString.length-1);
    	int yBump = (theHeight- 100)/(yValString.length-1);
	    for(int i=0; i<xValString.length; i++){
	    	xPixelCoord[i] = 50+i*xBump;
	    	g.drawString("|", 50+i*xBump, theHeight-45);
	    	g.drawString(xValString[i], 50+i*xBump, theHeight-30);
	    	if(xValString[i].equals("0")){
	    		g.setColor(Color.green);
		    	g.drawLine(51+i*xBump, theHeight-50, 51+i*xBump, 50);
		    	g.setColor(Color.black);
	    	}
	    }
	    for(int i=0; i<yValString.length; i++){
	    	g.drawString("-", 47, theHeight-45-(i*yBump));
	    	g.drawString(yValString[i], 32, theHeight-45-(i*yBump));
	    	if(yValString[i].equals("0")){
	    		g.setColor(Color.green);
		    	g.drawLine(50, theHeight-49-(i*yBump), theWidth-50, theHeight-49-(i*yBump));
		    	g.setColor(Color.black);
	    	}
	    }
	    
	    
	    xValueToPixelsConversionFactor = xBump;
	    yValueToPixelsConversionFactor = yBump;
	    
	    int prevY = 0;
	    int prevX = 0;
	    
	    // Draw Points on Graph
	  //  int yMin = Integer.parseInt(yValString[0]);
	  //  int yMax = Integer.parseInt(yValString[yVal.length-1]);	
	    int yValueRange = yMax-yMin;
	    
	    Double xMin = Double.parseDouble(xValString[0]);
	    Double xMax = Double.parseDouble(xValString[xVal.length-1]); 
	    int xValueRange =  xMax.intValue()-xMin.intValue();
	    
	    for(int i = 0; i < xValString.length ; i++){
	    	double yValuePercentage = (yVal[i]-yMin)/yValueRange;
	    	double xValuePercentage = (xVal[i]-xMin)/xValueRange;
	    	
	    	int xPixelCoordinate = xPixelCoord[i]-2;
	    	int yPixelCoordinate = (int) (theHeight - yValuePercentage*(theHeight - 100) - 52);
	    	
	    	System.out.println("xPointbyPixel = " + xPixelCoordinate +
	    					   " yPointbyPixel = " + yPixelCoordinate);

    		g.setColor(Color.red);
	    	g.drawOval(xPixelCoordinate, yPixelCoordinate, 6, 6);	
	    	
	    	if(i > 0){
	    		g.drawLine(prevX+3, prevY+3, xPixelCoordinate+3, yPixelCoordinate+3);
	    		g.setColor(Color.black);
	    	}
	    	prevX = xPixelCoordinate;
	    	prevY = yPixelCoordinate;
	    }
	    
	    
	    // Draw green line if there is a x=0
	    List<Double> xList = Arrays.asList(xVal);
	    List<Double> yList = Arrays.asList(yVal);
	    
	    double zero = 0; 
	    int xZeroIndex=-1;
	    if(xList.contains(zero)){
	    	xZeroIndex = xList.indexOf(zero);
	    }
	    
	    if(xZeroIndex != -1){
	    	double yZeroValue = yList.get(xZeroIndex);
	    	int yPixelHeight = (int) (theHeight - (((yZeroValue-yMin)/yValueRange) * (yBump*10)) - 50);
//	    	g.setColor(Color.green);
//	    	g.drawLine(50, yPixelHeight-50, theWidth-50, yPixelHeight-50);
	    }
	    
	    
	}
	
	private boolean checkForDecimal(Double theVal){
		Double test = theVal % 1;
		if(test < 1 && test > 0){
			return true;
		}
		else{
			return false;
		}
	}
}
