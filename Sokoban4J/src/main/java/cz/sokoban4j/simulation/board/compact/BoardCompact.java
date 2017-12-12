package cz.sokoban4j.simulation.board.compact;

import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import cz.sokoban4j.simulation.board.oop.ESpace;

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
	
	public void debugPrint() {
		for (int y = 0; y < height(); ++y) {
			for (int x = 0; x < width(); ++x) {
				EEntity entity = EEntity.fromFlag(tiles[x][y]);
				EPlace place = EPlace.fromFlag(tiles[x][y]);
				ESpace space = ESpace.fromFlag(tiles[x][y]);
				
				if (entity != null && entity != EEntity.NONE) {
					System.out.print(entity.getSymbol());
				} else
				if (place != null && place != EPlace.NONE) {
					System.out.print(place.getSymbol());
				} else
				if (space != null) {
					System.out.print(space.getSymbol());
				} else {
					System.out.print("?");
				}
			}
			System.out.println();
		}
	}
	
}
