package cz.sokoban4j.simulation.board.oop;

import cz.sokoban4j.simulation.board.slim.STile;

public enum EPlace {

	NONE(-1,     1024, (byte)0,             " #abcdefp$@", null),
	BOX_ANY(0,   2048, STile.PLACE_FLAG, "oOABCDEFP", "EndPoint_Brown.png"),
	BOX_1(1,     4096, STile.PLACE_FLAG, "1.*+", "EndPoint_Yellow.png"),
	BOX_2(2,     8192, STile.PLACE_FLAG, "2", "EndPoint_Blue.png"),
	BOX_3(3,    16384, STile.PLACE_FLAG, "3", "EndPoint_Red.png"),
	BOX_4(4,    32768, STile.PLACE_FLAG, "4", "EndPoint_Purple.png"),
	BOX_5(5,    65536, STile.PLACE_FLAG, "5", "EndPoint_Gray.png"),
	BOX_6(6,   131072, STile.PLACE_FLAG, "6", "EndPoint_Black.png");
	
	public static final int SOME_BOX_PLACE_FLAG = 2048 | 4096 | 8192 | 16384 | 32768 | 65536 | 131072;
	
	private final int boxNum;
	
	private final int flag;
	
	private final int slimFlag;

	private final String symbol;

	private final String sprite;
	
	private EPlace(int boxNum, int flag, int slimFlag, String symbol, String sprite) {
		this.boxNum = boxNum;
		this.flag = flag;
		this.slimFlag = slimFlag;
		this.symbol = symbol;
		this.sprite = sprite;
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

	public int getBoxNum() {
		return boxNum;
	}

	public boolean forAnyBox() {
		return boxNum == 0;
	}
	
	public boolean forSomeBox() {
		return boxNum >= 0;
	}
	
	public boolean forBox(EEntity entity) {
		return forBox(entity.getBoxNum());		
	}
	
	public boolean forBox(int boxNum) {
		if (this.boxNum < 0) return false;
		if (boxNum <= 0) return false;
		return this.boxNum == 0 || this.boxNum == boxNum;
	}
	
	public boolean isPlace(int tileFlag) {
		return (tileFlag & flag) != 0;
	}
	
	public static EPlace fromFlag(int tileFlag) {
		for (EPlace place : EPlace.values()) {
			if (place.isPlace(tileFlag)) return place;
		}
		return null;
	}
	
	public static EPlace fromSlimFlag(int tileSlimFlag) {
		for (EPlace place : EPlace.values()) {
			if ((place.getSlimFlag() & tileSlimFlag) > 0) return place;
		}
		return null;
	}
	
	public static EPlace fromSymbol(String symbol) {
		for (EPlace place : EPlace.values()) {
			if (place.symbol.indexOf(symbol) >= 0) return place;
		}
		return null;
	}
	
}
