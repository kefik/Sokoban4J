package cz.sokoban4j.simulation.board.slim;

public class STile {

	public static final byte NONE_FLAG = 0;
	
	public static final byte BOX_FLAG = 1;
	public static final byte PLACE_FLAG = 2;
	public static final byte PLAYER_FLAG = 4;
	public static final byte WALL_FLAG = 8;
	
	public static final byte SOME_ENTITY_FLAG = BOX_FLAG | PLAYER_FLAG;
	
	public static final byte NULLIFY_ENTITY_FLAG = PLACE_FLAG | WALL_FLAG;
	
	private static boolean isThis(byte whatFlag, byte tileSlimFlag) {
		return (whatFlag & tileSlimFlag) != 0;
	}
	
	public static boolean isFree(byte tileSlimFlag) {
		return !isThis(WALL_FLAG, tileSlimFlag) && !isThis(SOME_ENTITY_FLAG, tileSlimFlag);
	}
	
	public static boolean isWall(byte tileSlimFlag) {
		return isThis(WALL_FLAG, tileSlimFlag);
	}
	
	public static boolean isPlayer(byte tileSlimFlag) {
		return isThis(PLAYER_FLAG, tileSlimFlag);
	}
	
	public static boolean isBox(byte tileSlimFlag) {
		return isThis(BOX_FLAG, tileSlimFlag);
	}
	
	public static boolean forBox(byte tileSlimFlag) {
		return isThis(PLACE_FLAG, tileSlimFlag);
	}
	
}
