package uninsubria;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uninsubria.cognitivecomplexity.InputManager;

public class StartApplication {

	private static final Logger logger = LogManager.getLogger();
	private LinkedList<File> files = new LinkedList<>();
	private File outputDirectory;
	private String outputFileName;
	
	private StartApplication() {
		this.askForInputSpecification();
	}
	
	
	private void askForInputSpecification() {
		String responce = null;
		
		logger.info("Step 1: Get input");
		logger.info("The input can be a name of directory or a name of file, if it is located in the same project directory.");
		logger.info("Also, you can specify an absolute directory/file path on your PC.");
		
		try(BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));) {
			do{
				logger.info("Enter your input: ");
				String inputLine = rd.readLine();
				
				File directory = new File(inputLine);
				if(directory.exists()) {
					this.files.add(directory);
				}else {
					logger.info("Directory/File does not exist! \n");
				}
				//TODO da finire
				logger.info("Enter another input (S/N) ?");
				responce = rd.readLine();
				
				if(responce.equals("N") || responce.equals("n")) {
					this.asForOutputSpecification();
				}
		
			}while(responce.equals("S") || responce.equals("s"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void asForOutputSpecification() {
		String responce = null;
		
		logger.info("Step 2: Get output location.");
		logger.info("Specify where save the calculation results.");
		logger.info("You can specify a path and program will created.");
		
		try(BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));) {
			do{
				logger.info("Entre directory name/path: ");
				String inputLine = rd.readLine();
				
				File directory = new File(inputLine);
				if(directory.exists()) {
					logger.info("The directory exists and the result will be created there! \n");
					
				}else {
					directory.mkdir();
					
				}
				this.outputDirectory = directory;
				
				logger.info("Entre file name:");
				String fileName = rd.readLine();
				this.outputFileName = fileName;
				
				logger.info("Reinsert output directory and file (S/N) ?");
				responce = rd.readLine();
				
				if(responce.equals("N") || responce.equals("n")) {
					// new InputManager(files);
				}
				
			}while(responce.equals("S") || responce.equals("s"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		logger.info("START");
		logger.info("-----------------------------------------------");
		new StartApplication();
//		if(args.length > 0)
//			for(String str: args)
//				new InputManager(str);
//		else
//			logger.info("No file name received. Enter the name of the file to be processed!");
		
		logger.info("-----------------------------------------------");
		logger.info("END");
	}
}
