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
 * MOVE ONLY, if there is a box, an edge or no free space, then the action is considered "not possible".
 * @author Jimmy
 */
public class CMove extends CAction {
	
	private static Map<EDirection, CMove> actions = new HashMap<EDirection, CMove>();
	
	static {
		actions.put(EDirection.DOWN, new CMove(EDirection.DOWN));
		actions.put(EDirection.UP, new CMove(EDirection.UP));
		actions.put(EDirection.LEFT, new CMove(EDirection.LEFT));
		actions.put(EDirection.RIGHT, new CMove(EDirection.RIGHT));
	}
	
	public static Collection<CMove> getActions() {
		return actions.values();
	}
	
	public static CMove getAction(EDirection direction) {
		return actions.get(direction);
	}
	
	private EDirection dir;
	
	private EDirection[] dirs;
	
	public CMove(EDirection dir) {
		this.dir = dir;
		this.dirs = new EDirection[]{ dir };
	}
	
	@Override
	public EActionType getType() {
		return EActionType.MOVE;
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
		// PLAYER ON THE EDGE
		if (!onBoard(board, board.playerX, board.playerY, dir)) return false;
		
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
	@Override
	public void perform(BoardCompact board) {
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
	}
	
	/**
	 * REVERSE THE MOVE PRVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
	 * @param board
	 * @param dir
	 */
	@Override
	public void reverse(BoardCompact board) {
		// REVERSE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
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
		return "CMove[" + dir.toString() + "]";
	}

}
