package uninsubria.graficuserinterface;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CalculusResultWrapper {
	
	private final SimpleIntegerProperty entityPosition;
	private final SimpleStringProperty entityDeclaration;
	private final SimpleIntegerProperty entityComplexity;
	
	public CalculusResultWrapper(int entityPosition, String entityDeclaration, int entityComplexity) {
		this.entityPosition = new SimpleIntegerProperty(entityPosition);
		this.entityDeclaration = new SimpleStringProperty(entityDeclaration);
		this.entityComplexity = new SimpleIntegerProperty(entityComplexity);
	}
	
	public int getEntityPosition() {
		return entityPosition.get();
	}

	public void setEntityPosition(int entityPosition) {
		this.entityPosition.set(entityPosition);
	}

	public String getEntityDeclaration() {
		return entityDeclaration.get();
	}

	public void setEntityDeclaration(String entitySignature) {
		this.entityDeclaration.set(entitySignature);
	}

	public int getEntityComplexity() {
		return entityComplexity.get();
	}

	public void setEntityComplexity(int entityComplexity) {
		this.entityComplexity.set(entityComplexity);
	}
}
