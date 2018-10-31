package cz.sokoban4j.simulation.actions.compact;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;

/**
 * PUSH ONLY. If the player is not next to the box or there is nowhere to push the box, than the action is considered as not possible.
 * @author Jimmy
 */
public class CPush extends CAction {
	
	private static Map<EDirection, CPush> actions = new HashMap<EDirection, CPush>();
	
	static {
		actions.put(EDirection.DOWN, new CPush(EDirection.DOWN));
		actions.put(EDirection.UP, new CPush(EDirection.UP));
		actions.put(EDirection.LEFT, new CPush(EDirection.LEFT));
		actions.put(EDirection.RIGHT, new CPush(EDirection.RIGHT));
	}
	
	public static Collection<CPush> getActions() {
		return actions.values();
	}
	
	public static CPush getAction(EDirection direction) {
		return actions.get(direction);
	}
	
	private EDirection dir;
	
	private EDirection[] dirs;
	
	public CPush(EDirection dir) {
		this.dir = dir;
		this.dirs = new EDirection[]{ dir };
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
	public EDirection[] getDirections() {
		return dirs;
	}
	
	@Override
	public int getSteps() {
		return 1;
	}
	
	@Override
	public boolean isPossible(BoardCompact board) {
		return isPushPossible(board, board.playerX, board.playerY, dir);
	}
	
	/**
	 * Is it possible to push the box from [playerX, playerY] in 'pushDirection' ?
	 * @param board
	 * @param playerX
	 * @param playerY
	 * @param pushDirection
	 * @return
	 */
	public static boolean isPushPossible(BoardCompact board, int playerX, int playerY, EDirection pushDirection) {
		// PLAYER ON THE EDGE
		if (!CAction.isOnBoard(board, playerX, playerY, pushDirection)) return false;
		
		// TILE TO THE DIR IS NOT BOX
		if (!CTile.isSomeBox(board.tile(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;
		
		// BOX IS ON THE EDGE IN THE GIVEN DIR
		if (!CAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;
		
		// TILE TO THE DIR OF THE BOX IS NOT FREE
		if (!CTile.isFree(board.tile(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;
				
		// YEP, WE CAN PUSH
		return true;
	}
	
	/**
	 * Is it possible to push the box from [playerX, playerY] in 'pushDirection' ? 
	 * 
	 * This deem the box pushable even if there is a player in that direction.
	 * 
	 * @param board
	 * @param playerX
	 * @param playerY
	 * @param pushDirection
	 * @return
	 */
	public static boolean isPushPossibleIgnorePlayer(BoardCompact board, int playerX, int playerY, EDirection pushDirection) {
		// PLAYER ON THE EDGE
		if (!CAction.isOnBoard(board, playerX, playerY, pushDirection)) return false;
		
		// TILE TO THE DIR IS NOT BOX
		if (!CTile.isSomeBox(board.tile(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;
		
		// BOX IS ON THE EDGE IN THE GIVEN DIR
		if (!CAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;
		
		// TILE TO THE DIR OF THE BOX IS NOT FREE
		if (!CTile.isWalkable(board.tile(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;
				
		// YEP, WE CAN PUSH
		return true;
	}
	
	/**
	 * PERFORM THE PUSH, no validation, call {@link #isPossible(BoardCompact, EDirection)} first!
	 * @param board
	 * @param dir
	 */
	@Override
	public void perform(BoardCompact board) {
		// MOVE THE BOX
		board.moveBox(board.playerX + dir.dX, board.playerY + dir.dY, board.playerX + dir.dX + dir.dX, board.playerY + dir.dY + dir.dY);
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
	}
	
	/**
	 * REVERSE THE ACTION PREVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
	 * @param board
	 * @param dir
	 */
	@Override
	public void reverse(BoardCompact board) {
		// MARK PLAYER POSITION
		int playerX = board.playerX;
		int playerY = board.playerY;
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
		// MOVE THE BOX
		board.moveBox(playerX + dir.dX, playerY + dir.dY, playerX, playerY);
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
		return "CPush[" + dir.toString() + "]";
	}

}
