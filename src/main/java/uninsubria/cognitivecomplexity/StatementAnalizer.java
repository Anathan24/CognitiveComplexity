package uninsubria.cognitivecomplexity;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import uninsubria.cognitivecomplexity.enums.Statements;

public class StatementAnalizer {
	
	private static final Logger logger = LogManager.getLogger();
	
	public StatementAnalizer() { 
		//Default Class Constructor
	}
	
	/**
	 * @param node nodo da analizzare
	 * @param nesting il livello di annidamento
	 * @return la complessita' del nodo ricevuto in ingresso
	 */
	public int analizeStatement(Node node, int nesting) {
		int complexity = 0;

		String stmtSimpleName = node.getClass().getSimpleName();
		Statements stmtType = Statements.getEnumFromString(stmtSimpleName);
			switch(stmtType) {
				case IF_STMT:
					complexity = this.analizeIfStmt(node, nesting+1);
					break;
				case DO_STMT:
					complexity = this.analizeDoStmt(node, nesting+1);
					break;
				case WHILE_STMT:
					complexity = this.analizeWhileStmt(node, nesting+1);
					break;
				case FOR_STMT:
					complexity = this.analizeForStmt(node, nesting+1);
					break;
				case FOR_EACH_STMT:
					complexity = this.analizeForEachStmt(node, nesting+1);
					break;
				case SWITCH_STMT:
					complexity = this.analizeSwitchStmt(node, nesting+1);
					break;
				case TRY_CATCH_STMT:
					complexity = this.analizeTryStmt(node, nesting+1);
					break;
				case SYNCHRONIZED_STMT:
					complexity = this.analizeSynchronizedStmt(node, nesting);
					break;	
				case LABELED_STMT:
					complexity = this.analizeLabeledStmt(node, nesting);
					break;
				case CONTINUE_STMT:
					complexity = this.analizeContinueStmt(node);
					break;
				case BREAK_STMT:
					complexity = this.analizeBreakStmt(node);
					break;
				case RETURN_STMT:
					complexity = this.analizeReturnStmt(node, nesting);
					break;
				case EXPRESSION_STMT:
					complexity = this.analizeExpressionStmt(node, nesting);
					break;
				case LOCAL_CLASS_DECLARATION_STMT:
					complexity = this.analizeLocalClassDeclarationStmt(node, nesting);
					break;
				case STATEMENT_NOT_FOUND:
					if(node instanceof Expression) {
						complexity = new ExpressionAnalizer().analizeExpression(node, nesting);
					}else{
						logger.debug("Statement Enum Type Not Found! Statement Simple Name: {}; Node: {}", stmtSimpleName, node);
					}
					break;
			}
			
		return complexity;
	}
	
	private int analizeIfStmt(Node node, int nesting) {
		int complexity = 0;
		int ifExprComplexity = 0;
		int elseComplexity = 1;
		
		IfStmt ifStmt = (IfStmt) node;
		Expression expr = ifStmt.getCondition();
		Statement thenStmt = ifStmt.getThenStmt();
		Optional<Statement> elseStmt = ifStmt.getElseStmt();

		ifExprComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr);
		if(thenStmt instanceof BlockStmt) {
			complexity += this.decomposeBlockStmt(thenStmt, nesting);
			complexity += nesting;
		}
		else { 
			complexity += this.analizeStatement(thenStmt, nesting);
			complexity += nesting;
		}
		
		if(elseStmt.isPresent()) {
			Statement stmt = elseStmt.get();
			if(stmt instanceof BlockStmt) { 
				complexity += elseComplexity;
				complexity += this.decomposeBlockStmt(stmt, nesting);
			}else{
				if(stmt instanceof IfStmt) {
					nesting--;
					complexity += this.analizeStatement(stmt, nesting)-nesting;
				}else{
					complexity += elseComplexity;
					complexity += this.analizeStatement(stmt, nesting);
				}
			}
		}

