package cz.sokoban4j.tournament;

import java.io.File;
import java.util.Iterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class SokobanSummaryConsole {
	
	private static final char ARG_RUN_RESULTS_SHORT = 'r';
	
	private static final String ARG_RUN_RESULTS_LONG = "run-results-file";
	
	private static final char ARG_OUTPUT_TO_SHORT = 'o';
	
	private static final String ARG_OUTPUT_TO_LONG = "output-to-file";
		
	private static JSAP jsap;

	private static String runResultsFileString = null;
	
	private static File runResultsFile;
		
	private static String outputToFileString;
	
	private static File outputToFile;
	
	private static JSAPResult config;
	
	private static boolean headerOutput = false;

	private static void fail(String errorMessage) {
		fail(errorMessage, null);
	}

	private static void fail(String errorMessage, Throwable e) {
		header();
		System.out.println("ERROR: " + errorMessage);
		System.out.println();
		if (e != null) {
			e.printStackTrace();
			System.out.println("");
		}		
        System.out.println("Usage: java -jar sokoban-summary.jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("=======================");
		System.out.println("Sokoban Summary Console");
		System.out.println("=======================");
		System.out.println();
		headerOutput = true;
	}
		
	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		
	    FlaggedOption opt11 = new FlaggedOption(ARG_OUTPUT_TO_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setDefault("./results/results-summary.csv")
	    	.setShortFlag(ARG_OUTPUT_TO_SHORT)
	    	.setLongFlag(ARG_OUTPUT_TO_LONG);    
	    opt11.setHelp("File where to output the summary; if existing, will be overwritten.");
	
	    jsap.registerParameter(opt11);
	    
	    FlaggedOption opt31 = new FlaggedOption(ARG_RUN_RESULTS_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 	    	
	    	.setShortFlag(ARG_RUN_RESULTS_SHORT)
	    	.setLongFlag(ARG_RUN_RESULTS_LONG);    
	    opt31.setHelp("File with respective run results as produced by SokobanTournamentConsole.");
	
	    jsap.registerParameter(opt31);
   	}

	private static void readConfig(String[] args) {
		System.out.println("Parsing command arguments.");
		
		try {
	    	config = jsap.parse(args);
	    } catch (Exception e) {
	    	fail(e.getMessage());
	    	System.out.println("");
	    	e.printStackTrace();
	    	throw new RuntimeException("FAILURE!");
	    }
		
		if (!config.success()) {
			String error = "Invalid arguments specified.";
			Iterator errorIter = config.getErrorMessageIterator();
			if (!errorIter.hasNext()) {
				error += "\n-- No details given.";
			} else {
				while (errorIter.hasNext()) {
					error += "\n-- " + errorIter.next();
				}
			}
			fail(error);
    	}

		runResultsFileString = config.getString(ARG_RUN_RESULTS_LONG);

		outputToFileString = config.getString(ARG_OUTPUT_TO_LONG);		
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		runResultsFile = new File(runResultsFileString);
		System.out.println("-- run results file to read: " + runResultsFileString + " --> " + runResultsFile.getAbsolutePath());
		
		if (!runResultsFile.exists()) {
			fail("Results file does not exist!");
		} else {
			if (!runResultsFile.isFile()) {
				fail("Resuts file is not a file!");
			}
		}	
		
		outputToFile = new File(outputToFileString);
		System.out.println("-- output to file: " + outputToFileString + " --> " + outputToFile.getAbsolutePath());
		
		if (!outputToFile.exists()) {
			System.out.println("---- output file does not exist, will be created");
		} else {
			if (!outputToFile.isFile()) {
				fail("Output file is not a file!");
			} else {
				System.out.println("---- output file exists, will be overwritten");
			}
		}		
		
		if (!outputToFile.getParentFile().exists()) {
			System.out.println("---- creating parent directories for " + outputToFile.getAbsolutePath());
			outputToFile.getParentFile().mkdirs();
			if (!outputToFile.getParentFile().exists()) {
				fail("Failed to create parent directories for " + outputToFile.getAbsolutePath());
			}
		}
		
	    System.out.println("Sanity checks OK!");
	}
	
	private static void run() {
		System.out.println("================");
		System.out.println("Running SUMMARY!");
		System.out.println("================");
		
		SokobanSummary summary = new SokobanSummary(runResultsFile);
		summary.summarize(outputToFile);
	}
	
	// ==============
	// TEST ARGUMENTS
	// ==============
	
	public static String[] getTestArgs() {
		return new String[] {
				  "-r", "./results/results.csv" // result file to read
				, "-o", "./results/results-summary.csv" // output file to produce
		};
	}
	
	public static void main(String[] args) throws JSAPException {
		// -----------
		// FOR TESTING
		// -----------
		//args = getTestArgs();		
		
		// --------------
		// IMPLEMENTATION
		// --------------
		
		try {
			initJSAP();

			header();
		    
		    readConfig(args);
		    
		    sanityChecks();
		    
		    run();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	    
	    System.out.println("---// SUMMARY FINISHED //---");
	    
	    System.exit(0);
	}

	

}
