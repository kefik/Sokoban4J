package cz.sokoban4j.simulation.actions.slim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.slim.BoardSlim;
import cz.sokoban4j.simulation.board.slim.STile;

/**
 * PUSH ONLY. If the player is not next to the box or there is nowhere to push the box, than the action is considered as not possible.
 * @author Jimmy
 */
public class SPush extends SAction {
	
	private static Map<EDirection, SPush> actions = new HashMap<EDirection, SPush>();
	
	static {
		actions.put(EDirection.DOWN, new SPush(EDirection.DOWN));
		actions.put(EDirection.UP, new SPush(EDirection.UP));
		actions.put(EDirection.LEFT, new SPush(EDirection.LEFT));
		actions.put(EDirection.RIGHT, new SPush(EDirection.RIGHT));
	}
	
	public static Collection<SPush> getActions() {
		return actions.values();
	}
	
	public static SPush getAction(EDirection direction) {
		return actions.get(direction);
	}
	
	private EDirection dir;
	
	public SPush(EDirection dir) {
		this.dir = dir;
	}
	
	@Override
	public EActionType getType() {
		return EActionType.PUSH;
	}

	@Override
	public EDirection getDirection() {
		return dir;
	}
	
	@Override
	public boolean isPossible(BoardSlim board) {
		// PLAYER ON THE EDGE
		if (!onBoard(board, board.playerX, board.playerY, dir)) return false;
		
		// TILE TO THE DIR IS NOT BOX
		if (!STile.isBox(board.tile(board.playerX+dir.dX, board.playerY+dir.dY))) return false;
		
		// BOX IS ON THE EDGE IN THE GIVEN DIR
		if (!onBoard(board, board.playerX+dir.dX, board.playerY+dir.dY, dir)) return false;
		
		// TILE TO THE DIR OF THE BOX IS NOT FREE
		if (!STile.isFree(board.tile(board.playerX+dir.dX+dir.dX, board.playerY+dir.dY+dir.dY))) return false;
				
		// YEP, WE CAN PUSH
		return true;
	}
	
	/**
	 * PERFORM THE PUSH, no validation, call {@link #isPossible(BoardCompact, EDirection)} first!
	 * @param board
	 * @param dir
	 */
	@Override
	public void perform(BoardSlim board) {
		// MOVE THE BOX
		board.moveBox((byte)(board.playerX + dir.dX), (byte)(board.playerY + dir.dY), (byte)(board.playerX + dir.dX + dir.dX), (byte)(board.playerY + dir.dY + dir.dY));
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, (byte)(board.playerX + dir.dX), (byte)(board.playerY + dir.dY));
	}
	
	/**
	 * REVERSE THE ACTION PREVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
	 * @param board
	 * @param dir
	 */
	@Override
	public void reverse(BoardSlim board) {
		// MARK PLAYER POSITION
		byte playerX = board.playerX;
		byte playerY = board.playerY;
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, (byte)(board.playerX - dir.dX), (byte)(board.playerY - dir.dY));
		// MOVE THE BOX
		board.moveBox((byte)(playerX + dir.dX), (byte)(playerY + dir.dY), playerX, playerY);
	}
	
	/**
	 * Get OOP representation of given action.
	 * @param dir
	 * @return
	 */
	@Override
	public IAction getAction() {
		return MoveOrPush.getMoveOrPush(dir);
	}
	
	@Override
	public String toString() {
		return "SPush[" + dir.toString() + "]";
	}

}
