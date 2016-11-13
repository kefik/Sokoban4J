package cz.sokoban4j.simulation.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EDirection {
	
	NONE(-1, 0, 0), UP(0, 0, -1), RIGHT(1, 1, 0), DOWN(2, 0, 1), LEFT(3, -1, 0);
	
	private static EDirection[] arrows = new EDirection[]{UP, RIGHT, DOWN, LEFT};
	
	private static List<EDirection> arrowsList = null;
	
	private static Map<Integer, EDirection> indices = null;
	
	public final int index;
	public final int dX;
	public final int dY;

	private EDirection(int directionIndex, int dX, int dY) {
		this.index = directionIndex;
		this.dX = dX;
		this.dY = dY;
	}
	
	public EDirection opposite() {
		switch(this) {
		case NONE: return NONE;
		case DOWN: return UP;
		case LEFT: return RIGHT;
		case RIGHT: return LEFT;
		case UP: return DOWN;
		}
		return null;
	}
	
	public EDirection cw() {
		switch(this) {
		case NONE: return NONE;
		case DOWN: return LEFT;
		case LEFT: return UP;
		case RIGHT: return DOWN;
		case UP: return RIGHT;
		}
		return null;
	}
	
	public EDirection ccw() {
		switch(this) {
		case NONE: return NONE;
		case DOWN: return RIGHT;
		case LEFT: return DOWN;
		case RIGHT: return UP;
		case UP: return LEFT;
		}
		return null;
	}
	
	public static EDirection forIndex(int directionIndex) {
		if (indices == null) {
			indices = new HashMap<Integer, EDirection>();
			for (EDirection dir : values()) {
				indices.put(dir.index, dir);
			}
		}
		EDirection dir = indices.get(directionIndex);
		if (dir == null) return NONE;
		return dir;
	}
	
	/**
	 * UP, RIGHT, DOWN, LEFT
	 * @return
	 */
	public static EDirection[] arrows() {
		return arrows;
	}
	
	/**
	 * UP, RIGHT, DOWN, LEFT
	 * @return
	 */
	public static List<EDirection> arrowsList() {
		if (arrowsList == null) {
			arrowsList = new ArrayList<EDirection>(4);
			for (EDirection d : arrows) {
				arrowsList.add(d);
			}
		}
		return arrowsList;
	}
	

}
