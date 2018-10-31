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
	
	/**
	 * Each int is storing information about 4 tiles (2x2 square), therefore the dimension of the array is 1/4 than compared to other BoardXXX implementations.
	 * You have to access them through {@link SubSlimTile} flags, get one by calling {@link MTile#getSubSlimTile(int, int)}.
	 */
	public int[][] tiles;
	
	/**
	 * Note that this X is "full-X", i.e., not index into {@link #tiles} but actual full board X position.
	 */
	public int playerX;
	
	/**
	 * Note that this Y is "full-Y", i.e., not index into {@link #tiles} but actual full board Y position.
	 */
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
		result.hash = hash;
		return result;
	}
	
	@Override
	public int hashCode() {
		if (hash == null) {
			hash = 0;
			for (int x = 0; x < getCompressedWidth(); ++x) {
				for (int y = 0; y < getCompressedHeight(); ++y) {
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
		if (!(obj instanceof BoardCompressed)) return false;		
		BoardCompressed other = (BoardCompressed) obj;		
		if (getCompressedWidth() != other.getCompressedWidth() || getCompressedHeight() != other.getCompressedHeight()) return false;
		for (int x = 0; x < getCompressedWidth(); ++x) {
			for (int y = 0; y < getCompressedHeight(); ++y) {
				if (tiles[x][y] != other.tiles[x][y]) return false;
			}			
		}
		return true;
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
		
		int stx = sourceTileX / 2;
		int sty = sourceTileY / 2;
		int ttx = targetTileX / 2;
		int tty = targetTileY / 2;
				
		tiles[ttx][tty] &= targetSubSlimTile.getNullifyEntityFlag();
		tiles[ttx][tty] |= targetSubSlimTile.getPlayerFlag();
		
		tiles[stx][sty] &= sourceSubSlimTile.getNullifyEntityFlag();
		
		playerX = targetTileX;
		playerY = targetTileY;
	}
	
	public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		SubSlimTile sourceSubSlimTile = MTile.getSubSlimTile(sourceTileX, sourceTileY);
		SubSlimTile targetSubSlimTile = MTile.getSubSlimTile(targetTileX, targetTileY);
		
		int stx = sourceTileX / 2;
		int sty = sourceTileY / 2;
		int ttx = targetTileX / 2;
		int tty = targetTileY / 2;
		
		if ((tiles[ttx][tty] & targetSubSlimTile.getPlaceFlag()) > 0) {
			++boxInPlaceCount;
		}
		tiles[ttx][tty] &= targetSubSlimTile.getNullifyEntityFlag();
		tiles[ttx][tty] |= targetSubSlimTile.getBoxFlag();
		
		if ((tiles[stx][sty] & sourceSubSlimTile.getPlaceFlag()) > 0) {
			--boxInPlaceCount;
		}
		tiles[stx][sty] &= sourceSubSlimTile.getNullifyEntityFlag();
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

