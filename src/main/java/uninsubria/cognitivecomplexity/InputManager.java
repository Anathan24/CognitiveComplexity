package uninsubria.cognitivecomplexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import uninsubria.cognitivecomplexity.core.ModuleAnalizer;
import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;

public class InputManager {
	
	private static final Logger logger = LogManager.getLogger();
	private final String FILE_EXTENSION = ".java";
	
	public InputManager() {
		//Default Constructor
	}
	
	public List<List<ModuleInfoDAO>> executeCognitiveComplexityCalculus(File directoryOrFile) throws FileNotFoundException{
		List<List<ModuleInfoDAO>> calculusResults = new LinkedList<List<ModuleInfoDAO>>();
		
		List<File> javaFiles = this.getAllDirectoryFiles(directoryOrFile);
		for(File javaFile: javaFiles) {
			CompilationUnit compUnit = StaticJavaParser.parse(javaFile);
			String absoluteFilePath = javaFile.getAbsolutePath();
			List<ModuleInfoDAO> calculusResult = new ModuleAnalizer(absoluteFilePath).parseJavaFile(compUnit);
			calculusResults.add(calculusResult);
		}
		
		return calculusResults;
	}
	
	private List<File> getAllDirectoryFiles(File directoryOrFile){
		List<File> javaFiles = new LinkedList<>();
		
		if(directoryOrFile.isDirectory()) {
			File[] files = directoryOrFile.listFiles();
			for(File file: files) {
				List<File> subFiles = this.getAllDirectoryFiles(file);
				javaFiles.addAll(subFiles);
			}
		}else if(directoryOrFile.isFile()) { 
			if(directoryOrFile.getName().endsWith(FILE_EXTENSION)) {
				javaFiles.add(directoryOrFile);
			}
		}
			
		return javaFiles;
	}
}
