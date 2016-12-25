package cz.sokoban4j.simulation.board.oop;

import cz.sokoban4j.simulation.board.slim.STile;

public enum ESpace {
	
	FREE(true,  1, (byte)0,              " .o123456ABCDEFPOabcdefp$@*+", "GroundGravel_Sand.png"),
	WALL(false, 2, STile.WALL_FLAG, "#",                            "Wall_Brown.png");
	
	private final int flag;
	
	private final byte slimFlag;
	
	private final boolean walkable;

	private final String symbol;

	private String sprite;
	
	private ESpace(boolean walkable, int flag, byte slimFlag, String symbol, String sprite) {
		this.walkable = walkable;
		this.flag = flag;
		this.slimFlag = slimFlag;
		this.symbol = symbol;
		this.sprite = sprite;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public byte getSlimFlag() {
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

	public boolean isWalkable() {
		return walkable;
	}

	public boolean isSpace(int tileFlag) {
		return (tileFlag & flag) != 0;
	}
	
	public static ESpace fromFlag(int tileFlag) {
		for (ESpace space : ESpace.values()) {
			if (space.isSpace(tileFlag)) return space;
		}
		return null;
	}
	
	public static ESpace fromSlimFlag(int tileSlimFlag) {
		for (ESpace space : ESpace.values()) {
			if ((space.getSlimFlag() & tileSlimFlag) > 0) return space;
		}
		return ESpace.FREE;
	}
	
	public static ESpace fromSymbol(String symbol) {
		for (ESpace space : ESpace.values()) {
			if (space.symbol.indexOf(symbol) >= 0) return space;
		}
		return null;
	}
	
}
