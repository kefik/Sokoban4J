package cz.sokoban4j.simulation.board.compact;

import cz.sokoban4j.simulation.board.compressed.BoardCompressed;
import cz.sokoban4j.simulation.board.compressed.MTile;
import cz.sokoban4j.simulation.board.compressed.MTile.SubSlimTile;
import cz.sokoban4j.simulation.board.minimal.StateMinimal;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import cz.sokoban4j.simulation.board.oop.ESpace;
import cz.sokoban4j.simulation.board.slim.BoardSlim;
import cz.sokoban4j.simulation.board.slim.STile;

/**
 * More memory-compact representation of OOP-bulky {@link Board}.
 * 
 * BEWARE: once {@link #hashCode()} is called and you use {@link #moveBox(int, int, int, int)} or {@link #movePlayer(int, int, int, int)} it will
 *         force {@link #hashCode()} recomputation.
 * 
 * @author Jimmy
 */
public class BoardCompact implements Cloneable {

	private Integer hash = null;
	
	/**
	 * Compact representation of tiles.
	 */
	public int[][] tiles;
	
	public int playerX;
	public int playerY;
	
	public int boxCount;
	public int boxInPlaceCount;
	
	private BoardCompact() {
	}
	
	public BoardCompact(int width, int height) {
		tiles = new int[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = 0;
			}			
		}
	}
	
	@Override
	public BoardCompact clone() {
		BoardCompact result = new BoardCompact();
		result.tiles = new int[width()][height()];
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				result.tiles[x][y] = tiles[x][y];
			}			
		}
		result.playerX = playerX;
		result.playerY = playerY;
		result.boxCount = boxCount;
		result.boxInPlaceCount = boxInPlaceCount;
		return result;
	}
	
	@Override
	public int hashCode() {
		if (hash == null) {
			hash = 0;
			for (int x = 0; x < width(); ++x) {
				for (int y = 0; y < height(); ++y) {
					hash += (290317 * x + 97 * y) * tiles[x][y];
				}		
			}
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.hashCode() != hashCode()) return false;
		if (!(obj instanceof BoardCompact)) return false;		
		return equalsState((BoardCompact) obj);
	}
	
	public boolean equalsState(BoardCompact other) {
		if (other == null) return false;
		if (width() != other.width() || height() != other.height()) return false;
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				if (tiles[x][y] != other.tiles[x][y]) return false;
			}			
		}
		return true;
	}
	
	public int width() {
		return tiles.length;		
	}
	
	public int height() {
		return tiles[0].length;
	}
	
	public int tile(int x, int y) {
		return tiles[x][y];
	}
	
	/**
	 * Fair warning: by moving the player you're invalidating {@link #hashCode()}...
	 * @param sourceTileX
	 * @param sourceTileY
	 * @param targetTileX
	 * @param targetTileY
	 */
	public void movePlayer(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		int entity = tiles[sourceTileX][sourceTileY] & EEntity.SOME_ENTITY_FLAG;
		
		tiles[targetTileX][targetTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		tiles[sourceTileX][sourceTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= EEntity.NONE.getFlag();	
		
		playerX = targetTileX;
		playerY = targetTileY;
		
		hash = null;
	}
	
	/**
	 * Fair warning: by moving the box you're invalidating {@link #hashCode()}...
	 * @param sourceTileX
	 * @param sourceTileY
	 * @param targetTileX
	 * @param targetTileY
	 */
	public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		int entity = tiles[sourceTileX][sourceTileY] & EEntity.SOME_ENTITY_FLAG;
		int boxNum = CTile.getBoxNum(tiles[sourceTileX][sourceTileY]);

		if (CTile.forBox(boxNum, tiles[targetTileX][targetTileY]) || CTile.forAnyBox(tiles[targetTileX][targetTileY])) {
			++boxInPlaceCount;
		}
		tiles[targetTileX][targetTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		if (CTile.forBox(boxNum, tiles[sourceTileX][sourceTileY]) || CTile.forAnyBox(tiles[sourceTileX][sourceTileY])) {
			--boxInPlaceCount;
		}
		tiles[sourceTileX][sourceTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= EEntity.NONE.getFlag();
		
		hash = null;
	}
	
	/**
	 * Whether the board is in WIN-STATE == all boxes are in correct places.
	 * 
	 * @return
	 */
	public boolean isVictory() {
		return boxCount == boxInPlaceCount;
	}
	
	/**
	 * Adds "state" to this board, has sense only if {@link #unsetState(StateMinimal)} has been previously called.
	 * @param state
	 */
	public void setState(StateMinimal state) {
		playerX = state.getX(state.positions[0]);
		playerY = state.getY(state.positions[1]);
		
		for (int i = 1; i < state.positions.length; ++i) {
			tiles[state.getX(state.positions[i])][state.getY(state.positions[i])] &= EEntity.BOX_1.getFlag();
			if (CTile.forSomeBox(tiles[state.getX(state.positions[i])][state.getY(state.positions[i])])) ++boxInPlaceCount;
		}
	}
	
	/**
	 * Removes "dynamic" information from the board, leaves statics only. Use {@link #setState(StateMinimal)} to put the state back...
	 * @param state
	 */
	public void unsetState(StateMinimal state) {
		playerX = -1;
		playerY = -1;
		boxInPlaceCount = -1;
		for (int i = 1; i < state.positions.length; ++i) {
			tiles[state.getX(state.positions[i])][state.getY(state.positions[i])] &= EEntity.NULLIFY_ENTITY_FLAG;
		}
	}
	
	/**
	 * Prints the board into {@link System#out}.
	 */
	public void debugPrint() {
		System.out.print(getBoardString());
		System.out.println();
	}
	
	/**
	 * String representation of the board.
	 * @return
	 */
	public String getBoardString() {
		StringBuffer sb = new StringBuffer();
		
		for (int y = 0; y < height(); ++y) {
			if (y != 0) sb.append("\n");
			for (int x = 0; x < width(); ++x) {
				EEntity entity = EEntity.fromFlag(tiles[x][y]);
				EPlace place = EPlace.fromFlag(tiles[x][y]);
				ESpace space = ESpace.fromFlag(tiles[x][y]);
				
				if (entity != null && entity != EEntity.NONE) {
					sb.append(entity.getSymbol());
				} else
				if (place != null && place != EPlace.NONE) {
					sb.append(place.getSymbol());
				} else
				if (space != null) {
					sb.append(space.getSymbol());
				} else {
					sb.append("?");
				}
			}			
		}
		
		return sb.toString();
	}
	
	public BoardCompressed makeBoardCompressed() {
		BoardCompressed result = new BoardCompressed(width(), height());
		result.boxCount = boxCount;
		result.boxInPlaceCount = boxInPlaceCount;
		result.playerX = playerX;
		result.playerY = playerY;
		
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				SubSlimTile sst = MTile.getSubSlimTile(x, y);
				int tx = x / 2;
				int ty = y / 2;
				result.tiles[tx][ty] |= computeCompressedTile(sst, x, y);
			}
		}
		
		return result;
	}
	
	public int computeCompressedTile(SubSlimTile subSlimTile, int x, int y) {
		int compact = tile(x, y);
		
		int result = 0;
		
		if (CTile.forSomeBox(compact)) result |= subSlimTile.getPlaceFlag();		
		if (CTile.isFree(compact)) return result;
		if (CTile.isWall(compact)) {
			result |= subSlimTile.getWallFlag();
			return result;
		}
		if (CTile.isSomeBox(compact)) {
			result |= subSlimTile.getBoxFlag();
			return result;
		}		
		if (CTile.isPlayer(compact)) {
			result |= subSlimTile.getPlayerFlag();
			return result;
		}
		
		return result;
	}
	
	public BoardSlim makeBoardSlim() {
		BoardSlim result = new BoardSlim((byte)width(), (byte)height());
		result.boxCount = (byte)boxCount;
		result.boxInPlaceCount = (byte)boxInPlaceCount;
		result.playerX = (byte)playerX;
		result.playerY = (byte)playerY;
		
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				result.tiles[x][y] = computeSlimTile(x, y);
			}
		}
		
		return result;
	}
	
	public byte computeSlimTile(int x, int y) {
		int compact = tile(x, y);
		
		byte result = 0;
		
		if (CTile.forSomeBox(compact)) result |= STile.PLACE_FLAG;		
		if (CTile.isFree(compact)) return result;
		if (CTile.isWall(compact)) {
			result |= STile.WALL_FLAG;
			return result;
		}
		if (CTile.isSomeBox(compact)) {
			result |= STile.BOX_FLAG;
			return result;
		}		
		if (CTile.isPlayer(compact)) {
			result |= STile.PLAYER_FLAG;
			return result;
		}
		
		return result;
	}
	
	
	@Override
	public String toString() {
		return "BoardCompact[\n" + getBoardString() + "\n]";
	}
	
}
