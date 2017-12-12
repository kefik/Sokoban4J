package cz.jsokoban.simulation.board;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.compact.CAction;
import cz.sokoban4j.simulation.actions.compact.CMove;
import cz.sokoban4j.simulation.actions.compact.CPush;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.compressed.BoardCompressed;
import cz.sokoban4j.simulation.board.oop.Board;

public class ValidatingAgent {

	private Board board;

	public ValidatingAgent(Board board) {
		this.board = board;
	}
	
	// ===========
	// BOARD - OOP
	// ===========
	
	public void validateBoard() {
		// CHECK CLONING
		Board clone = board.clone();
		if (!board.equalsState(clone)) {
			throw new RuntimeException("CLONE FAILED!");
		}
		
		for (int x = 0; x < board.width; ++x) {
			for (int y = 0; y < board.height; ++y) {
				if (board.tile(x, y).isFree()) {
					board.player.getTile().entity = null;
					board.player.setTile(board.tile(x, y));
					board.player.getTile().entity = board.player;					
				}
				
				// CHECK CLONING
				clone = board.clone();
				if (!board.equalsState(clone)) {
					throw new RuntimeException("CLONE FAILED!");
				}
				
				// TEST ACTIONS
				testPlayerActions(board);
			}
		}
	}
	
	private void testPlayerActions(Board board) {
		for (EDirection direction : EDirection.arrows()) {
			IAction action = MoveOrPush.getMoveOrPush(direction);
			Board clone = board.clone();
			if (!action.isPossible(board)) continue;
			if (action.getType(board) == EActionType.MOVE) {
				IAction opposite = MoveOrPush.getMoveOrPush(direction.opposite());
				action.perform(clone);
				opposite.perform(clone);
				if (!board.equalsState(clone)) {
					throw new RuntimeException("MOVE BACK AND FORTH FAILED!");
				}
			} else {
				// we can just push
				action.perform(clone);
			}
		}
	}
	
	// ===============
	// BOARD - COMPACT
	// ===============

	public void validateBoardCompact() {
		BoardCompact compact = board.makeBoardCompact();
				
		BoardCompact clone = compact.clone();
		checkEqual(compact, clone, "CLONING");
				
		for (int x = 0; x < compact.width(); ++x) {
			for (int y = 0; y < compact.height(); ++y) {
				clone = compact.clone();
				
				if (CTile.isFree(clone.tile(x, y))) {
					clone.movePlayer(clone.playerX, clone.playerY, x, y);
				}
				
				// CHECK CLONING				
				BoardCompact clone2 = clone.clone();
				checkEqual(clone, clone2, "CLONING");
				
				// TEST ACTIONS
				testPlayerActions(clone);
			}
		}		
	}
	
	private void checkEqual(BoardCompact b1, BoardCompact b2, String msg) {
		if (!b1.equalsState(b2)) {
			throw new RuntimeException("NOT STATE EQUAL: " + msg);
		}
		if (b1.hashCode() != b2.hashCode()) {
			throw new RuntimeException("HASH CODE DIFFERS: " + msg);
		}
		if (!b1.equals(b2)) {
			throw new RuntimeException("NOT EQUAL: " + msg);
		}
	}
	
	private void testPlayerActions(BoardCompact board) {
		for (EDirection direction : EDirection.arrows()) {
			BoardCompact clone = board.clone();
			
			CAction action = CMove.getAction(direction);
			
			if (action.isPossible(clone)) {
				action.perform(clone);
				action.reverse(clone);
				checkEqual(board, clone, "ACTION: " + action);
			}
			
			action = CPush.getAction(direction);
			if (action.isPossible(clone)) {
				action.perform(clone);
				action.reverse(clone);
				checkEqual(board, clone, "ACTION: " + action);
			}
		}
	}
	
	// ==================
	// BOARD - COMPRESSED
	// ==================

	public void validateBoardCompressed() {
		throw new RuntimeException("NOT IMPLEMENTED YET");
	}

	// ============
	// BOARD - SLIM
	// ============
	
	public void validateSlimBoard() {
		throw new RuntimeException("NOT IMPLEMENTED YET");
	}
		

}
