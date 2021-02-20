package uninsubria.cognitivecomplexity.dao;

import java.util.List;

public class ModuleInfoDAO {

	private String absoluteModulePath;
	private int modulePosition;
	private String moduleDeclaration;
	private int moduleComplexity;
	private List<ModuleInfoDAO> subModules;
	
	public ModuleInfoDAO() {
		//Default Constructor
	}
	
	public ModuleInfoDAO(String absoluteModulePath,int modulePosition, String moduleDeclaration, 
						 int moduleComplexity, List<ModuleInfoDAO> subModules) {
		this.absoluteModulePath = absoluteModulePath;
		this.modulePosition = modulePosition;
		this.moduleDeclaration = moduleDeclaration;
		this.moduleComplexity = moduleComplexity;
		this.subModules = subModules;
	}
	
	public String getAbsoluteModulePath() {
		return absoluteModulePath;
	}

	public void setAbsoluteModulePath(String absoluteModulePath) {
		this.absoluteModulePath = absoluteModulePath;
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
