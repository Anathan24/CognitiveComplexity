package uninsubria.cognitivcomplexity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CSVGenerator {

	private static final Logger logger = LogManager.getLogger();
	private String fileName = null;
	
	private Map<String, List<CalculusResult>> calculusResult; 
	
	/**
	 * Crea un csv file contenente i risultati dei calcoli.
	 * @param fileName il nome del file.
	 * @param calculusResult la struttura dati contenente i risultati dei calcoli.
	 */
	public CSVGenerator(String fileName, Map<String, List<CalculusResult>> calculusResult) {
		this.fileName = fileName;
		this.calculusResult = calculusResult;
		this.createCSVFile();
	}
	
	private void createCSVFile() {
		
		try(
				FileWriter file = new FileWriter(this.fileName+"_complexity.csv");
				CSVPrinter printer = new CSVPrinter(file, CSVFormat.DEFAULT);
			){
			printer.printRecord("Position","Method Declaration","Complexity");
			Iterator<Entry<String, List<CalculusResult>>> iterator = calculusResult.entrySet().iterator();
			while(iterator.hasNext()) {
				int classComplexity = 0;
				Map.Entry<String, List<CalculusResult>> pair = iterator.next();
				String className = pair.getKey();
				List<CalculusResult> results = pair.getValue();
				for(CalculusResult result: results) {
					printer.printRecord(
							result.getEntityPosition(),
							result.getEntityDeclaration(),
							result.getEntityComplexity());
					classComplexity += result.getEntityComplexity();
				}
				printer.println();
				printer.printRecord("Complessita' della classe: "+className+" = "+classComplexity);
			}
			printer.flush();
		} catch (IOException e) {
			logger.debug("Creation CSV File failed! {}", e.getMessage());
		}
		
	}
}
