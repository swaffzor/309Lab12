import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GraphingCalculator implements ActionListener {
	
	private JFrame calcWindow = new JFrame("Calculator");
	private String newLine = System.lineSeparator(); 
	private JTextField inputField = new JTextField();
	private JTextField errorField = new JTextField();
	private JTextField variableField = new JTextField();
	private JTextField incrementsField = new JTextField();
	private JTextArea logAreaField = new JTextArea();
	private JLabel inputLabel = new JLabel("Input Expression:");
	private JLabel errorLabel = new JLabel("Error:");
	private JLabel variableLabel = new JLabel("For x:");
	private JLabel incrementsLabel = new JLabel("with X increments of:");
	private JLabel logAreaLabel = new JLabel("Log:");
	private JScrollPane logScrollPane = new JScrollPane(logAreaField);
	private String[] operators = {"(", "^", "r", "*", "/", "+", "-"};
	private int[] 	 priority =  { 3,   2,   2,   1,   1,   0,   0 };
	//						       0    1    2    3    4    5    6
	List<String> operatorList = Arrays.asList(operators);
	
	boolean graphMode = false;
	
	public GraphingCalculator() {
		calcWindow.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1.0;
		c.weighty = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		calcWindow.add(logAreaLabel, c);
		
		c.gridy = 1;
		c.ipady = 200;
		calcWindow.add(logScrollPane, c);
		
		c.gridy = 2;
		c.weighty = 0;
		c.ipady = 0;
		calcWindow.add(inputLabel, c);
		
		c.gridy = 3;
		calcWindow.add(inputField, c);
		
		c.gridy = 4;
		c.gridwidth = 1;
		calcWindow.add(variableLabel, c);
		
		c.gridy = 4;
		c.gridx = 1;
		calcWindow.add(incrementsLabel, c);

		c.gridy = 5;
		c.gridx = 0;
		calcWindow.add(variableField, c);
		
		c.gridy = 5;
		c.gridx = 1;
		calcWindow.add(incrementsField, c);
		
		c.gridy = 6;
		c.gridx = 0;
		c.gridwidth = 2;
		calcWindow.add(errorLabel, c);
		
		c.gridy = 7;
		c.ipady = 50;
		calcWindow.add(errorField, c);
		
		logAreaField.setEditable(false);
		errorField.setEditable(false);
		errorField.setBackground(Color.pink);
		
		calcWindow.setSize(400, 450);
		calcWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calcWindow.setVisible(true);
		
		inputField.addActionListener(this);
		variableField.addActionListener(this);
		incrementsField.addActionListener(this);
		
	}

	public static void main(String[] args) {
		System.out.println("ECE 309 - Fall 2016 - Lab 12");
		System.out.println("Team Members: Jeremy Swafford, Keshav Patel, Jacquelynn Drahuse");
		new GraphingCalculator();

	}

	@Override
	public void actionPerformed(ActionEvent ae) {	
		try {
			String originalExpression = inputField.getText().trim().toLowerCase();
			String variable = variableField.getText().trim();
			
			inputField.setText(originalExpression);
			
			
			/* Checking validity of argument */
			expressionsNotEmpty(originalExpression);
			checkForInvalidCharacters(originalExpression, variable);
			originalExpression = removeEquals(originalExpression);
			String expression = originalExpression;
			
			// Replaces Variable
			expression = variableSubstitution(expression, variable);
			
			// More Validity checks: 
			expression = addUnary(expression);
		
			checkForPositiveUnary(expression);
			parenthesesCheck(expression);
			expressionOperatorsValid(expression);
			
			//validate increments field
			validateIncrements();
			
			
			/* Solving Expression */
			
			System.out.println("Your expression: " + expression);
			
			// Calls Solve Function
			expression = complexSolve(expression);
			expression = expression.replaceAll("n", "-");
			
			if (!(variable.equals("")) && !graphMode){
				logAreaField.append(newLine + originalExpression + " = " + expression + " for x = " + variable);
			
			} else if(graphMode){
				int graphSize = 11;
				Double[] xVal = new Double[graphSize];
				Double[] yVal = new Double[graphSize];
				Double var = Double.parseDouble(variable);
				
				xVal[0] = Double.parseDouble(variableField.getText());
				yVal[0] = Double.parseDouble(expression);
				logAreaField.append(newLine + originalExpression + " = " + yVal[0] + " for x = " + xVal[0]);
				
				for(int i=1; i<graphSize; i++){
					var = var+Double.parseDouble(incrementsField.getText());
					String newVar = Double.toString(var);
					expression = variableSubstitution(originalExpression, newVar);
					expression = addUnary(expression);
					System.out.println("Your expression: " + expression);
					expression = complexSolve(expression);
					expression = expression.replaceAll("n", "-");

					xVal[i] = xVal[i-1] + Double.parseDouble(incrementsField.getText());
					yVal[i] = Double.parseDouble(expression);
					System.out.println("answer: " + expression);
					
					logAreaField.append(newLine + originalExpression + " = " + yVal[i] + " for x = " + xVal[i]);
				}
				GraphPanel gp = new GraphPanel(this, xVal, yVal, originalExpression);
			
			} else{
				logAreaField.append(newLine + originalExpression + " = " + expression);
			}
			
			errorField.setText("");
			inputField.setText("");
			graphMode = false;
			
		} catch (Exception e) {
			String message = e.getMessage();
			errorField.setText(message);
		}
	}
	
	/*
	 * FUNCTIONS FOR SOLVING EXPRESSION
	 */
	
	private String complexSolve(String expression){
		while(true){
			if (countOperators(expression) > 1){
				String innerExpression = handleParentheses(expression);
				int theOp = getOperator(innerExpression);
				String tempExpression = splitExpression(innerExpression, operators[theOp]);
				String result = complexSolve(tempExpression);
				int thing = expression.indexOf(tempExpression);
				String replacement = expression.substring(0, thing) + result + expression.substring(thing + tempExpression.length());
				expression = replacement;
				expression = removeParenthes(expression);
				System.out.println(expression);
			} else if (countOperators(expression) == 0){
				return expression;
			} else {
				String result = simpleSolve(expression);
				String replacement = expression.replace(expression, result);
				return replacement;
			}
		}
	}
	
	
	private int getOperator(String expression){
		int theOperator = -1;
		boolean stop = false;
		int highestPriority = -1;
		int count = 0;
		int opCount = countOperators(expression);
		Integer[] opIndexList = new Integer[opCount];
		Integer[] opPriorityList = new Integer[opCount];
		
		for(int i=0; i<expression.length(); i++){
			if(stop) break;
			for(int j=0; j<operators.length; j++){
				if(expression.charAt(i) == operators[j].charAt(0)){
					opIndexList[count] = j;
					opPriorityList[count++] = priority[j];
					break;
				}
			}
		}
		
		for(int i=0; i<opCount; i++){
			if(opPriorityList[i] > highestPriority){
				highestPriority = opPriorityList[i];
				theOperator = opIndexList[i];
			}
		}
		
		
		return theOperator;
	}
	
	
	private String splitExpression(String expression, String theOperator){
		int opPos, startPos = 0, endPos = 0;
		opPos = expression.indexOf(theOperator);
		
		//get starting position
		for(int i=opPos-1; i>-1; i--){
			if(operatorList.contains(expression.substring(i, i+1))){
				startPos = i+1;
				break;
			}
		}
		if(startPos == -1) startPos = 0;
		
		//get ending position
		for(int i=opPos+1; i<expression.length(); i++){
			if(i+1 > expression.length()) break;
			if(operatorList.contains(expression.substring(i, i+1))){
				endPos = i;
				break;
			}
			else{
				endPos = -1;
			}
		}
		if(endPos == -1) endPos = expression.length();
			
		return expression.substring(startPos, endPos);
	}
	
	
	private int countOperators(String expression){
		int opCount = 0;
		
		for(int i=0; i<operators.length; i++){
			opCount += expression.length() - expression.replace(operators[i], "").length();
		}
		
		return opCount;
	}
	
	
	private String variableSubstitution(String expression, String variable){	
		if (expression.contains("pi")){
			String pi = Double.toString(Math.PI);
			expression = expression.replace("pi", pi);
		}
		if (expression.contains("e")){
			String e = Double.toString(Math.E);
			expression = expression.replace("e", e);
		}
		if (expression.contains("x")){
			expression = expression.replace("x", variable);
		}
		return expression;
	}
	
	
	private String simpleSolve(String expression){
		String temp = "";
		
		if(expression.contains("+")){
			temp = Add(expression);
		}
		else if(expression.contains("*")){
			temp = Multiply(expression);
		}
		else if(expression.contains("-")){
			temp = Minus(expression);
		}
		else if(expression.contains("/")){
			temp = divide(expression);
		}
		else if(expression.contains("r")){
			temp = root(expression);
		}
		else if(expression.contains("^")){
			temp = exponential(expression);
		}
		return temp;
	}
	
	
	/*
	 * SIMPLE MATH FUNCTIONS
	 */
	
	// Positive (+) Unary Check
	public void checkForPositiveUnary(String expression){
		char [] expressionArray = expression.toCharArray();
		List<Character> charList = new ArrayList<Character>();
		for (char c: expressionArray){
			charList.add(c);
		}
		
		// Checks for positive unary for first character
		if(charList.get(0) == '+'){
			if ((charList.get(1) != '*') || (charList.get(1) != '/') || (charList.get(1) != '+') ||
				(charList.get(1) != '-') || (charList.get(1) != 'r') || (charList.get(1) != '^') ||
				(charList.get(1) != ')')){
				throw new IllegalArgumentException("Illegal '+' unary operator.");
			}
		}
		
		int length = expressionArray.length;
		for(int i = 1; i<length-1; i++){
			if(charList.get(i) == '+'){
				/* Checks if it is of positive operator
				 * (if previous character is an operator and the following character isn't an operator 
				 * e.g. *+3, r+4, etc.)
				 */
				if (((charList.get(i-1) == '*') || (charList.get(i-1) == '/') || (charList.get(i-1) == '+') ||
					(charList.get(i-1) == '-') || (charList.get(i-1) == 'r') || (charList.get(i-1) == '^')) 
					&&
					((charList.get(i+1) != '*') || (charList.get(i+1) != '/') || (charList.get(i+1) != '+') ||
					(charList.get(i+1) != '-') || (charList.get(i+1) != 'r') || (charList.get(i+1) != '^'))) {
					
					throw new IllegalArgumentException("Illegal '+' unary operator.");
				} 
			}
		}
	}
	
	
	//	Adding (n) for Negative Unary
	public String addUnary(String expression){
		char [] expressionArray = expression.toCharArray();
		char temp = expressionArray[0];
		List<Integer> unaryLocation = new ArrayList<Integer>();
		List<Integer> spaceLocation = new ArrayList<Integer>();
		
		if (temp == '-'){
			if ((expressionArray[1] != '*') || (expressionArray[1] != '/') || (expressionArray[1] != '+') ||
					(expressionArray[1] != '-') || (expressionArray[1] != 'r') || (expressionArray[1] != '^') ||
					(expressionArray[1] != '(') || (expressionArray[1] != ')')){
				unaryLocation.add(0);
			}
			else if (expressionArray[1] == ' '){
				throw new IllegalArgumentException("Can't have space after negative unary");
			}
		}
		
		for(int i = 1; i < expressionArray.length; i++){
			if (expressionArray[i] == '-'){
				if ((temp == '*') || (temp == '/') || (temp == '+') || (temp == '-') || (temp == 'r') || (temp == '^') || (temp == '(')){
					if (expressionArray[i+1] == ' '){
						throw new IllegalArgumentException("Can't have space after negative unary");
					}
					unaryLocation.add(i);
				}
			}
			if(expressionArray[i] != ' '){
				temp = expressionArray[i];
			}
		}
		for(Integer j : unaryLocation){
			expression = expression.substring(0,j) + 'n' + expression.substring(j+1);
		}
		expression = removeBlanks(expression);				// gets rid of blanks
		return expression;
	}
	
	
	//	Replacing 'n' for the Negative Unary
	public String[] replaceUnary(String [] expression){
		for (int i = 0; i < expression.length; i++){
			expression[i] = expression[i].replace('n', '-');
		}
		return expression;
	}

	
	//	Finding Inner most ()
	public String handleParentheses(String expression){
		if(expression.contains("(")){
			int innerParentheses = expression.lastIndexOf('(');
			String temp = expression.substring(innerParentheses);
			String innerExpression = temp.substring(1, temp.indexOf(')'));
			return innerExpression;
		}
		return expression;
	}
	
	
	// Removing () After Inner Expression is solved
	public String removeParenthes(String expression){
		String tempExpression = handleParentheses(expression);
		tempExpression = addUnary(tempExpression);
		
		if(expression.contains("(")){
			if(countOperators(tempExpression) == 0){
				char [] expressionArray = expression.toCharArray();
				// remove )
				List<Character> charList1 = new ArrayList<Character>();
				for (char c: expressionArray){
					charList1.add(c);
				}
				
				int parenthesesLocation1 = expression.lastIndexOf("(");
				charList1.remove(parenthesesLocation1);
				
			    StringBuilder builder1 = new StringBuilder(charList1.size());
			    for(Character ch: charList1) {
			        builder1.append(ch);
			    }
			    expression = builder1.toString();
			    
			    // removing (
				expressionArray = expression.toCharArray();
				List<Character> charList2 = new ArrayList<Character>();
				for (char c: expressionArray){
					charList2.add(c);
				}
				
				int parenthesesLocation2 = expression.indexOf(')', parenthesesLocation1);
				charList2.remove(parenthesesLocation2);
				
			    StringBuilder builder2 = new StringBuilder(charList2.size());
			    for(Character ch: charList2) {
			        builder2.append(ch);
			    }
			    expression = builder2.toString();
			}
		}
		expression = addUnary(expression);
		return expression;
	}
	
	// Validate parentheses: throws exception if invalid
	public void parenthesesCheck(String expression){
		
			char [] expressionArray = expression.toCharArray();
			int leftParenCount = 0, rightParenCount = 0;
			
			for(int i = 0; i < expressionArray.length; i++){
				if (expressionArray[i] == '('){
					leftParenCount++;
					if ((i) != 0){
						if (!((expressionArray[i-1] == '*') || (expressionArray[i-1] == '/') || (expressionArray[i-1] == '+') ||
						(expressionArray[i-1] == '-') || (expressionArray[i-1] == 'r') || (expressionArray[i-1] == '^') || (expressionArray[i-1] == '('))){
							throw new IllegalArgumentException("Implicit Multiplication is not allowed");
						}
					}
				}
				if (expressionArray[i] == ')'){
					rightParenCount++;
					if (i != expressionArray.length-1){
						if (!((expressionArray[i+1] == '*') || (expressionArray[i+1] == '/') || (expressionArray[i+1] == '+') ||
						(expressionArray[i+1] == '-') || (expressionArray[i+1] == 'r') || (expressionArray[i+1] == '^') || (expressionArray[i+1] == ')'))){
							throw new IllegalArgumentException("Implicit Multiplication is not allowed");
						}
					}
				}
				if (rightParenCount > leftParenCount){
					throw new IllegalArgumentException("Invalid Parentheses operator");
				}
			}
			
			if (leftParenCount!=rightParenCount){
				throw new IllegalArgumentException("Invalid Parentheses operator");
			}
			if (expression.indexOf('(') > expression.indexOf(')')){
				throw new IllegalArgumentException("Invalid Parentheses operator");
			}
			if (expression.lastIndexOf('(') > expression.lastIndexOf(')')){
				throw new IllegalArgumentException("Invalid Parentheses operator");
			}
			
	}

	
	//	Solves exponential: ^
	public String exponential(String expression){
		String[] temp = expression.split("\\^");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		nums[0] = Double.parseDouble(temp[0]); // value to take root of
		nums[1] = Double.parseDouble(temp[1]); // nth root
		
		double rootValue = Math.pow(nums[0], nums[1]);
		
		return Double.toString(rootValue);
	}
	
	
	//	Solves root: r
	public String root(String expression){
		String[] temp = expression.split("r");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		nums[0] = Double.parseDouble(temp[0]); // value to take root of
		nums[1] = Double.parseDouble(temp[1]); // nth root
		
		double exponential = 1/nums[1];
		double rootValue = Math.pow(nums[0], exponential);
		
		return Double.toString(rootValue);
	}
	
	
	// Solves multiplication: *
	public String Multiply(String expression){
		String[] temp = expression.split("\\*");
		temp = replaceUnary(temp);
		String result;
		double[] nums = new double[2];
		double product;
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		product = nums[0] * nums[1];
		result = Double.toString(product);
		
		return result;
		
	}
		
		
	//	Solves division: /
	public String divide(String expression){
		String[] temp = expression.split("\\/");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		if(nums[1] == 0){
			throw new IllegalArgumentException("Error: Cannot devide by zero");
		}

		double dividend = nums[0] / nums[1];

		return Double.toString(dividend);
		
	}
	
		
	//	Solves addition: + 
	public String Add(String expression){
		String[] temp = expression.split("\\+");
		temp = replaceUnary(temp);
		String result;
		double[] nums = new double[2];
		double sum;
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		sum = nums[0] + nums[1];
		result = Double.toString(sum);
		
		return result;
	}

	
	//	Solves subtraction:  -
	public String Minus(String expression){
		String[] temp = expression.split("\\-");
		temp = replaceUnary(temp);
		String result;
		double[] nums = new double[2];
		double sum;
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		sum = nums[0] - nums[1];
		result = Double.toString(sum);
		
		return result;
	}
	
	
	// Checks for operators, returns true if expression still needs evaluation
	public boolean checkForOperators(String expression){
		if(expression.contains("+") || expression.contains("-") || expression.contains("r") 
		|| expression.contains("^") || expression.contains("*") || expression.contains("/")
		|| expression.contains("n") || expression.contains("(") || expression.contains(")")
		|| expression.contains("e") || expression.contains("x") || expression.contains("pi")){
			return true;
			
		} else {
			return false;
		}
	}
	
	// Checks expression and x value is not empty
	public void expressionsNotEmpty(String originalExpression){
		String expression = originalExpression.replace(" ", "");
		
		// Checks string is not empty
		if ((expression.length() == 0)){
			System.out.println("You didn't enter an expression.");
			throw new IllegalArgumentException("You didn't enter an expression.");
		}
		
		// Checks x has a value
		String variable = variableField.getText().trim();
		if (expression.contains("x")){
			if (variable.length() == 0){
				System.out.println("You didn't define your x value.");
				throw new IllegalArgumentException("You didn't define your x value.");
			}
		}
		if (variable.length() != 0){
			if (!(expression.contains("x"))){
				System.out.println("You submitted an x value without putting it in expression");
				throw new IllegalArgumentException("You submitted an x value without putting it in expression");
			}
		}
	}
	
	// Checks that operators are valid (multiple operators are not next to each other i.e. +*/ ) 
	public void expressionOperatorsValid(String expression){
		char [] expressionArray = expression.toCharArray();
		for(int i = 0; i < expressionArray.length-1 ; i++){
			char c1 = expressionArray[i];
			char c2 = expressionArray[i+1];
			if(( c1 == '/' || c1 == '+' || c1 == '-' || c1 == '*' || c1 == 'r' || c1 == '^') && 
			   ( c2 == '/' || c2 == '+' || c2 == '-' || c2 == '*' || c2 == 'r' || c2 == '^')){
				throw new IllegalArgumentException("Invalid operator in expression");
			}
		}
	}
	
	public void checkForInvalidCharacters(String expression, String variable){
		char [] expressionArray = expression.toCharArray();
		for(int i = 0; i < expressionArray.length ; i++){
			char c = expressionArray[i];
			if(!(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '0' ||
				 c == '/' || c == '+' || c == '-' || c == '*' || c == 'r' || c == '^' || c == '(' || c == ')' || c == 'n' || c == 'x' || 
				 ((c == 'p') && (expressionArray[i+1] == 'i')) || ((c == 'i') && (expressionArray[i-1] == 'p')) || c == 'e' || c == ' ' || c == '=' || c == '.')) {
				throw new IllegalArgumentException("Invalid operator or character in expression.");
			}
		}
		if (variable.contains("-")){
			if (!(variable.equals("-1") || variable.equals("-2") || variable.equals("-3") || variable.equals("-4") || variable.equals("-5") || variable.equals("-6") ||
					variable.equals("-7") || variable.equals("-8") || variable.equals("-9") || variable.equals("-0") || variable.equals("") )){
				throw new IllegalArgumentException("X must be a single digit number");
			}
		}
//		else if (!(variable.equals("1") || variable.equals("2") || variable.equals("3") || variable.equals("4") || variable.equals("5") || variable.equals("6") ||
//				variable.equals("7") || variable.equals("8") || variable.equals("9") || variable.equals("0") || variable.equals("") )){
//			throw new IllegalArgumentException("X must be a single digit number");
//		}
		
	}
	
	//	Removes blanks/spaces from expression
	public String removeBlanks(String expression){
		expression = expression.replaceAll("\\s", "");
		return expression;
	}
	
	// Remove = sign and things that follow
	public String removeEquals(String expression){
		if (expression.contains("=")){
			int equalLocation = expression.indexOf('=');
			expression = expression.substring(0, equalLocation);
		}
		return expression;
	}
	
	public void validateIncrements(){
		if(incrementsField.getText().isEmpty()) return;
		else{
			if(Double.parseDouble(incrementsField.getText()) < 0){
				throw new IllegalArgumentException("The 'Increments of x' field must be positive");
			}
			graphMode = true;
		}
	}
	
	public String graphPoint(String expression, String xValue){
		expression = variableSubstitution(expression, xValue);
		expression = addUnary(expression);
		expression = complexSolve(expression);
		expression = expression.replaceAll("n", "-");
		
		return expression;
	}

}
