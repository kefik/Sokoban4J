package cz.sokoban4j.simulation;

import java.io.File;

import cz.sokoban4j.simulation.agent.IAgent;

public class SokobanResult {
	
	public static enum SokobanResultType {
		
		/**
		 * Simulation ended with agent winning the game.
		 */
		VICTORY(0),
		
		/**
		 * Simulation ended with timeout; agent failed to finish the game.
		 */
		TIMEOUT(1),
		
		/**
		 * Simulation ended with an agent exception; agent failed. 
		 */
		AGENT_EXCEPTION(2),
		
		/**
		 * Simulation ended with simulation exception; simulation failed.
		 */
		SIMULATION_EXCEPTION(3),
		
		/**
		 * Simulation has been terminated from the outside.
		 */
		TERMINATED(4);
		
		private int exitValue;

		private SokobanResultType(int exitValue) {
			this.exitValue = exitValue;
		}

		public int getExitValue() {
			return exitValue;
		}
		
		public static SokobanResultType getForExitValue(int exitValue) {
			for (SokobanResultType value : SokobanResultType.values()) {
				if (value.exitValue == exitValue) return value;
			}
			return null;
		}

	}
	
	private String id = null;
	
	private IAgent agent = null;
	
	private String level = null;
	
	private SokobanResultType result = null;
	
	private Throwable execption;
	
	private int steps = 0;
	
	private long simStartMillis = 0;
	
	private long simEndMillis = 0;
	
	public SokobanResult() {		
	}

	/**
	 * Assigned ID given to this simulation.
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Agent that was running in simulation.
	 * @return
	 */
	public IAgent getAgent() {
		return agent;
	}

	public void setAgent(IAgent agent) {
		this.agent = agent;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * Result of the simulation.
	 * @return
	 */
	public SokobanResultType getResult() {
		return result;
	}

	public void setResult(SokobanResultType result) {
		this.result = result;
	}

	/**
	 * How many steps an agent performed.
	 * @return
	 */
	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	/**
	 * Time the simulation started in milliseconds (obtained via {@link System#currentTimeMillis()}.
	 * @return
	 */
	public long getSimStartMillis() {
		return simStartMillis;
	}

	public void setSimStartMillis(long simStartMillis) {
		this.simStartMillis = simStartMillis;
	}

	/**
	 * Time the simulation ended in milliseconds (obtained via {@link System#currentTimeMillis()}.
	 * @return
	 */
	public long getSimEndMillis() {
		return simEndMillis;
	}

	public void setSimEndMillis(long simEndMillis) {
		this.simEndMillis = simEndMillis;
	}
	
	/**
	 * How long the simulation run in milliseconds.
	 * @return
	 */
	public long getSimDurationMillis() {
		return simEndMillis - simStartMillis;
	}

	/**
	 * Exception caught during the simulation; 
	 * filled in case of {@link #getResult()} == {@link SokobanResultType#AGENT_EXCEPTION} or {@link SokobanResultType#SIMULATION_EXCEPTION}.  
	 * @return
	 */
	public Throwable getExecption() {
		return execption;
	}

	public void setExecption(Throwable execption) {
		this.execption = execption;
	}
	
	@Override
	public String toString() {
		return "SokobanResult[" + getResult() + "]";
	}
	
}
