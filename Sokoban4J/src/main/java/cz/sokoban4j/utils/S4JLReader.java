package cz.sokoban4j.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class S4JLReader {
	
	private String name;

	private BufferedReader reader;
	
	/**
	 * 1-based.
	 */
	private int nextLevelNumber = 1;
	
	private List<String> lines = new ArrayList<String>();
	
	public S4JLReader(File s4jlFile) {
		if (s4jlFile == null) throw new RuntimeException("s4jlFile is null");
		if (!s4jlFile.exists()) throw new RuntimeException("Source file does not exist: " + s4jlFile.getAbsolutePath());
		if (!s4jlFile.isFile()) throw new RuntimeException("Source file is not a file: " + s4jlFile.getAbsolutePath());
		name = s4jlFile.getName();
		try {
			initReader(new FileReader(s4jlFile));			
		} catch (FileNotFoundException e) {
			close();
			throw new RuntimeException("Failed to open SOK file: " + s4jlFile.getAbsolutePath(), e);
		}
	}
	
	public S4JLReader(String name, Reader reader) {
		this.name = name;
		initReader(reader);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns number of the next level; 1-based.
	 * @return
	 */
	public int getNextLevelNumber() {
		return nextLevelNumber;
	}

	private void initReader(Reader reader) {
		try {
			this.reader = new BufferedReader(reader);
			findStart();
		} catch (Exception e) {
			close();
			throw new RuntimeException("Failed to initialize reader.", e);
		}
	}
	
	private void findStart() throws IOException {
		// FIND START
		// nothing to do...
	}
	
	/**
	 * Parses next level within the reader.
	 * Once there are no levels, it returns NULL.
	 * @return
	 */
	public TextLevelS4JL readNext() {
		try {
			
			int width = 0;
			int height = 0;
			List<String> maze = new ArrayList<String>();
			List<String> comments = new ArrayList<String>();
			
			String line = null;

			lines.clear();
			
			// SKIP WHITE SPACES
			line = reader.readLine();
			while (line.trim().length() == 0) line = reader.readLine();
			
			// READ COMMENTS
			while (line.startsWith(";")) comments.add(line.substring(1));
			
			// READ DIMENSIONS
			String[] parts = line.split(",");
			
			width = Integer.parseInt(parts[0]);
			height = Integer.parseInt(parts[1]);
			
			// READ MAZE
			for (int y = 0; y < height; ++y) {
				line = reader.readLine();
				maze.add(line);
			}
						
			TextLevelS4JL result = new TextLevelS4JL(name + " / " + nextLevelNumber, width, height, maze, comments);
				
			++nextLevelNumber;
				
			return result;			
		} catch (Exception e) {
			close();
			return null;
		}
	}
	
	public void close() {
		try {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		} catch (Exception e) {			
		}
	}
	
	/**
	 * Returns how many levels are within 'file'
	 * @param s4jlFile
	 */
	public static int getLevelNumber(File s4jlFile) {
		S4JLReader reader = new S4JLReader(s4jlFile);
		int count = 0;
		while (reader.readNext() != null) ++count;
		reader.close();
		return count;
	}
	
}
