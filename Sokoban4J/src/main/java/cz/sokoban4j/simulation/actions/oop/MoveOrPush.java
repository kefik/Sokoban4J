package cz.sokoban4j.simulation.actions.oop;

import java.util.HashMap;
import java.util.Map;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.simulation.board.oop.Tile;
import cz.sokoban4j.simulation.board.oop.entities.Entity;

/**
 * Combines MOVE and PUSH, can perform both.
 * 
 * @author Jimmy
 */
public class MoveOrPush implements IAction {

	private static Map<EDirection, MoveOrPush> actions = new HashMap<EDirection, MoveOrPush>();
	
	static {
		actions.put(EDirection.DOWN, new MoveOrPush(EDirection.DOWN));
		actions.put(EDirection.UP, new MoveOrPush(EDirection.UP));
		actions.put(EDirection.LEFT, new MoveOrPush(EDirection.LEFT));
		actions.put(EDirection.RIGHT, new MoveOrPush(EDirection.RIGHT));
	}
	
	public static IAction getMoveOrPush(EDirection direction) {
		return actions.get(direction);
	}
	
	protected EDirection dir;

	public MoveOrPush(EDirection direction) {
		this.dir = direction;
	}
	
	@Override
	public EActionType getType(Board board) {
		if (!isPossible(board)) return EActionType.INVALID;
		if (willMoveBox(board)) return EActionType.PUSH;
		return EActionType.MOVE;
	}
	
	@Override
	public EDirection getDirection() {
		return dir;
	}

	@Override
	public boolean isPossible(Board board) {
		Tile playerTile = board.player.getTile();
		
		// NO PLAYER TILE
		if (playerTile == null) return false;
		
		// PLAYER ON THE EDGE
		if (!onBoard(board, playerTile, dir, 1)) return false;
		
		// TILE TO THE DIR IS FREE
		if (board.tile(playerTile.tileX+dir.dX, playerTile.tileY+dir.dY).isFree()) return true;
		
		// IF WE CAN MOVE THE BOX, THEN OK
		if (willMoveBox(board)) return true;
		
		// IMPASSABLE TILE
		return false;
	}
	
	/**
	 * If we move 'steps' in given 'dir', will we still be at board? 
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	protected boolean onBoard(Board board, Tile tile, EDirection dir, int steps) {
		int targetX = tile.tileX + steps * dir.dX;
		if (targetX < 0 || targetX >= board.width) return false;
		int targetY = tile.tileY + steps * dir.dY;
		if (targetY < 0 || targetY >= board.height) return false;
		return true;
	}
	
	protected boolean willMoveBox(Board board) {
		Tile playerTile = board.player.getTile();
		// PLAYER ON THE EDGE
		if (!onBoard(board, playerTile, dir, 1)) return false;
		
		Tile boxTile = board.tile(playerTile.tileX+dir.dX, playerTile.tileY+dir.dY);
		
		// NOT A BOX NEXT TO THE PLAYER
		if (!boxTile.isSomeBox()) return false;
		
		// BOX ON THE EDGE
		if (!onBoard(board, boxTile, dir, 1)) return false;
				
		// BOX BEHIND THE TILE IS FREE
		if (board.tile(boxTile.tileX+dir.dX, boxTile.tileY+dir.dY).isFree()) return true;
		
		// ???
		return false;
	}

	@Override
	public boolean perform(Board board) {
		if (!isPossible(board)) return false;
		
		Tile playerTile = board.player.getTile();
		
		if (willMoveBox(board)) {
			// MOVE THE BOX FIRST
			Entity box = board.tile(playerTile.tileX+dir.dX, playerTile.tileY+dir.dY).entity;
			board.move(box, playerTile.tileX+dir.dX+dir.dX, playerTile.tileY+dir.dY+dir.dY);
		}
		
		// MOVE THE PLAYER
		Entity player = board.player;
		board.move(player, playerTile.tileX+dir.dX, playerTile.tileY+dir.dY);
		
		return true;
	}

	

}
