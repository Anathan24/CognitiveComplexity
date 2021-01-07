package uninsubria.cognitivcomplexity.enums;

public enum EntityDeclarations {

	CLASS_OR_INTERFACE_DECLARATION("ClassOrInterfaceDeclaration"),
	FIELD_DECLARATION("FieldDeclaration"),
	CONSTRUCTOR_DECLARATION("ConstructorDeclaration"),
	METHOD_DECLARATION("MethodDeclaration"),
	ENTITY_DECLARATION_NOT_FOUND("EntityDeclarationTypeNotFound");
	
	private String entity;
	
	private EntityDeclarations(String entity) {
		this.entity = entity;
	}
	
	public String getEntityName() {
		return this.entity;
	}
	
	public static EntityDeclarations getEntityTypeFromString(String entitySimpleName) {
		EntityDeclarations entity = ENTITY_DECLARATION_NOT_FOUND;
		
		for(EntityDeclarations type: EntityDeclarations.values()) {
			if(type.getEntityName().equals(entitySimpleName)) {
				entity = type;
				break;
			}
		}
		
		return entity;
	}
}
