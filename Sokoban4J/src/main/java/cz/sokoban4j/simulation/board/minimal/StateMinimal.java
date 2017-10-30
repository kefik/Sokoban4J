package cz.sokoban4j.simulation.board.minimal;

import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.compressed.StateCompressed;
import cz.sokoban4j.simulation.board.oop.Board;

/**
 * Runtime-part of the Sokoban game state (excluding static board configuration, just positions).
 * 
 * Can be used to mark the state of the board (e.g., create list of no-good states, etc.)
 *
 * @author Jimmy
 */
public class StateMinimal {
	
	/**
	 * PLAYER
	 * [0][1] = player-x, player-y
	 * 
	 * BOXES (for n>0)
	 * [2n][2n+1] = box-x, box-y
	 */
	public int[] positions;
	
	private Integer hash = null;
	
	/**
	 * Extract minimal state from 'board'
	 * @param board
	 */
	public StateMinimal(Board board) {
		int elements = 1 + board.boxes.size();
		positions = new int[elements * 2];
		
		positions[0] = board.player.getTileX();
		positions[1] = board.player.getTileY();
		
		for (int i = 0; i < board.boxes.size(); ++i) {
			positions[(i+1)*2]   = board.boxes.get(i).getTileX();
			positions[(i+1)*2+1] = board.boxes.get(i).getTileY();
		}
	}
	
	/**
	 * Extract minimal state from 'board'
	 * @param board
	 */
	public StateMinimal(BoardCompact board) {
		int elements = 1 + board.boxCount;
		positions = new int[elements * 2];

		positions[0] = board.playerX;
		positions[1] = board.playerY;
		
		int index = 1;
		for (int x = 0; x < board.width(); ++x) {
			for (int y = 0; y < board.height(); ++y) {
				if (CTile.isSomeBox(board.tile(x, y))) {
					positions[index*2]   = x;
					positions[index*2+1] = y;
					++index;
				}
			}
		}		
	}
	
	public int hashCode() {
		if (hash == null) {
			hash = 0;
			int[] coefs = new int[] { 290317, 97 };
			int coef = 0;
			for (int p : positions) {
				hash += coefs[coef % 2] * p;
				++coef;
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
	
	
}
