package uninsubria.cognitivecomplexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import uninsubria.cognitivecomplexity.core.ModuleAnalizer;
import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;

public class InputManager {
	
	private static final Logger logger = LogManager.getLogger();
	private String absoluteFilePath;
	private String fileName;
	private final String FILE_EXTENSION = ".java";
	
	public InputManager(String fileName) {
		this.fileName = fileName;
		this.analizeJavaFile();
	}
	
	public void readDirectory(){
		
	}
	
	public void readFile() {
		
	}
	
	private void analizeJavaFile() {
		CompilationUnit compUnit = null;
		List<ModuleInfoDAO> calculusResult;
		
		try {
			if(!fileName.endsWith(FILE_EXTENSION))
				fileName += FILE_EXTENSION;
				
			File file = new File(fileName);
			
			if(file.exists()) {
				compUnit = StaticJavaParser.parse(file);
				this.absoluteFilePath = file.getAbsolutePath();
				calculusResult = new ModuleAnalizer(absoluteFilePath).parseJavaFile(compUnit);
				
				if(calculusResult != null) {
					new OutputManager(fileName, calculusResult);
				}
			}else {
				logger.info("File with specified name not exist! Verify that the name is correct: {} and locate file in the same directory that a project", fileName);
			}
		
		} catch (FileNotFoundException e) {
			logger.debug("File non trovato: {}; {}", fileName, e.getMessage());
		}
	}
}
