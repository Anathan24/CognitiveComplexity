package uninsubria.cognitivcomplexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class CognitiveComplexity {
	
	private static final Logger logger = LogManager.getLogger();
	private String path;
	private EntityAnalizer analizer = new EntityAnalizer();
	private Map<String, List<CalculusResult>> calculusResult;
	
	public CognitiveComplexity(String path) {
		this.path = path;
		this.parseJavaFile();
	}
	
	private void parseJavaFile() {
		CompilationUnit compUnit = null;
		
		try {
			compUnit = StaticJavaParser.parse(new File(path));
			calculusResult = analizer.analizeJavaFile(compUnit);
			
		} catch (FileNotFoundException e) {
			logger.debug("File Not Found on path: {}; {}", path, e.getMessage());
		}
	}
	
	public Map<String, List<CalculusResult>> getCalculusResult() {
		return this.calculusResult;
	}
}
