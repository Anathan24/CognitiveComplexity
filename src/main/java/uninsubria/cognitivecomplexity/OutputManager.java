package uninsubria.cognitivecomplexity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uninsubria.cognitivecomplexity.dao.ModuleInfoDAO;

public class OutputManager {

	private static final Logger logger = LogManager.getLogger();
	
	public OutputManager(String fileName, List<List<ModuleInfoDAO>> calculusResult) {
		this.createCSVFile(fileName, calculusResult);
	}
	
	private void createCSVFile(String fileName, List<List<ModuleInfoDAO>> calculusResult) {
		
		try(
				FileWriter file = new FileWriter(fileName+" - complexity.csv");
				CSVPrinter printer = new CSVPrinter(file, CSVFormat.DEFAULT);
			){
			printer.printRecord("Absolute Module Path", "Module Position", "Module Declaration", "Module Complexity");
			
			for(List<ModuleInfoDAO> list: calculusResult) {
				for(ModuleInfoDAO type: list) {
					List<ModuleInfoDAO> modules = type.getSubModules();
					printModule(printer, modules);
					printer.println();
				}
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
