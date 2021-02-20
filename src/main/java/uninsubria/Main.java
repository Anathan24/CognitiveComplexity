package uninsubria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uninsubria.cognitivecomplexity.CognitiveComplexity;

public class Main {

	private static final Logger logger = LogManager.getLogger();
	
	public static void main(String[] args) {
		logger.info("START");
		logger.info("-----------------------------------------------");
		
		if(args.length > 0)
			for(String str: args)
				new CognitiveComplexity(str);
		else
			logger.info("No file name received. Enter the name of the file to be processed!");
		
		logger.info("-----------------------------------------------");
		logger.info("END");
	}
}
