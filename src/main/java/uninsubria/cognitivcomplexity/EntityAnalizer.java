package uninsubria.cognitivcomplexity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import uninsubria.cognitivcomplexity.enums.EntityDeclaration;

public class EntityAnalizer {
	
	private static final Logger logger = LogManager.getLogger();
	private Map<String, List<CalculusResult>> calculusResult = new HashMap<>();
	
	public EntityAnalizer() {
		//Default Class Constructor
	}
	
	/**
	 * @param compUnit unita di compilaione che contiene i nodi da analizzare
	 * @return Map contenente i risultti del calcolo
	 */
	public Map<String, List<CalculusResult>> parseJavaFile(CompilationUnit compUnit) {
		int methodNesting = -1;
		/*Estrazione di tutte le classi contenuti all'interno del file ricevuto in input.
		 * Per "Types", si intende i tipi contenuti nel file, ovvero la classe che rappresentano i loro tipi.
		 * NOTA: si prendono le classi del livello superiore!
		 */
		NodeList<TypeDeclaration<?>> types = compUnit.getTypes();
		for(TypeDeclaration<?> typeDeclaration: types) {
			List<CalculusResult> results = new LinkedList<>();
			/*Per ogni tipo estratto, si prendono i membri contenuti all'interno.
			 * Per "Members", si intende tutto ci� che pu� avere o non avere un corpo.
			 * Esempio: variabili, construttori, metodi, altri classi ecc.
			 */
			NodeList<BodyDeclaration<?>> typeMembers = typeDeclaration.getMembers();
			for(BodyDeclaration<?> bodyDeclaration: typeMembers) {
				//Calcolo della complessita' avviente solo per gli costruttori, i metodi e le classi interne
				//NOTA: i campi della classe vengono ignorati e/o eventuali strutture che si trovano fuori dai metodi/classi/costruttori.
				String entityDeclaration = this.getEntityDeclaration(bodyDeclaration);
				if(entityDeclaration != null) {
					CalculusResult result = new CalculusResult();
					result.setEntityPosition(this.getEntityPosition(bodyDeclaration));
					result.setEntityDeclaration(entityDeclaration);
					result.setEntityComplexity(this.analizeEntity(bodyDeclaration, methodNesting));
					results.add(result);
				}
			}
			/*
			 * Per ogni classe, contenuto all'interno del file ricevuto in input,
			 * si prende il nome della classe, piu' la complessita' dei suoi membri.
			 */
			calculusResult.put(typeDeclaration.getNameAsString(), results);
		}
	
		return calculusResult;
	}
	
	protected int analizeEntity(Node node, int methodNesting) {
		int complexity = 0;
		String entitySimpleName = node.getClass().getSimpleName();
		EntityDeclaration type = EntityDeclaration.getEntityTypeFromString(entitySimpleName);
		switch(type) {
		case CLASS_OR_INTERFACE_DECLARATION:
			complexity = this.analizeClassOrInterfaceDeclaration(node, methodNesting);
			break;
		case FIELD_DECLARATION:
			//Analisi dei campi della classe
			break;
		case CONSTRUCTOR_DECLARATION:
			complexity = this.analizeConstructorDeclaration(node, methodNesting+1);
			break;
		case METHOD_DECLARATION:
			complexity = this.analizeMethodDeclaration(node, methodNesting+1);
			break;
		case ENTITY_DECLARATION_NOT_FOUND:
			if(node instanceof Statement) {
				complexity = new StatementAnalizer().analizeStatement(node, methodNesting);
			}else {
				logger.debug("Entity Enum Type Not Found! Entity Simple Name: {}; Node: {}", entitySimpleName, node);
			}
			break;
		}
		
		return complexity;
	}
	
	private int analizeClassOrInterfaceDeclaration(Node node, int methodNesting) {
		int complexity = 0;
		ClassOrInterfaceDeclaration classOrInterFace = (ClassOrInterfaceDeclaration)node;
		NodeList<BodyDeclaration<?>> classBody = classOrInterFace.getMembers();
		for(BodyDeclaration<?> entity: classBody) {
			complexity += this.analizeEntity(entity, methodNesting);
		}
		
		return complexity;
	}
	
	private int analizeConstructorDeclaration(Node node, int methodNesting) {
		int complexity = 0;
		int stmtNesting = methodNesting;
		ConstructorDeclaration constructor = (ConstructorDeclaration)node;
		BlockStmt constructorBody = constructor.getBody();
		complexity = new StatementAnalizer().decomposeBlockStmt(constructorBody, stmtNesting);
		
		return complexity;
	}
	
	private int analizeMethodDeclaration(Node node, int methodNesting) {
		int complexity = 0;
		int stmtNesting = methodNesting;
		MethodDeclaration methodDeclaration = (MethodDeclaration)node;
		Optional<BlockStmt> methodBody = methodDeclaration.getBody();
		if(methodBody.isPresent()) {
			complexity = new StatementAnalizer().decomposeBlockStmt(methodBody.get(), stmtNesting);
		}
		return complexity;
	}
	
	private String getEntityDeclaration(BodyDeclaration<?> bodyDeclaration) {
		String entityDeclaration = null;
		
		if(bodyDeclaration.isClassOrInterfaceDeclaration()) {
			ClassOrInterfaceDeclaration classOrInterFace = (ClassOrInterfaceDeclaration)bodyDeclaration;
			entityDeclaration = classOrInterFace.getNameAsString();
		}
		
		if(bodyDeclaration.isConstructorDeclaration()) {
			ConstructorDeclaration constructor = (ConstructorDeclaration)bodyDeclaration;
			entityDeclaration = constructor.getDeclarationAsString(true, false, true);
		}
		
		if(bodyDeclaration.isMethodDeclaration()) {
			MethodDeclaration method = (MethodDeclaration)bodyDeclaration;
			entityDeclaration = method.getDeclarationAsString(true, false, true);
		}
		
		return entityDeclaration;
	}
	
	private int getEntityPosition(BodyDeclaration<?> bodyDeclaration) {
		int line = -1;
		Optional<Position> position = bodyDeclaration.getBegin();
		if(position.isPresent())
			line = position.get().line;
		
		return line;
	}
}
