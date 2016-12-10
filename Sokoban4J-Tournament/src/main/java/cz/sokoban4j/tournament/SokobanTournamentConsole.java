package cz.sokoban4j.tournament;

import java.io.File;
import java.util.Iterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import cz.sokoban4j.SokobanConfig;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.tournament.run.RunSokobanLevels;
import cz.sokoban4j.tournament.run.SokobanLevels;

public class SokobanTournamentConsole {
	
	private static final char ARG_LEVEL_LIST_SHORT = 'l';
	
	private static final String ARG_LEVEL_LIST_LONG = "level-list";
	
	private static final char ARG_TIMEOUT_MILLIS_SHORT = 't';
	
	private static final String ARG_TIMEOUT_MILLIS_LONG = "timeout-millis";
	
	private static final char ARG_VISUALIZATION_SHORT = 'v';
	
	private static final String ARG_VISUALIZATION_LONG = "visualization";
	
	private static final char ARG_AGENT_SHORT = 'a';
	
	private static final String ARG_AGENT_LONG = "agent";
	
	private static final char ARG_ID_SHORT = 'i';
	
	private static final String ARG_ID_LONG = "id";
	
	private static final char ARG_RESULT_FILE_SHORT = 'r';
	
	private static final String ARG_RESULT_FILE_LONG = "result-file";
	
	private static final char ARG_EXTRA_JAVA_PARAMS_SHORT = 'j';
	
	private static final String ARG_EXTRA_JAVA_PARAMS_LONG = "extra-java-params";

	
	private static JSAP jsap;

	private static String levelListString = null;
	
	private static SokobanLevels levelList;
		
	private static String extraJavaArgsString;
	
	private static String[] extraJavaArgs;
	
	private static long timeoutMillis;
	
	private static boolean visualiztion;
	
	private static String agentClassString;
	
	private static Class agentClass;
	
	private static IAgent agent;
	
	private static String id;
	
	private static String resultFileString;
	
	private static File resultFile;
	
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
        System.out.println("Usage: java -jar sokoban-tournament.jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("==========================");
		System.out.println("Sokoban Tournament Console");
		System.out.println("==========================");
		System.out.println();
		headerOutput = true;
	}
		