		return complexity + ifExprComplexity;
	}
	
	private int analizeDoStmt(Node node, int nesting) {
		int complexity = 0;
		int doWhileExpressionComplexity = 0;
		
		DoStmt doStmt = (DoStmt)node;
		Expression expr = doStmt.getCondition();
		Statement stmt = doStmt.getBody();
		
		doWhileExpressionComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr);
		
		if(stmt instanceof BlockStmt) 
			complexity = this.decomposeBlockStmt(stmt, nesting);
		
		return complexity + doWhileExpressionComplexity + nesting;
	}
	
	private int analizeWhileStmt(Node node, int nesting) {
		int complexity = 0;
		int whileExpressionComplexity = 0;

		WhileStmt whileStmt = (WhileStmt) node;
		Expression expr = whileStmt.getCondition();
		Statement stmt = whileStmt.getBody();

		whileExpressionComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr);
		
		if(stmt instanceof BlockStmt) 
			complexity = this.decomposeBlockStmt(stmt, nesting);
		else 
			complexity = this.analizeStatement(stmt, nesting);

		return complexity + whileExpressionComplexity + nesting;
	}
	
	private int analizeForStmt(Node node, int nesting) {
		int complexity = 0;
		int forExpressionComplexity = 0;
		
		ForStmt forStmt = (ForStmt)node;
		Optional<Expression> expr = forStmt.getCompare();
		Statement stmt = forStmt.getBody();
		
		if(expr.isPresent())
			forExpressionComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr.get());
		
		if(stmt instanceof BlockStmt) 
			complexity = this.decomposeBlockStmt(stmt, nesting);
		else 
			complexity = this.analizeStatement(stmt, nesting);
		
		return complexity + forExpressionComplexity + nesting;
	}
	
	private int analizeForEachStmt(Node node, int nesting) {
		int complexity = 0;
		
		ForEachStmt forEachStmt = (ForEachStmt)node;
		Statement stmt = forEachStmt.getBody();
		
		if(stmt instanceof BlockStmt) 
			complexity = this.decomposeBlockStmt(stmt, nesting);
		else 
			complexity = this.analizeStatement(stmt, nesting);
		
		return complexity + nesting;
	}
	
	private int analizeSwitchStmt(Node node, int nesting) {
		int complexity = 0;
		int switchExprComplexity = 0;
		
		SwitchStmt switchStmt = (SwitchStmt)node;
		Expression selector = switchStmt.getSelector();
		NodeList<SwitchEntry> entries = switchStmt.getEntries();
		
		switchExprComplexity = new ExpressionAnalizer().analizeExpression(selector, nesting);
		
		if(entries.isNonEmpty()) {
			for(SwitchEntry entry: entries) {
				NodeList<Statement> stmts = entry.getStatements();
				for(Statement stmt: stmts) {
					if(stmt instanceof BlockStmt) 
						complexity += this.decomposeBlockStmt(stmt, nesting);
					else 
						complexity += this.analizeStatement(stmt, nesting);
				}
			}
		}
		
		return complexity + switchExprComplexity + nesting;
	}
	
	private int analizeTryStmt(Node node, int nesting) {
		int complexity = 0;
		
		TryStmt tryStmt = (TryStmt)node;
		Statement tryBlockStmt = tryStmt.getTryBlock();
		NodeList<CatchClause> catchClauses = tryStmt.getCatchClauses();
		Optional<BlockStmt> finallyBlock = tryStmt.getFinallyBlock();
		
		if(tryBlockStmt instanceof BlockStmt) {
			complexity += this.decomposeBlockStmt(tryBlockStmt, nesting-1);
		}

		if(catchClauses.isNonEmpty()) {
			for(CatchClause catchClause: catchClauses) {
				Statement stmt = catchClause.getBody();
				complexity += this.decomposeBlockStmt(stmt, nesting);
				complexity += nesting;
			}
		}
		
		if(finallyBlock.isPresent()) {
			Statement stmt = finallyBlock.get();
			complexity += this.decomposeBlockStmt(stmt, nesting-1);
		}
		
		return complexity;
	}
	
	private int analizeSynchronizedStmt(Node node, int nesting) {
		int complexity = 0;
		SynchronizedStmt synchronizedStmt = (SynchronizedStmt)node;
		Statement stmt = synchronizedStmt.getBody();
	
		if(stmt instanceof BlockStmt) 
			complexity = this.decomposeBlockStmt(stmt, nesting);

		return complexity;
	}
	
	private int analizeLabeledStmt(Node node, int nesting) {
		int complexity = 0;
		LabeledStmt labeledStmt = (LabeledStmt)node;
		List<Node> childs = labeledStmt.getChildNodes();
		
		if(!childs.isEmpty())
			for(Node child: childs)
				complexity = this.analizeStatement(child, nesting);
		
		return complexity;
	}
	
	private int analizeContinueStmt(Node node) {
		int complexity = 0;
		ContinueStmt continueStmt = (ContinueStmt)node;
		Optional<SimpleName> lableName = continueStmt.getLabel();
		
		if(lableName.isPresent())
			complexity +=1;
		
		return complexity;
	}

	private int analizeBreakStmt(Node node) {
		int complexity = 0;
		BreakStmt breakStmt = (BreakStmt)node;
		Optional<SimpleName> lableName = breakStmt.getLabel();
		
		if(lableName.isPresent())
			complexity +=1;
		
		return complexity;
	}
	
	private int analizeReturnStmt(Node node, int nesting) {
		int complexity = 0;
		ReturnStmt returnStmt = (ReturnStmt)node;
		complexity = this.decomposeBlockStmt(returnStmt, nesting);

		return complexity;
	}
	
	private int analizeExpressionStmt(Node node, int nesting) {
		int complexity = 0;
		ExpressionStmt exprStmt = (ExpressionStmt)node;
		Expression expr = exprStmt.getExpression();

		complexity = new ExpressionAnalizer().analizeExpression(expr, nesting);
		
		return complexity;
	}
	
	private int analizeLocalClassDeclarationStmt(Node node, int nesting) {
		int complexity = 0;
		LocalClassDeclarationStmt localClass = (LocalClassDeclarationStmt)node;
		List<Node> localNodes = localClass.getChildNodes();
		for(Node localNode: localNodes) {
			complexity = new EntityAnalizer().analizeEntity(localNode, nesting);
		}
		return complexity;
	}
	
	protected int decomposeBlockStmt(Statement stmt, int nesting) {
		int complexity = 0;
		
		List<Node> list = stmt.getChildNodes();
		for(Node node: list) { 
			complexity += this.analizeStatement(node, nesting);
		}
		
		return complexity;
	}
}
