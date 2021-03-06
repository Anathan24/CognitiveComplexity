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
		
		printAllDetectedJavaFiles(javaFiles);
		
		System.out.println();//Questa riga server solo per separare due blocchi di testo in console
		for(File javaFile: javaFiles) {
			logger.info("Analizing {}", javaFile.getName());
			CompilationUnit compUnit = StaticJavaParser.parse(javaFile);
			String absoluteFilePath = javaFile.getAbsolutePath();
			List<ModuleInfoDAO> calculusResult = new ModuleAnalizer(absoluteFilePath).parseJavaFile(compUnit);
			calculusResults.add(calculusResult);
		}
		
		return calculusResults;
	}
	
	/**
	 * Esegue una ricerca ricorsiva per trovare tutti i java file contenuti nella cartella e/o nelle cartelle sottostanti.
	 * @param directoryOrFile può essere una cartella o semplicemente un java file
	 * @return lista contenente tutti i fava file trovati
	 */
	 
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
	
	/**
	 * Stampa in console i nomi di tutti i java file trovati nella cartella/sottocartella specificata
	 * @param javaFiles lista contenente i file trovti 
	 */
	private void printAllDetectedJavaFiles(List<File> javaFiles) {
		
		if(javaFiles != null && !javaFiles.isEmpty()) {
			logger.info("FOUND JAVA FILES: ");
			for(File file: javaFiles) {
				logger.info(file.getName());
			}
		}else {
			logger.info("No java files founded in specified directory!");
		}
	}
}
