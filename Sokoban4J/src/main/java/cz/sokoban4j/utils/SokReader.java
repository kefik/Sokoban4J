package cz.sokoban4j.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SokReader {

	private static final String NUMBERS = "0123456789";
	
	private static final String MAZE_SYMBOLS = " #.*@$+";
		
	private BufferedReader reader;
	
	private String name;
	
	/**
	 * 1-based.
	 */
	private int nextLevelNumber = 1;
	
	private List<String> lines = new ArrayList<String>();
	
	public SokReader(File sokFile) {
		if (sokFile == null) throw new RuntimeException("sokFile is null");
		if (!sokFile.exists()) throw new RuntimeException("Source file does not exist: " + sokFile.getAbsolutePath());
		if (!sokFile.isFile()) throw new RuntimeException("Source file is not a file: " + sokFile.getAbsolutePath());
		this.name = sokFile.getName();
		try {
			initReader(new FileReader(sokFile));			
		} catch (FileNotFoundException e) {
			close();
			throw new RuntimeException("Failed to open SOK file: " + sokFile.getAbsolutePath(), e);
		}
	}
	
	public SokReader(String name, Reader reader) {
		this.name = name;
		initReader(reader);
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
		if (reader == null || !reader.ready()) throw new RuntimeException("Failed to find first level within the SOK.");
		int lineNum = 1;
		String line = reader.readLine();
		while (reader.ready()) {
			if (line.length() != 0) {
				try {
					Integer.parseInt(line);
					break;
				} catch (Exception e) {					
				}
			}
			line = reader.readLine();
			++lineNum;
		}
		if (line == null || line.length() <= 0 || NUMBERS.indexOf(line.substring(0, 1)) < 0) {
			throw new RuntimeException("Failed to find first level within the SOK.");
		}
	}
	
	/**
	 * Parses next level within the reader.
	 * Once there are no levels, it returns NULL.
	 * @return
	 */
	public TextLevelS4JL readNext() {
		try {
			String line = null;

			lines.clear();
			line = reader.readLine();
			if (line == null) {
				return null;
			}
			while (reader.ready() && (line.length() == 0 || NUMBERS.indexOf(line.substring(0, 1)) < 0)) {					
				lines.add(line);
				line = reader.readLine();
			}
				
			TextLevelS4JL result = transform(lines);
				
			++nextLevelNumber;
				
			return result;			
		} catch (Exception e) {
			close();
			return null;
		}
	}
	
	private TextLevelS4JL transform(List<String> lines) {		
		// FIND MAZE
		int mazeStart = 0;
		int mazeEnd = 0;
		
		int maxLength = 0;
		
		while (mazeEnd < lines.size() && isMazeLine(lines.get(mazeEnd))) {			
			String line = lines.get(mazeEnd);
			int length = line.length()-1;
			while (length > 0 && line.substring(length, length+1).equals(" ")) --length;
			if (length+1 > maxLength) maxLength = length+1;
			++mazeEnd;
		}
		
		int width = maxLength;
		int height = mazeEnd - mazeStart;
		
		if (width <= 4 || height < 4) {
			throw new RuntimeException("Level of invalid dimensions (w x h): " + width + " x " + height);
		}
		
		List<String> maze = new ArrayList<String>();
		List<String> comments = new ArrayList<String>();
			
		// COMMENTS
		for (int i = mazeEnd; i < lines.size(); ++i) {
			if (lines.get(i).trim().length() > 0) {
				comments.add(lines.get(i).trim());
			}
		}
			
		// MAZE
		for (int i = mazeStart; i < mazeEnd; ++i) {
			maze.add(adjustMazeLine(lines.get(i), width));
		}
		
		return new TextLevelS4JL(name + " / " + nextLevelNumber, width, height, maze, comments);
	}
	
	private String adjustMazeLine(String line, int width) {
		String result = "";
		
		int i = 0;
		
		while (i < line.length() && line.substring(i, i+1).equals(" ")) {
			result += "#";
			++i;
		}
		result += line.substring(i);
		i = result.length()-1;
		while (result.substring(i, i+1).equals(" ")) {
			result = result.substring(0, i) + "#" + result.substring(i+1);
			++i;
		}
		while (result.length() < width) result += "#";
		result = result.substring(0, width);

		return result;			
	}

	private boolean isMazeLine(String line) {
		for (int i = 0; i < line.length(); ++i) {
			if (MAZE_SYMBOLS.indexOf(line.substring(i, i+1)) < 0) return false;
		}
		return true;
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
	 * @param sokFile
	 */
	public static int getLevelNumber(File sokFile) {
		SokReader reader = new SokReader(sokFile);
		int count = 0;
		while (reader.readNext() != null) ++count;
		reader.close();
		return count;
	}
	
}