	private static void initJSAP() throws JSAPException {
		jsap = new JSAP();
		
        FlaggedOption opt1 = new FlaggedOption(ARG_VISUALIZATION_LONG)
	    	.setStringParser(JSAP.BOOLEAN_PARSER)
	    	.setRequired(false) 
	    	.setDefault("true")
	    	.setShortFlag(ARG_VISUALIZATION_SHORT)
	    	.setLongFlag(ARG_VISUALIZATION_LONG);    
	    opt1.setHelp("Turn on/off (true/false) visualization.");
	
	    jsap.registerParameter(opt1);
	    
	    FlaggedOption opt11 = new FlaggedOption(ARG_AGENT_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false) 
	    	.setDefault("cz.sokoban4j.agents.HumanAgent")
	    	.setShortFlag(ARG_AGENT_SHORT)
	    	.setLongFlag(ARG_AGENT_LONG);    
	    opt11.setHelp("Agent FQCN, e.g.: cz.sokoban4j.agents.HumanAgent");
	
	    jsap.registerParameter(opt11);
	    
	    FlaggedOption opt2 = new FlaggedOption(ARG_ID_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false)
	    	.setDefault("Sokoban")
	    	.setShortFlag(ARG_ID_SHORT)
	    	.setLongFlag(ARG_ID_LONG);    
	    opt2.setHelp("Simulation ID echoed into CSV for every level run.");
	    
	    jsap.registerParameter(opt2);
	    
	    FlaggedOption opt3 = new FlaggedOption(ARG_TIMEOUT_MILLIS_LONG)
	    	.setStringParser(JSAP.LONG_PARSER)
	    	.setRequired(false)
	    	.setDefault("-1")
	    	.setShortFlag(ARG_TIMEOUT_MILLIS_SHORT)
	    	.setLongFlag(ARG_TIMEOUT_MILLIS_LONG);    
	    opt3.setHelp("Timeout for every level in milliseconds; -1 to disable.");
	    
	    jsap.registerParameter(opt3);
	    
	    FlaggedOption opt31 = new FlaggedOption(ARG_LEVEL_LIST_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true) 	    	
	    	.setShortFlag(ARG_LEVEL_LIST_SHORT)
	    	.setLongFlag(ARG_LEVEL_LIST_LONG);    
	    opt31.setHelp("Level list to execute (in that order), format: level-file;level;level-file;level;... level can be a number (0-based) or 'all' (without apostrpohes)");
	
	    jsap.registerParameter(opt31);
	    
	    FlaggedOption opt32 = new FlaggedOption(ARG_RESULT_FILE_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false)
	    	.setDefault("./results/Sokoban-Results.csv")
	    	.setShortFlag(ARG_RESULT_FILE_SHORT)
	    	.setLongFlag(ARG_RESULT_FILE_LONG);    
	    opt32.setHelp("File where to append the result. File will be created if does not exist.");
	    
	    jsap.registerParameter(opt32);	    
	    
	    FlaggedOption opt42 = new FlaggedOption(ARG_EXTRA_JAVA_PARAMS_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false)
	    	.setShortFlag(ARG_EXTRA_JAVA_PARAMS_SHORT)
	    	.setLongFlag(ARG_EXTRA_JAVA_PARAMS_LONG);    
	    opt42.setHelp("Extra JVM parameters to pass to execution of respective levels; ' ' separated values within single \"...\" param only");
	    
	    jsap.registerParameter(opt42);	    
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

		levelListString = config.getString(ARG_LEVEL_LIST_LONG);

		timeoutMillis = config.getLong(ARG_TIMEOUT_MILLIS_LONG);
		
		resultFileString = config.getString(ARG_RESULT_FILE_LONG);
		
		id = config.getString(ARG_ID_LONG);
		
		visualiztion = config.getBoolean(ARG_VISUALIZATION_LONG);
		
		agentClassString = config.getString(ARG_AGENT_LONG);
		
		extraJavaArgsString = config.getString(ARG_EXTRA_JAVA_PARAMS_LONG);
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		System.out.println("-- parsing level list: " + levelListString);
		
		levelList = SokobanLevels.fromString(levelListString);
		levelList.validate();
		
		System.out.println("---- going to run at max: " + levelList.levels.size() + " levels");
				
		try {
			agentClass = Class.forName(agentClassString);
		} catch (ClassNotFoundException e) {
			fail("Failed to find class for name: " + agentClassString, e);
		}
		
		resultFile = new File(resultFileString);
		System.out.println("-- result file: " + resultFileString + " --> " + resultFile.getAbsolutePath());
		
		if (!resultFile.exists()) {
			System.out.println("---- result file does not exist, will be created");
		} else {
			if (!resultFile.isFile()) {
				fail("Result file is not a file!!");
			} else {
				System.out.println("---- result file exists, will be appended to");
			}
		}		
		
		if (!resultFile.getParentFile().exists()) {
			System.out.println("---- creating parent directories for " + resultFile.getAbsolutePath());
			resultFile.getParentFile().mkdirs();
			if (!resultFile.getParentFile().exists()) {
				fail("Failed to create parent directories for " + resultFile.getAbsolutePath());
			}
		}
		
		Object agentObject = null;
		try {
			agentObject = agentClass.getConstructor().newInstance();
		} catch (Exception e) {
			fail("Failed to instantiate class: " + agentClassString, e);
		} 
		if (!IAgent.class.isAssignableFrom(agentObject.getClass())) {
			fail("Class does not implement IAgent: " + agentClassString);
		}
		agent = (IAgent)agentObject; 
		
		System.out.println("-- agent checked: " + agentClassString);
		
		if (extraJavaArgsString != null) {
			extraJavaArgs = extraJavaArgsString.split(" ");
			System.out.println("-- extra JVM params: " + extraJavaArgsString);
			System.out.println("---- split into " + extraJavaArgs.length + " parts");
		}
		
	    System.out.println("Sanity checks OK!");
	}
	
	private static void run() {
		System.out.println("================");
		System.out.println("Running SOKOBAN!");
		System.out.println("================");
		
		SokobanConfig config = new SokobanConfig();
		
		config.agent = agent;
		config.id = id;
		config.timeoutMillis = timeoutMillis;
		config.visualization = visualiztion;
		
		RunSokobanLevels run = new RunSokobanLevels(config, agentClass, levelList, resultFile, extraJavaArgs);
		
		run.run();
	}
	
	// ==============
	// TEST ARGUMENTS
	// ==============
	
	public static String[] getTestArgs() {
		return new String[] {
				  "-l", "../Sokoban4J/levels/sokobano.de/Blazz.sok;all" // see {@link SokobanLevels} for details
				, "-r", "./results/results.csv" // result file
				, "-t", "20000" // timeout, -1 to disable
				, "-a", "cz.sokoban4j.agents.HumanAgent"
				, "-v", "true"  // visualization
				, "-i", "human" // id of simulation	
				, "-j", "-cp ./target/classes;../Sokoban4J/target/classes;../Sokoban4J-Agents/target/classes;./libs/jsap-2.1.jar;./libs/process-execution-3.7.0.jar;./libs/xstream-1.3.1.jar"
		};
	}
	
	public static void main(String[] args) throws JSAPException {
		// -----------
		// FOR TESTING
		// -----------
		args = getTestArgs();		
		
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
	    
	    System.out.println("---// TOURNAMENT FINISHED //---");
	    
	    System.exit(0);
	}

	

}
