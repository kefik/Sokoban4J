package cz.sokoban4j.simulation.board.oop;

import cz.sokoban4j.simulation.board.slim.STile;

public enum EEntity {
	
	NONE(-1,   false,  false,   4, (byte)0,                " ", null, null),	
	BOX_1(1,    true,  false,   8, STile.BOX_FLAG,    "aA$*", "CrateDark_Yellow.png", "Crate_Yellow.png"),
	BOX_2(2,    true,  false,  16, STile.BOX_FLAG,    "bB", "CrateDark_Blue.png", "Crate_Blue.png"),
	BOX_3(3,    true,  false,  32, STile.BOX_FLAG,    "cC", "CrateDark_Red.png", "Crate_Red.png"),
	BOX_4(4,    true,  false,  64, STile.BOX_FLAG,    "dD", "CrateDark_Purple.png", "Crate_Purple.png"),
	BOX_5(5,    true,  false, 128, STile.BOX_FLAG,    "eE", "CrateDark_Gray.png", "Crate_Gray.png"),
	BOX_6(6,    true,  false, 256, STile.BOX_FLAG,    "fF", "CrateDark_Black.png", "Crate_Black.png"),
	PLAYER(-1, false,   true, 512, STile.PLAYER_FLAG, "pP@+", null, null);
	
	/**
	 * Any box, i.e., it can be any of {@link #BOX_1}, {@link #BOX_2}, {@link #BOX_3}, {@link #BOX_4}, {@link #BOX_5}, {@link #BOX_6}.
	 */
	public static final int SOME_BOX_FLAG = 8 | 16 | 32 | 64 | 128 | 256;
	
	/**
	 * A box or a {@link #PLAYER};
	 */
	public static final int SOME_ENTITY_FLAG = SOME_BOX_FLAG | 512;	
	
	/**
	 * Technical; any {@link EEntity} {@link #flag}.
	 */
	public static final int ALL_ENTITY_FLAG = 4 | 8 | 16 | 32 | 64 | 128 | 256 | 512;
	
	/**
	 * Flag containing zeroes at all {@link EEntity} {@link #flag} positions.
	 */
	public static final int NULLIFY_ENTITY_FLAG = 0xFFFFFFFF ^ ALL_ENTITY_FLAG;
	
	private final int flag;
	
	private final byte slimFlag; 
	
	private final int boxNum;
	
	private final boolean box;
	
	private final boolean player;

	private final String symbol;

	private final String sprite;

	private final String spriteBoxAtPosition;
	
	private EEntity(int boxNum, boolean box, boolean player, int flag, byte slimFlag, String symbol, String sprite, String spriteBoxAtPosition) {
		this.boxNum = boxNum;
		this.box = box;
		this.player = player;
		this.flag = flag;
		this.slimFlag = slimFlag;
		this.symbol = symbol;
		this.sprite = sprite;
		this.spriteBoxAtPosition = spriteBoxAtPosition;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public int getSlimFlag() {
		return slimFlag;
	}

	public String getSymbols() {
		return symbol;
	}
	
	public String getSymbol() {
		return symbol.substring(0,1);
	}

	public String getSprite() {
		return sprite;
	}
	
	public String getSpriteBoxAtPosition() {
		return spriteBoxAtPosition;
	}

	public boolean isPlayer() {
		return player;
	}
	
	public boolean isSomeBox() {
		return box;
	}
	
	public boolean isBox(int boxNum) {
		return this.boxNum == boxNum;
	}
	
	public int getBoxNum() {
		return boxNum;
	}
	
	public boolean forPlace(EPlace place) {
		return place.forBox(this);		
	}
		
	public boolean isEntity(int tileFlag) {
		return (tileFlag & flag) != 0;
	}
	
	public static EEntity fromFlag(int tileFlag) {
		for (EEntity entity : EEntity.values()) {
			if (entity.isEntity(tileFlag)) return entity;
		}
		return null;
	}
	
	public static EEntity fromSlimFlag(int tileSlimFlag) {
		for (EEntity entity : EEntity.values()) {
			if ((entity.getSlimFlag() & tileSlimFlag) > 0) return entity;
		}
		return null;
	}
	
	public static EEntity fromSymbol(String symbol) {
		for (EEntity entity : EEntity.values()) {
			if (entity.symbol.indexOf(symbol) >= 0) return entity;
		}
		return null;
	}
	
}
