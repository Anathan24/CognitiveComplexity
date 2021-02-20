package uninsubria.cognitivecomplexity;

import java.util.LinkedList;
import java.util.List;
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

import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;
import uninsubria.cognitivecomplexity.enums.EntityDeclaration;

public class ModuleAnalizer {
	
	private static final Logger logger = LogManager.getLogger();
	
	public ModuleAnalizer() {
		//Default Class Constructor
	}
	
	/**
	 * @param compUnit unita di compilaione che contiene i nodi da analizzare
	 * @return Map contenente i risultti del calcolo
	 */
	public  List<ModuleInfoDAO> parseJavaFile(CompilationUnit compUnit) {
		int nesting = -1;
		List<ModuleInfoDAO> allFileclasses = new LinkedList<>();
		
		NodeList<TypeDeclaration<?>> types = compUnit.getTypes();
		for(TypeDeclaration<?> typeDeclaration: types) {
			int classComplexity = 0;
			ModuleInfoDAO type = new ModuleInfoDAO();
			
			List<ModuleInfoDAO> typeModules = this.analizeModuleDeclaration(typeDeclaration, nesting);
			for(ModuleInfoDAO typeModule: typeModules ) {
				classComplexity += typeModule.getModuleComplexity();
			}
			
			type.setModulePosition(typeDeclaration.getBegin().get().line);
			type.setModuleDeclaration(typeDeclaration.getNameAsString());
			type.setModuleComplexity(classComplexity);
			type.setSubModules(typeModules);
			allFileclasses.add(type);
		}
	
		return allFileclasses;
	}
	
	private List<ModuleInfoDAO> analizeModuleDeclaration(TypeDeclaration<?> moduleDeclaration, int nesting) {
		List<ModuleInfoDAO> modules = new LinkedList<>();
		
		NodeList<BodyDeclaration<?>> typeMembers = moduleDeclaration.getMembers();
		for(BodyDeclaration<?> memberBodyDeclaration: typeMembers) {
			if(memberBodyDeclaration.isClassOrInterfaceDeclaration()) {
				ModuleInfoDAO member = this.analizeSubTypeDeclaration(memberBodyDeclaration, nesting);
				modules.add(member);
			}
			
			if(memberBodyDeclaration.isConstructorDeclaration() || memberBodyDeclaration.isMethodDeclaration()) {
				ModuleInfoDAO member = this.analizeMemberDeclaration(memberBodyDeclaration, nesting);
				modules.add(member);
			}
		}
		return modules;
	}
	
	private ModuleInfoDAO analizeSubTypeDeclaration(BodyDeclaration<?> subTypeDeclaration, int nesting) {
		int innerClassComplexity = 0;
		ModuleInfoDAO moduleInfo = new ModuleInfoDAO();
		
		ClassOrInterfaceDeclaration subType = (ClassOrInterfaceDeclaration)subTypeDeclaration;
		
		moduleInfo.setModulePosition(subType.getBegin().get().line);
		moduleInfo.setModuleDeclaration(subType.getNameAsString());
		
		List<ModuleInfoDAO> modules = this.analizeModuleDeclaration(subType, nesting);
		for(ModuleInfoDAO module: modules) {
			innerClassComplexity += module.getModuleComplexity();
		}
		
		moduleInfo.setSubModules(modules);
		moduleInfo.setModuleComplexity(innerClassComplexity);
		
		return moduleInfo;
	}
	
	private ModuleInfoDAO analizeMemberDeclaration(BodyDeclaration<?> memberBodyDeclaration, int nesting) {
		ModuleInfoDAO moduleInfo = new ModuleInfoDAO();
		
		if(memberBodyDeclaration.isConstructorDeclaration()) {
			ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)memberBodyDeclaration;
			moduleInfo.setModuleDeclaration(constructorDeclaration.getDeclarationAsString(true, false, true));
		}
		
		if(memberBodyDeclaration.isMethodDeclaration()) {
			MethodDeclaration methodDeclaration = (MethodDeclaration)memberBodyDeclaration;
			moduleInfo.setModuleDeclaration(methodDeclaration.getDeclarationAsString(true, false, true));
		}
		
		moduleInfo.setModulePosition(this.getModulePosition(memberBodyDeclaration));
		moduleInfo.setModuleComplexity(this.analizeEntity(memberBodyDeclaration, nesting));
		
		return moduleInfo;
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
			logger.debug("Entity Enum Type Not Found! Entity Simple Name: {}; Node: {}", entitySimpleName, node);
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
	
	private int getModulePosition(BodyDeclaration<?> bodyDeclaration) {
		int line = -1;
		Optional<Position> position = bodyDeclaration.getBegin();
		if(position.isPresent())
			line = position.get().line;
		
		return line;
	}
}
