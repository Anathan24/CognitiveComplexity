package uninsubria.cognitivecomplexity.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;

import uninsubria.cognitivecomplexity.core.enums.BinaryExpression;
import uninsubria.cognitivecomplexity.core.enums.LogicalOperator;

import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;

public class BinaryExprAnalizer {
	
	private static final Logger logger = LogManager.getLogger();
	private int AND_OR_SequenceComplexity = 0;
	private int NOT_OperatorComplexity = 0;

	public int calculateExpressionComplexity(Node node) {
		
		AND_OR_SequenceComplexity = new AndOrSequenceAnalizer().calculateAndOrSequenceComplexity(node);
		NOT_OperatorComplexity = new NotOperatorAnalizer().calculateNotOperatorComplexity(node);

		return AND_OR_SequenceComplexity + NOT_OperatorComplexity;
	}
	

	private class AndOrSequenceAnalizer{
		
		protected int calculateAndOrSequenceComplexity(Node node) {
			int sequenceComplexity = 0;
			LogicalOperator lastOperator = null;
			
			List<LogicalOperator> operators = this.analizeExpression(node);
			
			if(operators != null) {
				for(LogicalOperator operator: operators) {
					if(operator != lastOperator) {
						sequenceComplexity += 1;
						lastOperator = operator;
					}
				}
			}
			
			return sequenceComplexity;
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
	}
	
	
	private class NotOperatorAnalizer{
		
		private int notOperatorComplexity = 0;
		
		protected int calculateNotOperatorComplexity(Node node) {
			
			this.analizeExpression(node, LogicalOperator.OTHER);
			
			return notOperatorComplexity;
		}
		
		private void analizeExpression(Node node, LogicalOperator operator) {
			
			String expressionSimpleName = node.getClass().getSimpleName();
			BinaryExpression type = BinaryExpression.getExpressionTypeFromString(expressionSimpleName);
			switch(type) {
			case BINARY_EXPR:
					this.analizeBinaryExpr(node, operator);
				break;
			case UNARY_EXPR:
					this.analizeUnaryExpr(node, operator);
				break;
			case ENCLOSED_EXPR:
					this.analizeEnclosedExpr(node, operator);
				break;
			case EXPRESSION_NOT_FOUND:
				logger.debug("Binary Expression Enum Type Not Found! Expression Simple Name: {}; Node: {}" ,expressionSimpleName, node);
				break;
			}

		}
		
		private void analizeBinaryExpr(Node node, LogicalOperator operator) {
			BinaryExpr binaryExpr = (BinaryExpr) node;
			BinaryExpr.Operator binaryOperator = binaryExpr.getOperator();

			if(!operator.equals(LogicalOperator.OTHER)) {
				if(operator.toString().equals(binaryOperator.toString())) {
					this.notOperatorComplexity += 1;
				}
			}
			
			if(binaryOperator.equals(Operator.AND)) {
				operator = LogicalOperator.AND; 
			}else if(binaryOperator.equals(Operator.OR)) {
				operator = LogicalOperator.OR;
			}else {
				operator = LogicalOperator.OTHER;
			}
			
			this.analizeBinaryExpressionPart(binaryExpr.getLeft(), operator);
			this.analizeBinaryExpressionPart(binaryExpr.getRight(), operator);
		}
		
		private void analizeBinaryExpressionPart(Node binaryExpressionPart, LogicalOperator operator) {
			if(binaryExpressionPart instanceof UnaryExpr) {
				UnaryExpr unaryExpr = (UnaryExpr)binaryExpressionPart;
				UnaryExpr.Operator unaryOperator = unaryExpr.getOperator();
				if(unaryOperator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)) {
					this.analizeExpression(binaryExpressionPart, operator);
				}else{
					this.analizeExpression(binaryExpressionPart, LogicalOperator.OTHER);
				}
			}else{
				this.analizeExpression(binaryExpressionPart, LogicalOperator.OTHER);
			}
		}
		
		private void analizeUnaryExpr(Node node, LogicalOperator operator) {
			UnaryExpr unaryExpr = (UnaryExpr)node;
			UnaryExpr.Operator unaryOperator = unaryExpr.getOperator();
			Expression expression = unaryExpr.getExpression();

			if(unaryOperator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)) {
				this.analizeExpression(expression, operator);
			}else{
				this.analizeExpression(expression, LogicalOperator.OTHER);
			}
		}
		
		private void analizeEnclosedExpr(Node node, LogicalOperator operator) {
			EnclosedExpr enclosedExpr = (EnclosedExpr)node;
			Expression enclosedInner = enclosedExpr.getInner();

			this.analizeExpression(enclosedInner, operator);
		}
		
	}
}
