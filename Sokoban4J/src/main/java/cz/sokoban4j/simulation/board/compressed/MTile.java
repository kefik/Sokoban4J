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
		
		private final int shift;
		private final int boxFlag;
		private final int placeFlag;
		private final int playerFlag;
		private final int wallFlag;
		private final int someEntityFlag;
		private final int nullifyEntityFlag;
		
		public SubSlimTile(int shift, int boxFlag, int placeFlag, int playerFlag, int wallFlag, int someEntityFlag) {
			super();
			this.shift = shift;
			this.boxFlag = boxFlag;
			this.placeFlag = placeFlag;
			this.playerFlag = playerFlag;
			this.wallFlag = wallFlag;
			this.someEntityFlag = someEntityFlag;
			this.nullifyEntityFlag = Integer.MAX_VALUE ^ someEntityFlag;
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
		
	}
	
	public static final SubSlimTile[] SUB_SLIM_TILES = new SubSlimTile[] {
			
			new SubSlimTile( 0, STile.BOX_FLAG,       STile.PLACE_FLAG,       STile.PLAYER_FLAG,       STile.WALL_FLAG,        STile.BOX_FLAG | STile.PLAYER_FLAG),
			new SubSlimTile( 4, STile.BOX_FLAG << 4,  STile.PLACE_FLAG << 4,  STile.PLAYER_FLAG << 4,  STile.WALL_FLAG << 4,  (STile.BOX_FLAG | STile.PLAYER_FLAG) << 4),
			new SubSlimTile( 8, STile.BOX_FLAG << 8,  STile.PLACE_FLAG << 8,  STile.PLAYER_FLAG << 8,  STile.WALL_FLAG << 8,  (STile.BOX_FLAG | STile.PLAYER_FLAG) << 8),
			new SubSlimTile(12, STile.BOX_FLAG << 12, STile.PLACE_FLAG << 12, STile.PLAYER_FLAG << 12, STile.WALL_FLAG << 12, (STile.BOX_FLAG | STile.PLAYER_FLAG) << 1)
			
	};
	
	public static int getSlimTileIndex(int width, int height) {
		// 0,0 => 0
		// 1,0 => 1
		// 0,1 => 2
		// 1,1 => 3
		return (height % 2) * 2 + width % 2;
	}
	
	public static SubSlimTile getSubSlimTile(int width, int height) {
		return SUB_SLIM_TILES[getSlimTileIndex(width, height)];
	}
	
	private static boolean isThis(int whatFlag, int tileCompressedFlag) {
		return (whatFlag & tileCompressedFlag) != 0;
	}
	
	public static boolean isFree(SubSlimTile subSlimTile, int tileCompressedFlag) {
		return !isThis(subSlimTile.getWallFlag(), tileCompressedFlag);
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
