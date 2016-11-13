package cz.sokoban4j.simulation.actions.compact;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;

/**
 * MOVE ONLY, if there is a box, an edge or no free space, then the action is considered "not possible".
 * @author Jimmy
 */
public class CMove {
	
	public static boolean isPossible(BoardCompact board, EDirection dir) {
		// PLAYER ON THE EDGE
		if (!CAction.onBoard(board, board.playerX, board.playerY, dir)) return false;
		
		// TILE TO THE DIR IS FREE
		if (CTile.isFree(board.tile(board.playerX+dir.dX, board.playerY+dir.dY))) return true;
				
		// TILE WE WISH TO MOVE TO IS NOT FREE
		return false;
	}
		
	/**
	 * PERFORM THE MOVE, no validation, call {@link #isPossible(BoardCompact, EDirection)} first!
	 * @param board
	 * @param dir
	 */
	public static void perform(BoardCompact board, EDirection dir) {
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
		board.playerX = board.playerX + dir.dX;
		board.playerY = board.playerY + dir.dY;
	}
	
	/**
	 * REVERSE THE MOVE PRVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
	 * @param board
	 * @param dir
	 */
	public static void reverse(BoardCompact board, EDirection dir) {
		// REVERSE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
		board.playerX = board.playerX - dir.dX;
		board.playerY = board.playerY - dir.dY;
	}
	
	/**
	 * Get OOP representation of given action.
	 * @param dir
	 * @return
	 */
	public static IAction getAction(EDirection dir) {
		return MoveOrPush.getMoveOrPush(dir);
	}

}
