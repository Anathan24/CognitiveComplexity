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
	private String fileName;
	private String FILE_EXTENSION = ".java";
	private EntityAnalizer analizer = new EntityAnalizer();
	private Map<String, List<CalculusResult>> calculusResult;
	
	/**
	 * Calcola la complessita' cognitiva del file.
	 * @param fileName il nome del file da analizzare, può contenere o non contenere l'estensione de file,
	 * Se non c'e', viene aggiunto.
	 */
	public CognitiveComplexity(String fileName) {
		this.fileName = fileName;
		this.analizeJavaFile();
	}
	
	private void analizeJavaFile() {
		CompilationUnit compUnit = null;

		try {
			if(!fileName.endsWith(FILE_EXTENSION))
				fileName += FILE_EXTENSION;
			
			File file = new File(fileName);
			if(file.exists()) {
				logger.info("Creating compilation unit for {} ",fileName);
				compUnit = StaticJavaParser.parse(file);
				logger.info("START analyzing file {} ", fileName);
				calculusResult = analizer.parseJavaFile(compUnit);
				
				if(calculusResult != null) {
					logger.info("Creating CSV file with calculation results");
					new CSVGenerator(fileName, calculusResult);
					logger.info("CSV file created.");
				}
			}else {
				logger.info("File with specified name not exist! Verify that the name is correct: {} and locate file in the same directory that a project",fileName);
			}
		
		} catch (FileNotFoundException e) {
			logger.debug("File non trovato: {}; {}", fileName, e.getMessage());
		}
	}
}
