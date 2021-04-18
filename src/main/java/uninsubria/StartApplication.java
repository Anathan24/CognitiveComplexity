package uninsubria;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uninsubria.cognitivecomplexity.InputManager;
import uninsubria.cognitivecomplexity.OutputManager;
import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;

public class StartApplication {

	private static final Logger logger = LogManager.getLogger();
	private File directoryOrFile = null;
	private File outputDirectory = null;
	private List<List<ModuleInfoDAO>> calculusResults;
	
	private StartApplication(String directoryOrFilePath, String outputDirectoryPath, String outputFileName) throws IOException {
		directoryOrFile = checkExistingInputDirectory(directoryOrFilePath);
		outputDirectory = checkexistingOutputDirectory(outputDirectoryPath);
		
		System.out.println();//Questa riga serve solo per serapare due blocchi di testo in console
		calculusResults = new InputManager().executeCognitiveComplexityCalculus(directoryOrFile);
		System.out.println();//Questa riga serve solo per serapare due blocchi di testo in console
		if(calculusResults != null && !calculusResults.isEmpty()) {
			new OutputManager(outputDirectory.getAbsolutePath(), outputFileName, calculusResults);
			logger.info("CSV file was created with name: {}, in directory: {}", outputFileName, outputDirectory);
		}else {
			logger.info("CSV file was not created because no java files founded!");
		}
	}
	
	private File checkExistingInputDirectory(String directoryOrFilePath) throws IOException {
		File result = null;
		File directoryOrJavaFile = new File(directoryOrFilePath);
		if(directoryOrJavaFile.isDirectory()) {
			result = directoryOrJavaFile;
		}else if(directoryOrJavaFile.isFile()? directoryOrJavaFile.getName().endsWith(".java") : false){
			result = directoryOrJavaFile;
		}else {
			logger.info("Execution Stopped! An error has occurred");
			logger.info("If you try to analyze a java file, check the name of file ends with file extension(.java)");
			logger.info("If you try to analize a directory, check the directory exist or the path is written correctly");
			System.out.println();
			throw new IOException("File or Directory does not exist! - "+directoryOrFilePath);
		}
		
		return result;		
	}
	
	private File checkexistingOutputDirectory(String outputDirectoryPath) throws IOException {
		File result = null;
		
		File directory = new File(outputDirectoryPath);
		if(directory.exists()) {
			result = directory;
		}else {
			logger.info("Execution Stopped! An error has occurred");
			throw new IOException("Directory does not exist! - "+outputDirectoryPath);
		}
		
		return result;
	}
	
	
	public static void main(String[] args) throws IOException  {
		try {
			logger.info("START");
			if(args.length < 3) {
				logger.info("Execution Stopped! An error has occurred: application must receive three arguments in input!");
				logger.info("First one: the path to file/directory to analize");
				logger.info("Secone one: the path to output directory in which to create the result file");
				logger.info("Third one: the name of output csv file that will be contain a calculation results");
				System.out.println();
				throw new IOException("Insufficient arguments amount!");
			}else {
				new StartApplication(args[0], args[1], args[2]);
			}
			
			logger.info("END");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
