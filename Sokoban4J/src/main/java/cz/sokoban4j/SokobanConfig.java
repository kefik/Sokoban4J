package cz.sokoban4j;

import java.io.File;

import cz.sokoban4j.simulation.agent.IAgent;

public class SokobanConfig {
	
	/**
	 * Can be used to mark unique name of the simulation.
	 */
	public String id = "Sokoban";
	
	/**
	 * Sokoban level file or directory to play.
	 * 
	 * FILE == play this file.
	 * DIRECTORY == play all *.s4kl files in alphabetic order found in this directory (no directory recursion).
	 */
	public File level;
	
	/**
	 * Timeout for the game; positive number == timeout in effect; otherwise no timeout.
	 */
	public long timeoutMillis = 0;
	
	/**
	 * TRUE == start Sokoban visualized using {@link SokobanVis}; FALSE == start Sokoban headless using {@link SokobanSim}.
	 */
	public boolean visualization = false;
	
	/**
	 * Instance of the agent that should play the game.
	 */
	public IAgent agent;
	
	/**
	 * Validates the configuration; throws {@link RuntimeException} if config is found invalid. 
	 */
	public void validate() {
		if (id == null) throw new RuntimeException("ID is null.");
		if (id.length() == 0) throw new RuntimeException("ID is of zero length.");
		if (agent == null) throw new RuntimeException("Agent is null.");
		if (level == null) throw new RuntimeException("Level is null.");
		if (!level.exists()) throw new RuntimeException("Level '" + level.getAbsolutePath() + "' does not exist.");
		if (!level.isFile() && !level.isDirectory()) throw new RuntimeException("Level '" + level.getAbsolutePath() + "' is neither a file nor a directory.");
	}

}
