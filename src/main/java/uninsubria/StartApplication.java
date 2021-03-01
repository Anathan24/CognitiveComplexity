package uninsubria;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uninsubria.cognitivecomplexity.InputManager;
import uninsubria.cognitivecomplexity.OutputManager;
import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;


public class StartApplication {

	private static final Logger logger = LogManager.getLogger();
	private static final BufferedReader readLine = new BufferedReader(new InputStreamReader(System.in));
	private File directoryOrFile = null;
	private File outputDirectory = null;
	private String outputFileName = null;
	private List<List<ModuleInfoDAO>> calculusResults;
	
	private StartApplication() throws IOException {
		directoryOrFile = this.askForInputFiles();
		outputDirectory = this.askForOutputDirectory();
		outputFileName = this.askForOutputFileName();
		calculusResults = new InputManager().executeCognitiveComplexityCalculus(directoryOrFile);
		new OutputManager(outputDirectory.getAbsolutePath(), outputFileName, calculusResults);
	}
	
	private File askForInputFiles() throws IOException {
		String responce = null;
		File result = null;
		
		try {
			do{
				responce = "N";
				logger.info("Step 1: Enter the path to File or Directory:");
				String inputLine = readLine.readLine();
				
				File directoryOrJavaFile = new File(inputLine);
				if(directoryOrJavaFile.exists()) {
					result = directoryOrJavaFile;
				}else if(directoryOrJavaFile.isFile()? directoryOrJavaFile.getName().endsWith(".java") : false){
					result = directoryOrJavaFile;
				}else {
					logger.info("Directory/File does not exist!");
					logger.info("Retry (S/N) ?");
					responce = readLine.readLine();
				}
				
				if((responce.equals("N") || responce.equals("n")) && result == null) {
					throw new IOException("Specified Directory/File does not exist! If you try to analyze a java file, check that the name of file end with file extension(.java)");
				}
				
			}while(responce.equals("S") || responce.equals("s"));
		} catch (IOException e) {
			readLine.close();
			e.printStackTrace();
			System.exit(0);
		}
		
		return result;		
	}
	
	private File askForOutputDirectory() throws IOException {
		String responce = null;
		File result = null;
		
		try {
			do{
				responce = "N";
				logger.info("Step 2: Enter the path to the output directory:");
				String inputLine = readLine.readLine();
				
				File directory = new File(inputLine);
				if(directory.exists()) {
					result = directory;
				}else {
					logger.info("Directory does not exist!");
					logger.info("Retry (S/N) ?");
					responce = readLine.readLine();
				}
				
				if((responce.equals("N") || responce.equals("n")) && result == null) {
					throw new IOException("Specified Directory does not exist!");
				}
			}while(responce.equals("S") || responce.equals("s"));
		} catch (IOException e) {
			readLine.close();
			e.printStackTrace();
			System.exit(0);
		}
		
		return result;
	}
	
	private String askForOutputFileName() throws IOException {
		String responce = null;
		String result = null;
		
		try {
			responce = "N";
			logger.info("Step 3: Enter output file name:");
			String fileName = readLine.readLine();
			
			if(!(fileName == null)) {
				result = fileName;
			}else {
				logger.info("File name can not be null!");
				logger.info("Retry (S/N) ?");
				responce = readLine.readLine();
			}
			
			if((responce.equals("N") || responce.equals("n")) && result == null) {
				throw new IOException("FileName can not be null!");
			}
		} catch (IOException e) {
			readLine.close();
			e.printStackTrace();
			System.exit(0);
		}
		
		return result;
	}
	
	public static void main(String[] args)  {
		try {
			logger.info("START");
			logger.info("-----------------------------------------------");
			new StartApplication();
			logger.info("-----------------------------------------------");
			logger.info("END");
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				readLine.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
