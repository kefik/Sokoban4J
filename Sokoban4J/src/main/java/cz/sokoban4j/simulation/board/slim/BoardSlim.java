package cz.sokoban4j.simulation.board.slim;

import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import cz.sokoban4j.simulation.board.oop.ESpace;

/**
 * Even more compact board than {@link BoardCompact}. Ignores colors of boxes and places.
 * Can be used for levels with only one type of boxes and places.
 * <br/>
 * Roughly 50% memory-wiser representation then {@link BoardCompact}.
 */
public class BoardSlim {
	
	private Integer hash = null;
	
	public byte[][] tiles;
	
	public byte playerX;
	public byte playerY;
	
	public byte boxCount;
	public byte boxInPlaceCount;
	
	private BoardSlim() {
	}
	
	public BoardSlim(byte width, byte height) {
		tiles = new byte[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = 0;
			}			
		}
	}
	
	@Override
	public BoardSlim clone() {
		BoardSlim result = new BoardSlim();
		result.tiles = new byte[width()][height()];
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				result.tiles[x][y] = tiles[x][y];
			}			
		}
		result.playerX = playerX;
		result.playerY = playerY;
		result.boxCount = boxCount;
		result.boxInPlaceCount = boxInPlaceCount;
		result.hash = hash;
		return result;
	}
	
	@Override
	public int hashCode() {
		if (hash == null) {
			hash = 0;
			for (byte x = 0; x < width(); ++x) {
				for (byte y = 0; y < height(); ++y) {
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
		if (!(obj instanceof BoardSlim)) return false;		
		BoardSlim other = (BoardSlim) obj;		
		if (width() != other.width() || height() != other.height()) return false;
		for (byte x = 0; x < width(); ++x) {
			for (byte y = 0; y < height(); ++y) {
				if (tiles[x][y] != other.tiles[x][y]) return false;
			}			
		}
		return true;
	}
	
	public byte width() {
		return (byte)tiles.length;		
	}
	
	public byte height() {
		return (byte)tiles[0].length;
	}
	
	public byte tile(byte x, byte y) {
		return tiles[x][y];
	}
	
	public byte tile(int x, int y) {
		return tiles[x][y];
	}
	
	public void movePlayer(byte sourceTileX, byte sourceTileY, byte targetTileX, byte targetTileY) {
		byte entity = (byte) (tiles[sourceTileX][sourceTileY] & STile.SOME_ENTITY_FLAG);
		
		tiles[targetTileX][targetTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		tiles[sourceTileX][sourceTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= STile.NONE_FLAG;	
		
		playerX = targetTileX;
		playerY = targetTileY;
	}
	
	public void moveBox(byte sourceTileX, byte sourceTileY, byte targetTileX, byte targetTileY) {
		byte entity = (byte)(tiles[sourceTileX][sourceTileY] & STile.SOME_ENTITY_FLAG);
		
		if ((tiles[targetTileX][targetTileY] & STile.PLACE_FLAG) > 0) {
			++boxInPlaceCount;
		}
		tiles[targetTileX][targetTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		if ((tiles[sourceTileX][sourceTileY] & STile.PLACE_FLAG) > 0) {
			--boxInPlaceCount;
		}
		tiles[sourceTileX][sourceTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= STile.NONE_FLAG;
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
				EEntity entity = EEntity.fromSlimFlag(tiles[x][y]);
				EPlace place = EPlace.fromSlimFlag(tiles[x][y]);
				ESpace space = ESpace.fromSlimFlag(tiles[x][y]);
				
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

