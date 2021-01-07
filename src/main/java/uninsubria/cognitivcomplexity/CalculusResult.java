package uninsubria.cognitivcomplexity;

public class CalculusResult {

	private int entityPosition;
	private String entityDeclaration;
	private int entityComplexity;
	
	public CalculusResult() {
		//Default Constructor
	}
	
	public CalculusResult(int entityPosition, String entityDeclaration, int entityComplexity) {
		this.entityPosition = entityPosition;
		this.entityDeclaration = entityDeclaration;
		this.entityComplexity = entityComplexity;
	}
	
	public int getEntityPosition() {
		return entityPosition;
	}

	public void setEntityPosition(int entityPosition) {
		this.entityPosition = entityPosition;
	}

	public String getEntityDeclaration() {
		return entityDeclaration;
	}

	public void setEntityDeclaration(String entitySignature) {
		this.entityDeclaration = entitySignature;
	}

	public int getEntityComplexity() {
		return entityComplexity;
	}

	public void setEntityComplexity(int entityComplexity) {
		this.entityComplexity = entityComplexity;
	}
}
