package cz.sokoban4j.utils;

import java.util.List;

import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.ESpace;

public class TextLevelS4JL {

	private String name;
	
	private int height;
	
	private int width;
	
	private List<String> maze;
	
	private List<String> comments;
	
	public TextLevelS4JL(String name, int width, int height, List<String> mazeLinesS4JL, List<String> comments) {
		super();
		this.name = name;
		this.height = height;
		this.width = width;
		this.maze = mazeLinesS4JL;
		this.comments = comments;
	}
	
	public String getName() {
		return name == null ? "N/A" : name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public List<String> getMaze() {
		return maze;
	}

	public void setMaze(List<String> maze) {
		this.maze = maze;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public void validate() {
		if (getWidth() < 4 || getWidth() < 4) throw new RuntimeException("Maze of invalid dimensions (w x h): " + getWidth() + " x " + getHeight());
		if (getMaze() == null) throw new RuntimeException("getMaze() is null");
		if (getMaze().size() != getHeight()) throw new RuntimeException("getMaze() has invalid number of lines: " + getMaze().size() + " != " + getHeight() + " == getHeight()");
		int i = 0;
		for (String line : getMaze()) {
			if (line.length() != getWidth()) throw new RuntimeException("Maze line " + i + " has invalid length: " + line.length() + " != " + getWidth() + " = getWidth()");
			if (ESpace.WALL.getSymbols().indexOf(line.substring(0, 1)) < 0) throw new RuntimeException("Maze line " + i + " does not start with a wall (wall symbols: '" + ESpace.WALL.getSymbols() + "'): " + line);
			if (ESpace.WALL.getSymbols().indexOf(line.substring(line.length()-1, line.length())) < 0) throw new RuntimeException("Maze line " + i + " does not end with a wall (wall symbols: '" + ESpace.WALL.getSymbols() + "'): " + line);
			++i;
		}
	}
	
}
