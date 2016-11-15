package cz.sokoban4j.simulation.agent;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.compact.BoardCompact;

public interface IAgent {

	/**
	 * Agent got into a new level.
	 */
	public void newLevel();
	
	/**
	 * An agent receives current state of the board.
	 * @param board
	 */
	public void observe(BoardCompact board);
	
	/**
	 * An agent is queried where to move next. 
	 * @return
	 */
	public EDirection act();
	
	/**
	 * Agent managed to finish the level.
	 */
	public void victory();
	
	/**
	 * Terminate the agent as the game has finished.
	 */
	public void stop();
	
}
