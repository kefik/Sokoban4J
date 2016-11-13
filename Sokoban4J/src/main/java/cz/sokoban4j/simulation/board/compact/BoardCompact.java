package cz.sokoban4j.simulation.board.compact;

import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.Tile;
import cz.sokoban4j.simulation.board.oop.entities.Entity;

public class BoardCompact {

	public int[][] tiles;
	
	public int playerX;
	public int playerY;
	
	public int boxCount;
	public int boxInPlaceCount;
	
	public BoardCompact(int width, int height) {
		tiles = new int[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = 0;
			}			
		}
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
	
	public void movePlayer(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		int entity = tiles[sourceTileX][sourceTileY] & EEntity.SOME_ENTITY_FLAG;
		
		tiles[targetTileX][targetTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		tiles[sourceTileX][sourceTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= EEntity.NONE.getFlag();		
	}
	
	public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
		int entity = tiles[sourceTileX][sourceTileY] & EEntity.SOME_ENTITY_FLAG;
		int boxNum = CTile.getBoxNum(tiles[sourceTileX][sourceTileY]);

		if (CTile.forBox(boxNum, tiles[targetTileX][targetTileX])) {
			++boxInPlaceCount;
		}
		tiles[targetTileX][targetTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		if (CTile.forBox(boxNum, tiles[sourceTileX][sourceTileY])) {
			--boxInPlaceCount;
		}
		tiles[sourceTileX][sourceTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= EEntity.NONE.getFlag();
	}
	
	/**
	 * Whether the board is in WIN-STATE == all boxes are in correct places.
	 * 
	 * @return
	 */
	public boolean isVictory() {
		return boxCount == boxInPlaceCount;
	}
	
}
