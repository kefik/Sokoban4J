package cz.sokoban4j.simulation.board.compressed;

import cz.sokoban4j.simulation.board.slim.STile;

/**
 * Idea of compressed TILE is that you have 4 sub-slim-tiles within single INTEGER.
 * <br/> 
 * FLAG[0] =&gt; SubSlimTile[0,0]<br/>
 * FLAG[1] =&gt; SubSlimTile[1,0]<br/>
 * FLAG[2] =&gt; SubSlimTile[0,1]<br/>
 * FLAG[3] =&gt; SubSlimTile[1,1]<br/>
 *  
 * @author Jimmy
 */
public class MTile {
	
	public static final class SubSlimTile {
		
		private final String name;
		private final int shift;
		private final int boxFlag;
		private final int placeFlag;
		private final int playerFlag;
		private final int wallFlag;
		private final int someEntityFlag;
		private final int nullifyEntityFlag;
		
		public SubSlimTile(String name, int shift, int boxFlag, int placeFlag, int playerFlag, int wallFlag, int someEntityFlag) {
			this.name = name;
			this.shift = shift;
			this.boxFlag = boxFlag;
			this.placeFlag = placeFlag;
			this.playerFlag = playerFlag;
			this.wallFlag = wallFlag;
			this.someEntityFlag = someEntityFlag;
			this.nullifyEntityFlag = Integer.MAX_VALUE ^ someEntityFlag;
		}
		
		public final String getName() {
			return name;
		}
		
		public final int getSlimFlagShift() {
			return shift;
		}
		
		public final int getNoneFlag() {
			return 0;
		}
		public final int getBoxFlag() {
			return boxFlag;
		}
		public final int getPlaceFlag() {
			return placeFlag;
		}
		public final int getPlayerFlag() {
			return playerFlag;
		}
		public final int getWallFlag() {
			return wallFlag;
		}
		public final int getSomeEntityFlag() {
			return someEntityFlag;
		}
		public final int getNullifyEntityFlag() {
			return nullifyEntityFlag;
		}
		
		@Override
		public String toString() {
			return "SubSlimTile[" + name + "]";
		}
		
	}
	
	public static final SubSlimTile[] SUB_SLIM_TILES = new SubSlimTile[] {
			
			new SubSlimTile("0,0", 0, STile.BOX_FLAG,       STile.PLACE_FLAG,       STile.PLAYER_FLAG,       STile.WALL_FLAG,        STile.BOX_FLAG | STile.PLAYER_FLAG),
			new SubSlimTile("1,0", 4, STile.BOX_FLAG << 4,  STile.PLACE_FLAG << 4,  STile.PLAYER_FLAG << 4,  STile.WALL_FLAG << 4,  (STile.BOX_FLAG | STile.PLAYER_FLAG) << 4),
			new SubSlimTile("0,1", 8, STile.BOX_FLAG << 8,  STile.PLACE_FLAG << 8,  STile.PLAYER_FLAG << 8,  STile.WALL_FLAG << 8,  (STile.BOX_FLAG | STile.PLAYER_FLAG) << 8),
			new SubSlimTile("1,1", 12, STile.BOX_FLAG << 12, STile.PLACE_FLAG << 12, STile.PLAYER_FLAG << 12, STile.WALL_FLAG << 12, (STile.BOX_FLAG | STile.PLAYER_FLAG) << 12)
			
	};
	
	public static int getSlimTileIndex(int x, int y) {
		// 0,0 => 0
		// 1,0 => 1
		// 0,1 => 2
		// 1,1 => 3
		return (y % 2) * 2 + x % 2;
	}
	
	public static SubSlimTile getSubSlimTile(int x, int y) {
		return SUB_SLIM_TILES[getSlimTileIndex(x, y)];
	}
	
	private static boolean isThis(int whatFlag, int tileCompressedFlag) {
		return (whatFlag & tileCompressedFlag) != 0;
	}
	
	public static boolean isFree(SubSlimTile subSlimTile, int tileCompressedFlag) {
		return !isThis(subSlimTile.getWallFlag(), tileCompressedFlag) && !isThis(subSlimTile.getSomeEntityFlag(), tileCompressedFlag);
	}
	
	public static boolean isWall(SubSlimTile subSlimTile, int tileCompressedFlag) {
		return isThis(subSlimTile.getWallFlag(), tileCompressedFlag);
	}
	
	public static boolean isPlayer(SubSlimTile subSlimTile, int tileCompressedFlag) {
		return isThis(subSlimTile.getPlayerFlag(), tileCompressedFlag);
	}
	
	public static boolean isBox(SubSlimTile subSlimTile, int tileCompressedFlag) {
		return isThis(subSlimTile.getBoxFlag(), tileCompressedFlag);
	}
	
	public static boolean forBox(SubSlimTile subSlimTile, int tileCompressedFlag) {
		return isThis(subSlimTile.getPlaceFlag(), tileCompressedFlag);
	}
	
}
