package cz.sokoban4j.simulation.board.compact;

import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.EPlace;
import cz.sokoban4j.simulation.board.oop.ESpace;
import cz.sokoban4j.simulation.board.oop.Tile;

/**
 * CTile stands for "Compact Tile". It contains many static methods for querying "tileFlag", obtainable via {@link Tile#computeTileFlag()} or {@link BoardCompact#tile(int, int)}.
 * 
 * @author Jimmy
 */
public class CTile {
	
	private static final int spaceFree;
	private static final int spaceWall;
	private static final int entityNone;
	private static final int entityPlayer;
	private static final int entitySomeBox;	
	private static final int[] entitySpecificBox;
	private static final int placeSomeBox;
	private static final int placeAnyBox;
	private static final int[] placeSpecificBox;
	
	static {
		spaceFree = ESpace.FREE.getFlag();
		spaceWall = ESpace.WALL.getFlag();
		
		entityNone = EEntity.NONE.getFlag();
		
		entityPlayer = EEntity.PLAYER.getFlag();
		
		entitySomeBox = EEntity.SOME_BOX_FLAG;
		
		entitySpecificBox = new int[]{0, EEntity.BOX_1.getFlag(), EEntity.BOX_2.getFlag(), EEntity.BOX_3.getFlag(), EEntity.BOX_4.getFlag(), EEntity.BOX_5.getFlag(), EEntity.BOX_6.getFlag() }; 
		
		placeSomeBox = EPlace.SOME_BOX_PLACE_FLAG;
		
		placeAnyBox = EPlace.BOX_ANY.getFlag();

		placeSpecificBox = new int[]{0, EPlace.BOX_1.getFlag(), EPlace.BOX_2.getFlag(), EPlace.BOX_3.getFlag(), EPlace.BOX_4.getFlag(), EPlace.BOX_5.getFlag(), EPlace.BOX_6.getFlag() };
	}
	
	private static boolean isThis(int whatFlag, int tileFlag) {
		return (whatFlag & tileFlag) != 0;
	}
	
	/**
	 * Is this tile free == no wall, no box, no player ?
	 * @param tileFlag
	 * @return
	 */
	public static boolean isFree(int tileFlag) {
		return isThis(spaceFree, tileFlag) && isThis(entityNone, tileFlag);
	}
	
	/**
	 * Can a player can pass through this tile (a tile containing a player is considered walkable as well)?
	 * @param tileFlag
	 * @return
	 */
	public static boolean isWalkable(int tileFlag) {
		return isFree(tileFlag) || isPlayer(tileFlag);
	}
	
	/**
	 * Is this tile (an impassable) wall?
	 * @param tileFlag
	 * @return
	 */
	public static boolean isWall(int tileFlag) {
		return isThis(spaceWall, tileFlag);
	}
	
	/**
	 * Does this tile contain a player?
	 * @param tileFlag
	 * @return
	 */
	public static boolean isPlayer(int tileFlag) {
		return isThis(entityPlayer, tileFlag);
	}
	
	/**
	 * Does this tile contain some box of any color?
	 * @param tileFlag
	 * @return
	 */
	public static boolean isSomeBox(int tileFlag) {
		return isThis(entitySomeBox, tileFlag);
	}
	
	/**
	 * Does this tile contain a specific 'boxNum' box?
	 * @param boxNum
	 * @param tileFlag
	 * @return
	 */
	public static boolean isBox(int boxNum, int tileFlag) {
		return isThis(entitySpecificBox[boxNum], tileFlag);
	}
	
	/**
	 * There is place / target for "some" box, may be specific one.
	 * 
	 * I.e., whether the tile flag contains one of {@link EPlace#BOX_1}, {@link EPlace#BOX_2},
	 * {@link EPlace#BOX_3}, {@link EPlace#BOX_4}, {@link EPlace#BOX_5}, {@link EPlace#BOX_6}, {@link EPlace#BOX_ANY}. 
	 * 
	 * @param tileFlag
	 * @return
	 */
	public static boolean forSomeBox(int tileFlag) {
		return isThis(placeSomeBox, tileFlag);
	}
	
	/**
	 * There is a place / target for "any" box, you can place there "any" box you want.
	 * 
	 * WARNING: You probably want to use {@link #forSomeBox(int)} instead! 
	 * "ANY" means {@link EPlace#BOX_ANY} only, which is different from {@link #forSomeBox(int)}, which means all box places.
	 *  
	 * @param tileFlag
	 * @return
	 */
	public static boolean forAnyBox(int tileFlag) {
		return isThis(placeAnyBox, tileFlag);
	}
	
	/**
	 * Is 'tile' specifically for box of number 'boxNumber'?
	 * @param boxNum
	 * @param tileFlag
	 * @return
	 */
	public static boolean forBox(int boxNum, int tileFlag) {
		if (boxNum < 1) {
			return false;
		}
		return isThis(placeSpecificBox[boxNum], tileFlag);
	}

	/**
	 * If there is a box encoded within 'tileFlag', it returns its number (for the meaning see {@link EEntity}).
	 * If not, it returns -1.
	 *  
	 * @param tileFlag
	 * @return
	 */
	public static int getBoxNum(int tileFlag) {
		if ((tileFlag & EEntity.BOX_1.getFlag()) != 0) return 1;
		if ((tileFlag & EEntity.BOX_2.getFlag()) != 0) return 2;
		if ((tileFlag & EEntity.BOX_3.getFlag()) != 0) return 3;
		if ((tileFlag & EEntity.BOX_4.getFlag()) != 0) return 4;
		if ((tileFlag & EEntity.BOX_5.getFlag()) != 0) return 5;
		if ((tileFlag & EEntity.BOX_6.getFlag()) != 0) return 6;
		return -1;
	}
	
}
