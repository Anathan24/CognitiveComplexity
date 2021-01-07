package uninsubria.cognitivcomplexity;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import uninsubria.cognitivcomplexity.enums.Expressions;

public class ExpressionAnalizer {

	private static final Logger logger = LogManager.getLogger();
	
	public ExpressionAnalizer() {
		//Default Class Constructor
	}
	
	public int analizeExpression(Node node,  int nesting) {
		int complexity = 0;
		
		String expressionSimpleName = node.getClass().getSimpleName();
		Expressions type = Expressions.getExpressionTypeFromString(expressionSimpleName);
		switch(type) {
			case VARIABLE_DECLARATION_EXPR:
				complexity = this.analizeVarDeclarationExpr(node, nesting);
				break;
			case LAMBDA_EXPR:
				complexity = this.analizeLambdaExpr(node, nesting+1);
				break;
			case METHOD_CALL_EXPR:
				complexity = this.analizeMethodCallExpr(node, nesting);
				break;
			case ASSIGN_EXPR:
				complexity = this.analizeAssignExpr(node, nesting);
				break;
			case CONDITIONAL_EXPR:
				complexity = this.analizeConditionalExpr(node, nesting+1);
				break;
			case ENCLOSED_EXPR:
				complexity = this.analizeEnclosedExpr(node, nesting);
				break;
			case OBJECT_CREATION_EXPR:
				complexity = this.analizeObjectCreationExr(node, nesting);
				break;
			case BINARY_EXPR:
				complexity = new BinaryExpressionAnalizer().calculateExpressionComplexity(node);
				break;
			case VARIABLE_DECLARATOR:
				complexity = this.analizeVariableDeclarator(node, nesting);
				break;
			case EXPRESSION_NOT_FOUND:
				if(node instanceof MethodDeclaration) {
					complexity = new EntityAnalizer().analizeEntity(node, nesting);
				}else {
					logger.debug("Expression Enum Type Not Found! Expression Simple Name: {}; Node: {}", expressionSimpleName, node);
				}
				break;
		}
		return complexity;
	}
	
	private int analizeVarDeclarationExpr(Node node,  int nesting) {
		int complexity = 0;
		VariableDeclarationExpr varDeclarationExpr = (VariableDeclarationExpr)node;
		complexity = this.decomposeExprBlock(varDeclarationExpr, nesting);

		return complexity;
	}
	
	private int analizeVariableDeclarator(Node node, int nesting) {
		int complexity = 0;
		VariableDeclarator varDeclarator = (VariableDeclarator)node;
		Optional<Expression> varDecInitializer = varDeclarator.getInitializer();
		
		if(varDecInitializer.isPresent()) {
			Expression initializer = varDecInitializer.get();
			complexity = this.analizeExpression(initializer, nesting);
		}
		
		return complexity;
	}
	
	private int analizeLambdaExpr(Node node,  int nesting) {
		int complexity = 0;
		int lambdaComplexity = 0;
		LambdaExpr lambdaExpr = (LambdaExpr)node;
		Statement lambdaBody = lambdaExpr.getBody();
		
		if(lambdaBody instanceof BlockStmt) 
			complexity = new StatementAnalizer().decomposeBlockStmt(lambdaBody, nesting);
		else 
			complexity = new StatementAnalizer().analizeStatement(lambdaBody, nesting);
	
		return complexity + lambdaComplexity;
	}
	
	private int analizeAssignExpr(Node node, int nesting) {
		int complexity = 0;
		AssignExpr assignExpr = (AssignExpr)node;
		Expression assExpr = assignExpr.getValue();
		complexity = this.analizeExpression(assExpr, nesting);
		
		return complexity;
	}
	
	private int analizeConditionalExpr(Node node, int nesting) {
		int complexity = 0;

		ConditionalExpr condExpr = (ConditionalExpr)node;
		Expression condition = condExpr.getCondition();
		
		complexity += new BinaryExpressionAnalizer().calculateExpressionComplexity(condition);
		
		return complexity + nesting;
	}
	
	private int analizeEnclosedExpr(Node node, int nesting) {
		int complexity = 0;
		EnclosedExpr enclosedExpr = (EnclosedExpr)node;
		Expression inner = enclosedExpr.getInner();
		complexity = this.analizeExpression(inner, nesting);
		
		return complexity;
	}
	
	private int analizeMethodCallExpr(Node node, int nesting) {
		int complexity = 0;
		MethodCallExpr methodCallExpr = (MethodCallExpr)node;
		complexity = this.decomposeExprBlock(methodCallExpr, nesting);
		
		return complexity;
	}
	
	private int analizeObjectCreationExr(Node node, int nesting) {
		int complexity = 0;
		ObjectCreationExpr objectCreation = (ObjectCreationExpr)node;
		complexity = this.decomposeExprBlock(objectCreation, nesting);
		
		return complexity;
	}
	
	protected int decomposeExprBlock(Node node, int nesting) {
		int complexity = 0;
		List<Node> childNodes = node.getChildNodes();
		for(Node childNode: childNodes) {
			complexity += this.analizeExpression(childNode, nesting);
		}
		
		return complexity;
	}
}
