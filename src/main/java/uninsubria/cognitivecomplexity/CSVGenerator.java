package uninsubria.cognitivecomplexity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;

public class CSVGenerator {

	private static final Logger logger = LogManager.getLogger();
	private String fileName = null;
	
	private List<ModuleInfoDAO> calculusResult; 
	
	/**
	 * Crea un csv file contenente i risultati dei calcoli.
	 * @param fileName il nome del file.
	 * @param calculusResult la struttura dati contenente i risultati dei calcoli.
	 */
	public CSVGenerator(String fileName, List<ModuleInfoDAO> calculusResult) {
		this.fileName = fileName;
		this.calculusResult = calculusResult;
		this.createCSVFile();
	}
	
	private void createCSVFile() {
		
		try(
				FileWriter file = new FileWriter(this.fileName+" - complexity.csv");
				CSVPrinter printer = new CSVPrinter(file, CSVFormat.DEFAULT);
			){
			printer.printRecord("Absolute Module Path", "Module Position", "Module Declaration", "Module Complexity");
			for(ModuleInfoDAO type: calculusResult) {
				List<ModuleInfoDAO> modules = type.getSubModules();
				printModule(printer, modules);
				printer.println();
			}
			printer.flush();
		} catch (IOException e) {
			logger.debug("Creation CSV File failed! {}", e.getMessage());
		}
		
	}
	
	private void printModule(CSVPrinter printer, List<ModuleInfoDAO> modules) throws IOException {
		
		if(modules != null) 
			for(ModuleInfoDAO subModule: modules) {
				List<ModuleInfoDAO> subModules = subModule.getSubModules();
				
				if(subModules != null) 
					printModule(printer, subModules);
				else
					printer.printRecord(
							subModule.getAbsoluteModulePath(),
							subModule.getModulePosition(),
							subModule.getModuleDeclaration(),
							subModule.getModuleComplexity());
			}
	}
}
