package cz.sokoban4j.simulation.actions.compact;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.compact.BoardCompact;

public class CAction {

	/**
	 * If we move 1 step in given 'dir', will we still be at board? 
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	protected static boolean onBoard(BoardCompact board, int tileX, int tileY, EDirection dir) {
		int targetX = tileX + dir.dX;
		if (targetX < 0 || targetX >= board.width()) return false;
		int targetY = tileY + dir.dY;
		if (targetY < 0 || targetY >= board.height()) return false;
		return true;
	}
	
}
