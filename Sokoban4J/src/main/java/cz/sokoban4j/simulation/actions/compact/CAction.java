package cz.sokoban4j.simulation.actions.compact;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.oop.Board;

public abstract class CAction {
	
	public abstract EActionType getType();
	
	/**
	 * Provides "the first direction"
	 * @return
	 */
	public abstract EDirection getDirection();
	
	/**
	 * Provides "all movements" in case of macro actions (e.g. {@link CWalk}, {@link CWalkPush}).
	 * @return
	 */
	public abstract EDirection[] getDirections();
	
	/**
	 * How many steps the action implements; may return -1 if unknown (i.e., custom teleports).
	 * @return
	 */
	public abstract int getSteps();

	public abstract boolean isPossible(BoardCompact board);
	
	public abstract void perform(BoardCompact board);
	
	public abstract void reverse(BoardCompact board);
	
	public abstract IAction getAction();
	
	/**
	 * If we move 1 step in given 'dir', will we still be at board? 
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	protected boolean onBoard(BoardCompact board, int tileX, int tileY, EDirection dir) {		
		return isOnBoard(board, tileX, tileY, dir);
	}
	
	/**
	 * If we move 1 step in given 'dir', will we still be at board? 
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	public static boolean isOnBoard(BoardCompact board, int tileX, int tileY, EDirection dir) {
		int targetX = tileX + dir.dX;
		if (targetX < 0 || targetX >= board.width()) return false;
		int targetY = tileY + dir.dY;
		if (targetY < 0 || targetY >= board.height()) return false;
		return true;
	}
	
}
