package cz.sokoban4j.simulation.actions.compact;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;

/**
 * PUSH ONLY. If the player is not next to the box or there is nowhere to push the box, than the action is considered as not possible.
 * @author Jimmy
 */
public class CPush {
	
	public static boolean isPossible(BoardCompact board, EDirection dir) {
		// PLAYER ON THE EDGE
		if (!CAction.onBoard(board, board.playerX, board.playerY, dir)) return false;
		
		// TILE TO THE DIR IS NOT BOX
		if (!CTile.isSomeBox(board.tile(board.playerX+dir.dX, board.playerY+dir.dY))) return false;
		
		// BOX IS ON THE EDGE IN THE GIVEN DIR
		if (!CAction.onBoard(board, board.playerX+dir.dX, board.playerY+dir.dY, dir)) return false;
		
		// TILE TO THE DIR OF THE BOX IS NOT FREE
		if (!CTile.isFree(board.tile(board.playerX+dir.dX+dir.dX, board.playerY+dir.dY+dir.dY))) return false;
				
		// YEP, WE CAN PUSH
		return true;
	}
	
	/**
	 * PERFORM THE PUSH, no validation, call {@link #isPossible(BoardCompact, EDirection)} first!
	 * @param board
	 * @param dir
	 */
	public static void perform(BoardCompact board, EDirection dir) {
		// MOVE THE BOX
		board.moveBox(board.playerX + dir.dX, board.playerY + dir.dY, board.playerX + dir.dX + dir.dX, board.playerY + dir.dY + dir.dY);
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
		board.playerX = board.playerX + dir.dX;
		board.playerY = board.playerY + dir.dY;
	}
	
	/**
	 * REVERSE THE ACTION PREVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
	 * @param board
	 * @param dir
	 */
	public static void reverse(BoardCompact board, EDirection dir) {
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
		// MOVE THE BOX
		board.moveBox(board.playerX + dir.dX, board.playerY + dir.dY, board.playerX, board.playerY);
		// UPDATE PLAYER LOCATION
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
