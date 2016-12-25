package cz.sokoban4j.simulation.board.compressed;

import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.compressed.MTile.SubSlimTile;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import cz.sokoban4j.simulation.board.oop.ESpace;
import cz.sokoban4j.simulation.board.slim.BoardSlim;
import cz.sokoban4j.simulation.board.slim.STile;

/**
 * Even more compact board than {@link BoardCompact}. Ignores colors of boxes and places.
 * Can be used for levels with only one type of boxes and places.
 * <br/>
 * Roughly 50% memory-wiser representation then {@link BoardSlim}, but slightly slower due to div/mod operations; see {@link MTile}.
 */
public class BoardCompressed {
	
	private Integer hash = null;
	
	public int[][] tiles;
	
	public int playerX;
	public int playerY;
	
	public int boxCount;
	public int boxInPlaceCount;
	
	private BoardCompressed() {
	}
	
	public BoardCompressed(int width, int height) {
		tiles = new int[width / 2 + width % 2][height / 2 + height % 2];
		for (int x = 0; x < getCompressedWidth(); ++x) {
			for (int y = 0; y < getCompressedHeight(); ++y) {
				tiles[x][y] = 0;
			}			
		}
	}
	
	@Override
	public BoardCompressed clone() {
		BoardCompressed result = new BoardCompressed();
		result.tiles = new int[getCompressedWidth()][getCompressedHeight()];
		for (int x = 0; x < getCompressedWidth(); ++x) {
			for (int y = 0; y < getCompressedHeight(); ++y) {
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
			for (int x = 0; x < getCompressedWidth(); ++x) {
				for (int y = 0; y < getCompressedHeight(); ++y) {
					hash += 27 * tiles[x][y];
				}			
			}
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof BoardCompressed)) return false;
		if (obj.hashCode() != hashCode()) return false;
		BoardCompressed other = (BoardCompressed) obj;		
		if (getCompressedWidth() != other.getCompressedWidth() || getCompressedHeight() != other.getCompressedHeight()) return false;
		for (int x = 0; x < getCompressedWidth(); ++x) {
			for (int y = 0; y < getCompressedHeight(); ++y) {
				if (tiles[x][y] != other.tiles[x][y]) return false;
			}			
		}
		return super.equals(obj);
	}
	
	public int width() {
		return getCompressedWidth() * 2;
	}
	
	public int height() {
		return getCompressedHeight() * 2;
	}
	
	public int getCompressedWidth() {
		return (int)tiles.length;		
	}
	
	public int getCompressedHeight() {
		return (int)tiles[0].length;
	}
	
	public int tile(int x, int y) {
		return tiles[x / 2][y / 2];
	}
	
	public int tile2x2(int cX, int cY) {
		return tiles[cX][cY];
	}
		
	public void movePlayer(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		SubSlimTile sourceSubSlimTile = MTile.getSubSlimTile(sourceTileX, sourceTileY);
		SubSlimTile targetSubSlimTile = MTile.getSubSlimTile(targetTileX, targetTileY);
		
		int entity = tiles[sourceTileX][sourceTileY] & sourceSubSlimTile.getSomeEntityFlag();
		
		tiles[targetTileX][targetTileY] &= targetSubSlimTile.getNullifyEntityFlag();
		tiles[targetTileX][targetTileY] |= entity;
		
		tiles[sourceTileX][sourceTileY] &= sourceSubSlimTile.getNullifyEntityFlag();
		tiles[sourceTileX][sourceTileY] |= sourceSubSlimTile.getNoneFlag();	
		
		playerX = targetTileX;
		playerY = targetTileY;
	}
	
	public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		SubSlimTile sourceSubSlimTile = MTile.getSubSlimTile(sourceTileX, sourceTileY);
		SubSlimTile targetSubSlimTile = MTile.getSubSlimTile(targetTileX, targetTileY);
		
		int entity = tiles[sourceTileX][sourceTileY] & sourceSubSlimTile.getSomeEntityFlag();
		
		if ((tiles[targetTileX][targetTileY] & targetSubSlimTile.getPlaceFlag()) > 0) {
			++boxInPlaceCount;
		}
		tiles[targetTileX][targetTileY] &= targetSubSlimTile.getNullifyEntityFlag();
		tiles[targetTileX][targetTileY] |= entity;
		
		if ((tiles[sourceTileX][sourceTileY] & sourceSubSlimTile.getPlaceFlag()) > 0) {
			--boxInPlaceCount;
		}
		tiles[sourceTileX][sourceTileY] &= sourceSubSlimTile.getNullifyEntityFlag();
		tiles[sourceTileX][sourceTileY] |= sourceSubSlimTile.getNoneFlag();
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
				SubSlimTile subSlimTile = MTile.getSubSlimTile(x, y);
								
				EEntity entity = EEntity.fromSlimFlag(tiles[x/2][y/2] >> subSlimTile.getSlimFlagShift());
				EPlace place = EPlace.fromSlimFlag(tiles[x/2][y/2] >> subSlimTile.getSlimFlagShift());
				ESpace space = ESpace.fromSlimFlag(tiles[x/2][y/2] >> subSlimTile.getSlimFlagShift());
				
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

