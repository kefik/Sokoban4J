package cz.sokoban4j.simulation.board.oop;

public enum EEntity {
	
	NONE(-1,   false,  false,   4, " ", null, null),	
	BOX_1(1,    true,  false,   8, "aA$", "CrateDark_Yellow.png", "Crate_Yellow.png"),
	BOX_2(2,    true,  false,  16, "bB", "CrateDark_Blue.png", "Crate_Blue.png"),
	BOX_3(3,    true,  false,  32, "cC", "CrateDark_Red.png", "Crate_Red.png"),
	BOX_4(4,    true,  false,  64, "dD", "CrateDark_Purple.png", "Crate_Purple.png"),
	BOX_5(5,    true,  false, 128, "eE", "CrateDark_Gray.png", "Crate_Gray.png"),
	BOX_6(6,    true,  false, 256, "fF", "CrateDark_Black.png", "Crate_Black.png"),
	PLAYER(-1, false,   true, 512, "pP@", null, null);
	
	public static final int FIRST_BOX_FLAG = 8;
	public static final int SOME_ENTITY_FLAG = 8 | 16 | 32 | 64 | 128 | 256 | 512;
	public static final int NULLIFY_ENTITY_FLAG = 0xFFFFFFFF ^ SOME_ENTITY_FLAG;
	
	private final int flag;
	
	private final int boxNum;
	
	private final boolean box;
	
	private final boolean player;

	private final String symbol;

	private final String sprite;

	private final String spriteBoxAtPosition;
	
	private EEntity(int boxNum, boolean box, boolean player, int flag, String symbol, String sprite, String spriteBoxAtPosition) {
		this.boxNum = boxNum;
		this.box = box;
		this.player = player;
		this.flag = flag;
		this.symbol = symbol;
		this.sprite = sprite;
		this.spriteBoxAtPosition = spriteBoxAtPosition;
	}
	
	public int getFlag() {
		return flag;
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
	
	public static EEntity fromSymbol(String symbol) {
		for (EEntity entity : EEntity.values()) {
			if (entity.symbol.indexOf(symbol) >= 0) return entity;
		}
		return null;
	}
	
}
