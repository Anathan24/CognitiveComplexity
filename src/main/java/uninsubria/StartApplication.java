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
	BufferedReader readLine = new BufferedReader(new InputStreamReader(System.in));
	private File directoryOrFile = null;
	private File outputDirectory = null;
	private String outputFileName = null;
	private List<List<ModuleInfoDAO>> calculusResults;
	
	private StartApplication() throws IOException {
		directoryOrFile = this.askForInputFiles();
//		this.askForOutputDirectory();
//		this.askForOutputFileName();
		calculusResults = new InputManager().executeCognitiveComplexityCalculus(directoryOrFile);
		new OutputManager("Calculus Results",calculusResults);
	}
	
	private File askForInputFiles() throws IOException {
		String responce = "N";
		File result = null;
		
		try {
			do{
				logger.info("Step 1: Enter the path to File or Directory:");
				String inputLine = readLine.readLine();
				
				File directory = new File(inputLine);
				if(directory.exists()) {
					result = directory;
				}else {
					logger.info("Directory/File does not exist!");
					logger.info("Retry (S/N) ?");
					responce = readLine.readLine();
				}
				
				if((responce.equals("N") || responce.equals("n")) && result == null) {
					throw new IOException("File or Directory not exist!");
				}
				
			}while(responce.equals("S") || responce.equals("s"));
		} catch (IOException e) {
			readLine.close();
			e.printStackTrace();
			System.exit(0);
		}
		
		return result;		
	}
	
	private void askForOutputDirectory() throws IOException {
		String responce = "N";
		
		try {
			do{
				logger.info("Step 2: Enter the path to the output directory:");
				String inputLine = readLine.readLine();
				
				File directory = new File(inputLine);
				if(directory.exists()) {
					this.outputDirectory = directory;
				}else {
					logger.info("Directory does not exist!");
					logger.info("Retry (S/N) ?");
					responce = readLine.readLine();
				}
				
				if((responce.equals("N") || responce.equals("n")) && outputDirectory == null) {
					throw new IOException("Directory not exist!");
				}
			}while(responce.equals("S") || responce.equals("s"));
		} catch (IOException e) {
			readLine.close();
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void askForOutputFileName() throws IOException {
		String responce = null;
		
		try {
			logger.info("Step 3: Enter output file name:");
			String fileName = readLine.readLine();
			
			if(!(fileName == null)) {
				this.outputFileName = fileName;
			}else {
				logger.info("File name can not be null!");
				logger.info("Retry (S/N) ?");
				responce = readLine.readLine();
			}
			
			if((responce.equals("N") || responce.equals("n")) && outputFileName == null) {
				readLine.close();
				throw new IOException("File name can not be null!");
			}
		} catch (IOException e) {
			readLine.close();
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws IOException {
		logger.info("START");
		logger.info("-----------------------------------------------");
		new StartApplication();
		logger.info("-----------------------------------------------");
		logger.info("END");
	}
}
