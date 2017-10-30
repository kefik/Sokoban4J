package cz.sokoban4j.simulation.board.compressed;

import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compact.CTile;
import cz.sokoban4j.simulation.board.compressed.MTile.SubSlimTile;
import cz.sokoban4j.simulation.board.slim.BoardSlim;
import cz.sokoban4j.simulation.board.slim.STile;

/**
 * To be used for marking state of Sokoban board; can be used only for boards with single type of boxes.
 * 
 * Can be used for boards up-to 2^5x2^5 = 32x32 big.
 * 
 * @author Jimmy
 */
public class StateCompressed {
	
	/**
	 * How many bits are we using for INT (x or y) representation.
	 */
	private static final int INT_SHIFT = 5;
	
	/**
	 * How many positions ( (int,int) pairs ) can be fit into single int number;
	 */
	private static final int POSITIONS_IN_INT = 32 / (INT_SHIFT*2);
	
	/**
	 * This number cannot be hold within our compressed representation.
	 */
	private static final int MAX_POSITION = 1 << INT_SHIFT;
	
	private int[] positions;
	
	private Integer hash;
	
	/**
	 * Creates the state out of the 'board' assuming there are certain number of 'boxes'.
	 * @param board
	 * @param boxes
	 */
	public StateCompressed(BoardCompact board, int boxes) {
		positions = new int[(boxes+1) / (POSITIONS_IN_INT) + ((((boxes+1) / (POSITIONS_IN_INT)) % POSITIONS_IN_INT) == 0 ? 0 : 1)];
		
		for (int i = 0; i < positions.length; ++i) {
			positions[i] = 0;
		}
		
		positions[0] = addPosition(0, board.playerX, board.playerY);
		
		int positionIndex = 0;
		int positionNum = 1;
		
		for (int x = 0; x < board.width(); ++x) {
			for (int y = 0; y < board.height(); ++y) {
				if (CTile.isSomeBox(board.tile(x, y))) {
					positions[positionIndex] = addPosition(positions[positionIndex], x, y);
					++positionNum;
					if (positionNum == POSITIONS_IN_INT) {
						++positionIndex;
						positionNum = 0;
					}
				}
			}
		}
	}
	
	/**
	 * Creates the state out of the 'board' assuming there are certain number of 'boxes'.
	 * @param board
	 * @param boxes
	 */
	public StateCompressed(BoardSlim board, int boxes) {
		positions = new int[(boxes+1) / (POSITIONS_IN_INT) + ((((boxes+1) / (POSITIONS_IN_INT)) % POSITIONS_IN_INT) == 0 ? 0 : 1)];
		
		for (int i = 0; i < positions.length; ++i) {
			positions[i] = 0;
		}
		
		positions[0] = addPosition(0, board.playerX, board.playerY);
		
		int positionIndex = 0;
		int positionNum = 1;
		
		for (int x = 0; x < board.width(); ++x) {
			for (int y = 0; y < board.height(); ++y) {
				if (STile.isBox(board.tile(x, y))) {
					positions[positionIndex] = addPosition(positions[positionIndex], x, y);
					++positionNum;
					if (positionNum == POSITIONS_IN_INT) {
						++positionIndex;
						positionNum = 0;
					}
				}
			}
		}
	}
	
	/**
	 * Creates the state out of the 'board' assuming there are certain number of 'boxes'.
	 * @param board
	 * @param boxes
	 */
	public StateCompressed(BoardCompressed board, int boxes) {
		positions = new int[(boxes+1) / (POSITIONS_IN_INT) + ((((boxes+1) / (POSITIONS_IN_INT)) % POSITIONS_IN_INT) == 0 ? 0 : 1)];
		
		for (int i = 0; i < positions.length; ++i) {
			positions[i] = 0;
		}
		
		positions[0] = addPosition(0, board.playerX, board.playerY);
		
		int positionIndex = 0;
		int positionNum = 1;
		
		for (int x = 0; x < board.width(); ++x) {
			for (int y = 0; y < board.height(); ++y) {
				SubSlimTile subSlimTile = MTile.getSubSlimTile(x, y);
				if (MTile.isBox(subSlimTile, board.tile(x, y))) {
					positions[positionIndex] = addPosition(positions[positionIndex], x, y);
					++positionNum;
					if (positionNum == POSITIONS_IN_INT) {
						++positionIndex;
						positionNum = 0;
					}
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
		if (!(obj instanceof StateCompressed)) return false;
		StateCompressed other = (StateCompressed) obj;		
		if (positions.length != other.positions.length) return false;
		for (int index = 0; index < positions.length; ++index) {
			if (positions[index] != other.positions[index]) return false;
		}
		return true;
	}
	
	private int addPosition(int position, int x, int y) {
		position <<= INT_SHIFT;
		position |= x;
		position <<= INT_SHIFT;
		position |= y;
		return position;
	}
	
	public static boolean isAvailableFor(BoardCompact board) {
		return board.width() < MAX_POSITION && board.height() < MAX_POSITION;
	}
	
	public static boolean isAvailableFor(BoardSlim board) {
		return board.width() < MAX_POSITION && board.height() < MAX_POSITION;
	}
	
	public static boolean isAvailableFor(BoardCompressed board) {
		return board.width() < MAX_POSITION && board.height() < MAX_POSITION;
	}
	
}
