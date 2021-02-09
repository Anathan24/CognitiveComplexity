package uninsubria.cognitivecomplexity.dao;

import java.util.List;

public class ModuleInfoDAO {

	private int modulePosition;
	private String moduleDeclaration;
	private int moduleComplexity;
	private List<ModuleInfoDAO> subModules;
	
	public ModuleInfoDAO() {
		//Default Constructor
	}
	
	public ModuleInfoDAO(int modulePosition, String moduleDeclaration, 
						 int moduleComplexity, List<ModuleInfoDAO> subModules) {
		this.modulePosition = modulePosition;
		this.moduleDeclaration = moduleDeclaration;
		this.moduleComplexity = moduleComplexity;
		this.subModules = subModules;
	}
	
	public int getModulePosition() {
		return modulePosition;
	}

	public void setModulePosition(int modulePosition) {
		this.modulePosition = modulePosition;
	}

	public String getModuleDeclaration() {
		return moduleDeclaration;
	}

	public void setModuleDeclaration(String moduleDeclaration) {
		this.moduleDeclaration = moduleDeclaration;
	}

	public int getModuleComplexity() {
		return moduleComplexity;
	}

	public void setModuleComplexity(int moduleComplexity) {
		this.moduleComplexity = moduleComplexity;
	}
	
	public List<ModuleInfoDAO> getSubModules() {
		return subModules;
	}

	public void setSubModules(List<ModuleInfoDAO> subModules) {
		this.subModules = subModules;
	}
}
