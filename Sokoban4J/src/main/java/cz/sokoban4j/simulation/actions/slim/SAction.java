package cz.sokoban4j.simulation.actions.slim;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.board.slim.BoardSlim;

public abstract class SAction {
	
	public abstract EActionType getType();
	
	public abstract EDirection getDirection();

	public abstract boolean isPossible(BoardSlim board);
	
	public abstract void perform(BoardSlim board);
	
	public abstract void reverse(BoardSlim board);
	
	public abstract IAction getAction();
	
	/**
	 * If we move 1 step in given 'dir', will we still be at board? 
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	protected boolean onBoard(BoardSlim board, int tileX, int tileY, EDirection dir) {
		int targetX = tileX + dir.dX;
		if (targetX < 0 || targetX >= board.width()) return false;
		int targetY = tileY + dir.dY;
		if (targetY < 0 || targetY >= board.height()) return false;
		return true;
	}
	
}
