package uninsubria.cognitivecomplexity;

import static uninsubria.cognitivecomplexity.enums.LogicalOperator.NOT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;

import uninsubria.cognitivecomplexity.enums.BinaryExpression;
import uninsubria.cognitivecomplexity.enums.LogicalOperator;

import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;

public class BinaryExprAnalizer {
	
	private static final Logger logger = LogManager.getLogger();
	private int complexityOfNotOperators = 0;
	private int sequenceComplexity = 0;

	/**
	 * Analizza il nodo per calcolare la sua complessità. Il tipo di nodo e un'espressione binaria
	 * @param node il nodo na analizzare
	 * @return int la complessità del nodo analizzato
	 */
	public int calculateExpressionComplexity(Node node) {
		LogicalOperator lastOperator = null;
		
		List<LogicalOperator> operators = this.analizeExpression(node);
		
		if(operators != null) {
			if(operators.size() >= 3 && hasAtLeastTwoBinaryOper(operators)) 
				this.calculateComplexityForNotOpertors(operators);	
			else
				this.removeNotOperatorsIfExist(operators);
				
			for(LogicalOperator operator: operators) {
				if(operator != lastOperator) {
					sequenceComplexity +=1;
					lastOperator = operator;
				}
			}
		}
		return complexityOfNotOperators+sequenceComplexity;
	}
	
	private List<LogicalOperator> analizeExpression(Node node) {
		List<LogicalOperator> operators = null;
		
		String expressionSimpleName = node.getClass().getSimpleName();
		BinaryExpression type = BinaryExpression.getExpressionTypeFromString(expressionSimpleName);
		switch(type) {
		case BINARY_EXPR:
				operators = this.analizeBinaryExpr(node);
			break;
		case UNARY_EXPR:
				operators = this.analizeUnaryExpr(node);
			break;
		case ENCLOSED_EXPR:
				operators = this.analizeEnclosedExpr(node);
			break;
		case EXPRESSION_NOT_FOUND:
			logger.debug("Binary Expression Enum Type Not Found! Expression Simple Name: {}; Node: {}" ,expressionSimpleName, node);
			break;
		}
		return operators;
	}
	
	private List<LogicalOperator> analizeBinaryExpr(Node node) {
		List<LogicalOperator> result = new ArrayList<>();
		BinaryExpr binaryExpr = (BinaryExpr) node;
		BinaryExpr.Operator binaryOperator = binaryExpr.getOperator();

		List<LogicalOperator> leftOperators = this.analizeExpression(binaryExpr.getLeft());
		if(leftOperators != null)
			result.addAll(leftOperators);
		
		if(binaryOperator.equals(Operator.AND) || binaryOperator.equals(Operator.OR))
			result.add(LogicalOperator.getEnumFromString(binaryOperator.asString()));
		
		List<LogicalOperator> rightOperators = this.analizeExpression(binaryExpr.getRight());
		if(rightOperators != null) 
			result.addAll(rightOperators);
			
		return result;
	}
	
	private List<LogicalOperator> analizeUnaryExpr(Node node) {
		List<LogicalOperator> result = new ArrayList<>();
		UnaryExpr unaryExpr = (UnaryExpr)node;
		Expression expression = unaryExpr.getExpression();
		UnaryExpr.Operator unaryOperator = unaryExpr.getOperator();
		
		if(unaryOperator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)
				&& expression instanceof EnclosedExpr) 
			result.add(LogicalOperator.getEnumFromString(unaryOperator.asString()));
		
		List<LogicalOperator> nextOperators = this.analizeExpression(expression);
		if(nextOperators != null) 
			result.addAll(nextOperators);
		
		return result;
	}
	
	private List<LogicalOperator> analizeEnclosedExpr(Node node) {
		EnclosedExpr enclosedExpr = (EnclosedExpr)node;
		Expression enclosedInner = enclosedExpr.getInner();
		
		return this.analizeExpression(enclosedInner);
	}
	
	private List<LogicalOperator> calculateComplexityForNotOpertors(List<LogicalOperator> operators){

		for(int i = 0; i < operators.size(); i++) {
			if(operators.get(i) == NOT) {
				if(i != 0) {
					if(operators.get(i-1) == operators.get(i+1))
						complexityOfNotOperators += 1;
				}else
					if(operators.get(i+1) == operators.get(i+2)) 
						complexityOfNotOperators += 1;
			}
		}
		
		return this.removeNotOperatorsIfExist(operators);
	}
	
	private List<LogicalOperator> removeNotOperatorsIfExist(List<LogicalOperator> operators){
		
		for(Iterator<LogicalOperator> iterator = operators.iterator(); iterator.hasNext();) {
			LogicalOperator operator = iterator.next();
			if(operator == NOT) {
				iterator.remove();
			}
		}
		return operators;
	}
	
	public boolean hasAtLeastTwoBinaryOper(List<LogicalOperator> operators) {
		boolean result = false;
		int count = 0;
		
		for(int i = 0; i<operators.size(); i++) {
			if(count <= 2) {
				if(operators.get(0) != NOT) {
					count ++;
				}
			}else {
				result = true;
				break;
			}
		}

		return result;
	}
}
