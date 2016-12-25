package cz.sokoban4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import cz.sokoban4j.SokobanConfig.ELevelFormat;
import cz.sokoban4j.simulation.SokobanResult;
import cz.sokoban4j.simulation.SokobanResult.SokobanResultType;
import cz.sokoban4j.simulation.agent.IAgent;

public class SokobanConsole {
	
	private static final char ARG_FILE_SHORT = 'f';
	
	private static final String ARG_FILE_LONG = "file";
	
	private static final char ARG_LEVEL_SHORT = 'l';
	
	private static final String ARG_LEVEL_LONG = "level";
	
	private static final char ARG_FILE_FORMAT_SHORT = 'm';
	
	private static final String ARG_FILE_FORMAT_LONG = "format";
	
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
	
	private static JSAP jsap;

	private static String fileString = null;
	
	private static File file = null;

	private static int level;
	
	private static String fileFormatString;
	
	private static ELevelFormat fileFormat;
	
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
        System.out.println("Usage: java -jar sokoban.jar ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
	}

	private static void header() {
		if (headerOutput) return;
		System.out.println();
		System.out.println("===============");
		System.out.println("Sokoban Console");
		System.out.println("===============");
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
	    opt2.setHelp("Simulation ID echoed into CSV.");
	    
	    jsap.registerParameter(opt2);
	    
	    FlaggedOption opt3 = new FlaggedOption(ARG_TIMEOUT_MILLIS_LONG)
	    	.setStringParser(JSAP.LONG_PARSER)
	    	.setRequired(false)
	    	.setDefault("-1")
	    	.setShortFlag(ARG_TIMEOUT_MILLIS_SHORT)
	    	.setLongFlag(ARG_TIMEOUT_MILLIS_LONG);    
	    opt3.setHelp("Timeout for the level in milliseconds; -1 to disable.");
	    
	    jsap.registerParameter(opt3);
	    
	    FlaggedOption opt31 = new FlaggedOption(ARG_LEVEL_LONG)
	    	.setStringParser(JSAP.INTEGER_PARSER)
	    	.setRequired(true) 	    	
	    	.setShortFlag(ARG_LEVEL_SHORT)
	    	.setLongFlag(ARG_LEVEL_LONG);    
	    opt31.setHelp("Level number to run from the file, 0-based.");
	
	    jsap.registerParameter(opt31);
	    
	    FlaggedOption opt32 = new FlaggedOption(ARG_RESULT_FILE_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false)
	    	.setDefault("./results/Sokoban-Results.csv")
	    	.setShortFlag(ARG_RESULT_FILE_SHORT)
	    	.setLongFlag(ARG_RESULT_FILE_LONG);    
	    opt32.setHelp("File where to append the result. File will be created if does not exist.");
	    
	    jsap.registerParameter(opt32);
	    
	    FlaggedOption opt33 = new FlaggedOption(ARG_FILE_FORMAT_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(false)
	    	.setShortFlag(ARG_FILE_FORMAT_SHORT)
	    	.setLongFlag(ARG_FILE_FORMAT_LONG);    
	    opt33.setHelp("Force level file format interpretation to: s4jl, sok");
	
	    jsap.registerParameter(opt33);
    
	    FlaggedOption opt6 = new FlaggedOption(ARG_FILE_LONG)
	    	.setStringParser(JSAP.STRING_PARSER)
	    	.setRequired(true)
	    	.setShortFlag(ARG_FILE_SHORT)
	    	.setLongFlag(ARG_FILE_LONG);    
	    opt6.setHelp("Level file to load.");
	
	    jsap.registerParameter(opt6);
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

		fileString = config.getString(ARG_FILE_LONG);

		level = config.getInt(ARG_LEVEL_LONG);
		
		fileFormatString = config.getString(ARG_FILE_FORMAT_LONG);
		
		timeoutMillis = config.getLong(ARG_TIMEOUT_MILLIS_LONG);
		
		resultFileString = config.getString(ARG_RESULT_FILE_LONG);
		
		id = config.getString(ARG_ID_LONG);
		
		visualiztion = config.getBoolean(ARG_VISUALIZATION_LONG);
		
		agentClassString = config.getString(ARG_AGENT_LONG);
	}
	
	private static void sanityChecks() {
		System.out.println("Sanity checks...");
		
		if (fileFormatString != null) {
			fileFormat = ELevelFormat.getForExtension(fileFormatString);
			if (fileFormat == null) {
				fail("Unrecognized file format: " + fileFormatString);
			}
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
		
		file = new File(fileString);
		System.out.println("-- level file: " + fileString + " --> " + file.getAbsolutePath());
		if (!file.isFile()) {
			fail("Level file specified is not a file: " + fileString + " --> " + file.getAbsolutePath());
		}
		System.out.println("-- level number: " + level);
		
		try {
			agentClass = Class.forName(agentClassString);
		} catch (ClassNotFoundException e) {
			fail("Failed to find class for name: " + agentClassString, e);
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
		
	    System.out.println("Sanity checks OK!");
	}
	
	private static SokobanResult run() {
		System.out.println("Running SOKOBAN!");
		
		SokobanConfig config = new SokobanConfig();
		
		config.agent = agent;
		config.id = id;
		config.level = file;
		config.levelNumber = level;
		config.levelFormat = (fileFormat == null ? ELevelFormat.getExpectedLevelFormat(file) : fileFormat);
		config.timeoutMillis = timeoutMillis;
		config.visualization = visualiztion;
		
		SokobanResult result = Sokoban.runAgentLevel(config);
		
		outputResult(result, resultFile);
		
		return result;
	}
	
	private static void outputResult(SokobanResult result, File resultFile) {
		System.out.println("Outputting result: " + result);
		FileOutputStream output = null;		
		boolean header = !resultFile.exists();
		try {
			output = new FileOutputStream(resultFile, true);
		} catch (FileNotFoundException e) {
			fail("Failed to append to the result file: " + resultFile.getAbsolutePath());
		}
		try {
			PrintWriter writer = new PrintWriter(output);
		
			if (header) {
				writer.println("id;levelFile;levelNumber;agent;result;steps;playTimeMillis");
			}
			writer.println(result.getId() + ";" + file.getName() + ";" + level + ";" + agentClassString + ";" + result.getResult() + ";" + result.getSteps() + ";" + result.getSimDurationMillis());
			
			writer.flush();
			writer.close();
			
		} finally {
			try {
				output.close();
			} catch (IOException e) {
			}
		}		
	}

	// ==============
	// TEST ARGUMENTS
	// ==============
	
	public static String[] getTestArgs() {
		return new String[] {
				  "-f", "./levels/sokobano.de/Blazz.sok"
				, "-r", "./results/human.csv" // result file
				, "-l", "0" // level count (0-based)
		      //, "-m", "s4jl" // level file format
				, "-t", "20000"   // timeout, -1 to disable
				, "-a", "cz.sokoban4j.agents.HumanAgent"
				, "-v", "true"  // visualization
				, "-i", "human" // id of simulation				
		};
	}
	
	public static String[] getArgs(SokobanConfig config, File resultFile) {
		List<String> args = new ArrayList<String>();
		
		args.add("-f"); args.add(config.level.getAbsolutePath()); // level file to play
		
		args.add("-r"); args.add(resultFile.getAbsolutePath());   // result file
		
		args.add("-l"); args.add(String.valueOf(config.levelNumber)); // level number
		
		if (config.levelFormat != null) {
			args.add("-m"); args.add(config.levelFormat.getExtension()); // level file format
		}
		
		args.add("-t"); args.add(String.valueOf(config.timeoutMillis)); // timeout
		
		args.add("-a"); args.add(config.agent.getClass().getName()); // class name
		
		args.add("-v"); args.add(String.valueOf(config.visualization)); // visualization
		
		if (config.id != null) {
			args.add("-i"); args.add(config.id); // simulation id
		}
		
		return (String[]) args.toArray(new String[0]);
	}
	
	public static String[] getArgs(SokobanConfig config, Class agentClass, File resultFile) {
		List<String> args = new ArrayList<String>();
		
		args.add("-f"); args.add(config.level.getAbsolutePath()); // level file to play
		
		args.add("-r"); args.add(resultFile.getAbsolutePath());   // result file
		
		args.add("-l"); args.add(String.valueOf(config.levelNumber)); // level number
		
		if (config.levelFormat != null) {
			args.add("-m"); args.add(config.levelFormat.getExtension()); // level file format
		}
		
		args.add("-t"); args.add(String.valueOf(config.timeoutMillis)); // timeout
		
		args.add("-a"); args.add(agentClass == null ? config.agent.getClass().getName() : agentClass.getName()); // class name
		
		args.add("-v"); args.add(String.valueOf(config.visualization)); // visualization
		
		if (config.id != null) {
			args.add("-i"); args.add(config.id); // simulation id
		}
		
		return (String[]) args.toArray(new String[0]);
	}
	
	public static String[] getArgs(SokobanConfig config, String agentClass, File resultFile) {
		List<String> args = new ArrayList<String>();
		
		args.add("-f"); args.add(config.level.getAbsolutePath()); // level file to play
		
		args.add("-r"); args.add(resultFile.getAbsolutePath());   // result file
		
		args.add("-l"); args.add(String.valueOf(config.levelNumber)); // level number
		
		if (config.levelFormat != null) {
			args.add("-m"); args.add(config.levelFormat.getExtension()); // level file format
		}
		
		args.add("-t"); args.add(String.valueOf(config.timeoutMillis)); // timeout
		
		args.add("-a"); args.add(agentClass == null ? config.agent.getClass().getName() : agentClass); // class name
		
		args.add("-v"); args.add(String.valueOf(config.visualization)); // visualization
		
		if (config.id != null) {
			args.add("-i"); args.add(config.id); // simulation id
		}
		
		return (String[]) args.toArray(new String[0]);
	}
	
	public static void main(String[] args) throws JSAPException {
		if (args == null || args.length == 0) {
			Main.main(args);
			return;
		}
		
		// -----------
		// FOR TESTING
		// -----------
		//args = getTestArgs();		
		
		// --------------
		// IMPLEMENTATION
		// --------------
		
		SokobanResult result = null;
		
		try {
			initJSAP();

			header();
		    
		    readConfig(args);
		    
		    sanityChecks();
		    
		    result = run();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	    
	    System.out.println("---// FINISHED //---");

	    if (result == null) System.exit(SokobanResultType.TERMINATED.getExitValue()+1);
	    
	    System.exit(result.getResult().getExitValue());	    	    
	}

}
