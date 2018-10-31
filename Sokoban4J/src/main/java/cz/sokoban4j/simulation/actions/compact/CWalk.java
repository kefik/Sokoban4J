package cz.sokoban4j.simulation.actions.compact;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.oop.Board;

/**
 * Works more like a teleport, does not check whether the path to [x,y] actually exists.
 * @author Jimmy
 */
public class CWalk extends CAction implements IAction {

	private int x;
	private int y;
	
	private int fromX = -1;
	private int fromY = -1;
	
	private EDirection[] path;

	public CWalk(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public CWalk(int x, int y, EDirection[] path) {
		this.x = x;
		this.y = y;
		this.path = path;
	}

	@Override
	public EActionType getType() {
		return EActionType.WALK;
	}

	@Override
	public EDirection getDirection() {
		return path == null ? null : path[0];
	}
	
	@Override
	public EDirection[] getDirections() {
		return path;
	}
	
	/**
	 * How many steps do you need in order to perform the walk; defined only if directions are provided during construction using {@link CWalk#CWalk(int, int, EDirection[])}.
	 * @return
	 */
	public int getSteps() {
		return path == null ? -1 : path.length;
	}

	@Override
	public boolean isPossible(BoardCompact board) {
		return CTile.isWalkable(board.tile(x, y));
	}

	@Override
	public void perform(BoardCompact board) {
		this.fromX = board.playerX;
		this.fromY = board.playerY;
		if (fromX != x || fromY != y) {
			board.movePlayer(board.playerX, board.playerY, x, y);
		}
	}

	@Override
	public void reverse(BoardCompact board) {
		if (fromX != x || fromY != y) {
			board.movePlayer(x, y, fromX, fromY);
		}
	}

	@Override
	public IAction getAction() {
		return this;
	}
	
	// =======
	// IAction
	// =======

	@Override
	public EActionType getType(Board board) {
		return EActionType.WALK;
	}

	@Override
	public boolean isPossible(Board board) {
		return board.tile(x, y).isWalkable();
	}

	@Override
	public boolean perform(Board board) {
		if (!isPossible(board)) return false;
		this.fromX = board.player.getTileX();
		this.fromY = board.player.getTileY();
		board.move(board.player, x, y);
		return true;
	}
	
	// ======
	// Object
	// ======
	
	@Override
	public String toString() {
		if (fromX < 0) {
			return "CWalk[->" + x + "," + y + "]";
		} else {
			return "CWalk[" + fromX + "," + fromY + "->" + x + "," + y + "]";
		}
	}
	
}
