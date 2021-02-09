package uninsubria.cognitivecomplexity.enums;

public enum EntityDeclaration {

	CLASS_OR_INTERFACE_DECLARATION("ClassOrInterfaceDeclaration"),
	FIELD_DECLARATION("FieldDeclaration"),
	CONSTRUCTOR_DECLARATION("ConstructorDeclaration"),
	METHOD_DECLARATION("MethodDeclaration"),
	ENTITY_DECLARATION_NOT_FOUND("EntityDeclarationTypeNotFound");
	
	private String entity;
	
	private EntityDeclaration(String entity) {
		this.entity = entity;
	}
	
	public String getEntityName() {
		return this.entity;
	}
	
	public static EntityDeclaration getEntityTypeFromString(String entitySimpleName) {
		EntityDeclaration entity = ENTITY_DECLARATION_NOT_FOUND;
		
		for(EntityDeclaration type: EntityDeclaration.values()) {
			if(type.getEntityName().equals(entitySimpleName)) {
				entity = type;
				break;
			}
		}
		
		return entity;
	}
}
