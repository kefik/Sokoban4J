package cz.sokoban4j.simulation.board.minimal;

import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.compressed.StateCompressed;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import cz.sokoban4j.simulation.board.oop.ESpace;
import cz.sokoban4j.simulation.board.oop.entities.Entity;

/**
 * Runtime-part of the Sokoban game state (excluding static board configuration, just positions).
 * 
 * Can be used to mark the state of the board (e.g., create list of no-good states, etc.)
 * 
 * Cannot be used with multicolored-boxes.
 *
 * @author Jimmy
 */
public class StateMinimal {
	
	public static final int Y_MASK = (Integer.MAX_VALUE >> 16);
	
	/**
	 * PLAYER
	 * [0] = player-x, player-y
	 * 
	 * BOXES (for n>0)
	 * [n] = nth-box-x (the first 16bits), nth-box-y (the second 16bits)
	 */
	public int[] positions;
	
	private Integer hash = null;
	
	/**
	 * Extract minimal state from 'board'
	 * @param board
	 */
	public StateMinimal(Board board) {
		int elements = 1 + board.boxes.size();
		positions = new int[elements];
		positions[0] = getPacked(board.player.getTileX(), board.player.getTileY());
		int index = 1;
		for (int x = 0; x < board.width; ++x) {
			for (int y = 0; y < board.height; ++y) {
				if (board.tile(x, y).isSomeBox()) {
					positions[index] = getPacked(x, y);
					++index;
				}
			}
		}			
	}
	
	/**
	 * Extract minimal state from 'board'
	 * @param board
	 */
	public StateMinimal(BoardCompact board) {
		int elements = 1 + board.boxCount;
		positions = new int[elements];
		positions[0] = getPacked(board.playerX, board.playerY);
		int index = 1;
		for (int x = 0; x < board.width(); ++x) {
			for (int y = 0; y < board.height(); ++y) {
				if (CTile.isSomeBox(board.tile(x, y))) {
					positions[index] = getPacked(x, y);
					++index;
				}
			}
		}		
	}
	
	/**
	 * Packs [x;y] into single integer.
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPacked(int x, int y) {
		return x << 16 | y;
	}
	
	/**
	 * Returns X coordinate from packed value.
	 * @param packed
	 * @return
	 */
	public int getX(int packed) {
		return packed >> 16;
	}
	
	/**
	 * Returns Y coordinate from packed value.
	 * @param packed
	 * @return
	 */
	public int getY(int packed) {
		return packed & Y_MASK;
	}
	
	public int hashCode() {
		if (hash == null) {
			hash = (getX(positions[0]) + 5) * 290317 * getX(positions[1]) + (getY(positions[0]) + 7) * 290317 * getY(positions[1]);			
			for (int i = 1; i < positions.length; ++i) {
				hash += getX(positions[i]) * 290317 + getY(positions[i]) * 97;
			}
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.hashCode() != hashCode()) return false;		
		if (!(obj instanceof StateMinimal)) return false;
		StateMinimal other = (StateMinimal) obj;		
		if (positions.length != other.positions.length) return false;
		for (int index = 0; index < positions.length; ++index) {
			if (positions[index] != other.positions[index]) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "StateMinimal[" + hashCode() + "]";
	}
	
	
}
