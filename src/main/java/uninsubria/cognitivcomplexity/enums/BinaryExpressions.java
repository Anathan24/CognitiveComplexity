package uninsubria.cognitivcomplexity.enums;

public enum BinaryExpressions {
	BINARY_EXPR("BinaryExpr"),
	UNARY_EXPR("UnaryExpr"),
	ENCLOSED_EXPR("EnclosedExpr"),
	EXPRESSION_NOT_FOUND("ExpressionNotFound");
	
	private String expression;
	
	private BinaryExpressions(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return this.expression;
	}
	
	public static BinaryExpressions getExpressionTypeFromString(String expressionSimpleName) {
		BinaryExpressions expressionType = EXPRESSION_NOT_FOUND;
		
		for(BinaryExpressions type: BinaryExpressions.values()) {
			if(type.expression.equals(expressionSimpleName)) {
				expressionType = type;
			}
		}
		return expressionType;
	}
}
