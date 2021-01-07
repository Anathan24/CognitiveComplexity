package uninsubria.cognitivcomplexity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;

import uninsubria.cognitivcomplexity.enums.BinaryExpressions;
import uninsubria.cognitivcomplexity.enums.LogicalOperators;
import static uninsubria.cognitivcomplexity.enums.LogicalOperators.NOT;

public class BinaryExpressionAnalizer {
	
	private static final Logger logger = LogManager.getLogger();
	private int complexityOfNotOperators = 0;
	private int sequenceComplexity = 0;

	public int calculateExpressionComplexity(Node node) {
		LogicalOperators lastOperator = null;
		
		List<LogicalOperators> operators = this.analizeExpression(node);
		
		if(operators != null) {
			if(operators.size() >= 3) 
				this.calculateComplexityForNotOpertors(operators);	
			else
				this.removeNotOperatorsIfExist(operators);
				
			for(LogicalOperators operator: operators) {
				if(operator != lastOperator) {
					sequenceComplexity +=1;
					lastOperator = operator;
				}
			}
		}
		return complexityOfNotOperators+sequenceComplexity;
	}
	
	private List<LogicalOperators> analizeExpression(Node node) {
		List<LogicalOperators> operators = null;
		
		String expressionSimpleName = node.getClass().getSimpleName();
		BinaryExpressions type = BinaryExpressions.getExpressionTypeFromString(expressionSimpleName);
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
	
	private List<LogicalOperators> analizeBinaryExpr(Node node) {
		List<LogicalOperators> result = new ArrayList<>();
		BinaryExpr binaryExpr = (BinaryExpr) node;
		BinaryExpr.Operator binaryOperator = binaryExpr.getOperator();

		List<LogicalOperators> leftOperators = this.analizeExpression(binaryExpr.getLeft());
		if(leftOperators != null)
			result.addAll(leftOperators);
		
		if(binaryOperator.equals(Operator.AND) || binaryOperator.equals(Operator.OR))
			result.add(LogicalOperators.getEnumFromString(binaryOperator.asString()));
		
		List<LogicalOperators> rightOperators = this.analizeExpression(binaryExpr.getRight());
		if(rightOperators != null) 
			result.addAll(rightOperators);
			
		return result;
	}
	
	private List<LogicalOperators> analizeUnaryExpr(Node node) {
		List<LogicalOperators> result = new ArrayList<>();
		UnaryExpr unaryExpr = (UnaryExpr)node;
		Expression expression = unaryExpr.getExpression();
		UnaryExpr.Operator unaryOperator = unaryExpr.getOperator();
		
		if(unaryOperator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)
				&& expression instanceof EnclosedExpr) 
			result.add(LogicalOperators.getEnumFromString(unaryOperator.asString()));
		
		List<LogicalOperators> nextOperators = this.analizeExpression(expression);
		if(nextOperators != null) 
			result.addAll(nextOperators);
		
		return result;
	}
	
	private List<LogicalOperators> analizeEnclosedExpr(Node node) {
		EnclosedExpr enclosedExpr = (EnclosedExpr)node;
		Expression enclosedInner = enclosedExpr.getInner();
		
		return this.analizeExpression(enclosedInner);
	}
	
	private List<LogicalOperators> calculateComplexityForNotOpertors(List<LogicalOperators> operators){

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
	
	private List<LogicalOperators> removeNotOperatorsIfExist(List<LogicalOperators> operators){
		
		for(Iterator<LogicalOperators> iterator = operators.iterator(); iterator.hasNext();) {
			LogicalOperators operator = iterator.next();
			if(operator == NOT) {
				iterator.remove();
			}
		}
		return operators;
	}
}
