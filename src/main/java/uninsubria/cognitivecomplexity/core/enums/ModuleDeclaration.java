package uninsubria.cognitivecomplexity.core.enums;

public enum ModuleDeclaration {

	CLASS_OR_INTERFACE_DECLARATION("ClassOrInterfaceDeclaration"),
	FIELD_DECLARATION("FieldDeclaration"),
	CONSTRUCTOR_DECLARATION("ConstructorDeclaration"),
	METHOD_DECLARATION("MethodDeclaration"),
	ENTITY_DECLARATION_NOT_FOUND("EntityDeclarationTypeNotFound");
	
	private String entity;
	
	private ModuleDeclaration(String entity) {
		this.entity = entity;
	}
	
	public String getEntityName() {
		return this.entity;
	}
	
	public static ModuleDeclaration getEntityTypeFromString(String entitySimpleName) {
		ModuleDeclaration entity = ENTITY_DECLARATION_NOT_FOUND;
		
		for(ModuleDeclaration type: ModuleDeclaration.values()) {
			if(type.getEntityName().equals(entitySimpleName)) {
				entity = type;
				break;
			}
		}
		
		return entity;
	}
}
