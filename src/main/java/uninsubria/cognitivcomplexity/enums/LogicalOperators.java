package uninsubria.cognitivcomplexity.enums;

public enum LogicalOperators {
	AND("&&"),
	OR("||"),
	NOT("!");

	private String operator;
	
	private LogicalOperators(String operator) {
		this.operator = operator;
	}
	
	public String getOperator() {
		return this.operator;
	}
	
	public static LogicalOperators getEnumFromString(String logicalOperator) {
		LogicalOperators operator = null;
		
		for(LogicalOperators type: LogicalOperators.values()) {
			if(type.operator.equals(logicalOperator)) {
				return type;
			}
		}
		return operator;
	}
}
