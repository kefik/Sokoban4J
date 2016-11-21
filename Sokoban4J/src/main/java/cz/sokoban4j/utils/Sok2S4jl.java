package cz.sokoban4j.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforms .sok files from "http://sokobano.de/en/levels.php" into multiple 
 * files in .s4jl format.
 * 
 * Created for manual use - modify {@link Sok2S4jl#main(String[])} and run ;)
 * 
 * @author Jimmy
 */
public class Sok2S4jl {
	
	private static final String NUMBERS = "0123456789";
	
	private static final String MAZE_SYMBOLS = " #.*@$";
	
	private static final String ALLOWED_ID_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
	
	private File sourceFile;
	
	private File targetDir;
	
	public Sok2S4jl(File sourceFile, File targetDir) {
		super();
		this.sourceFile = sourceFile;
		this.targetDir = targetDir;
	}
	
	public void tranformDir() {
		if (sourceFile.isFile()) {
			transform();
			return;
		}
		if (!sourceFile.exists()) throw new RuntimeException("Source directory does not exist: " + sourceFile.getAbsolutePath());
		if (!sourceFile.isDirectory()) throw new RuntimeException("Source directory is not a directory: " + sourceFile.getAbsolutePath());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
			if (!targetDir.exists()) throw new RuntimeException("Target directory does not exist and cannot be created: " + targetDir.getAbsolutePath());
		}
		if (!targetDir.isDirectory()) throw new RuntimeException("Target directory is not a directory: " + targetDir.getAbsolutePath());
		
		File origDir = targetDir;
		
		for (File file : sourceFile.listFiles()) {
			if (file.getAbsolutePath().endsWith(".sok")) {
				sourceFile = file;
				targetDir = new File(origDir, extractFileName(file));
				transform();
			}
		}
	}

	private String extractFileName(File file) {
		String name = file.getName();
		name = name.substring(0, name.lastIndexOf("."));
		String result = "";
		for (int i = 0; i < name.length(); ++i) {
			if (ALLOWED_ID_CHAR.indexOf(name.substring(i, i+1)) >= 0) result += name.substring(i, i+1);
			else result += "_";
		}
		return result;
	}

	public void transform() {
		if (sourceFile.isDirectory()) {
			tranformDir();
			return;
		}
		if (!sourceFile.exists()) throw new RuntimeException("Source file does not exist: " + sourceFile.getAbsolutePath());
		if (!sourceFile.isFile()) throw new RuntimeException("Source file is not a file: " + sourceFile.getAbsolutePath());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
			if (!targetDir.exists()) throw new RuntimeException("Target directory does not exist and cannot be created: " + targetDir.getAbsolutePath());
		}
		if (!targetDir.isDirectory()) throw new RuntimeException("Target directory is not a directory: " + targetDir.getAbsolutePath());
				
		System.out.println("TRANSFORMING: " + sourceFile.getAbsolutePath());
		
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(sourceFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			List<String> lines = new ArrayList<String>();
			
			String line = "";
			
			// FIND START
			while (reader.ready() && (line.length() == 0 || NUMBERS.indexOf(line.substring(0, 1)) < 0)) line = reader.readLine();
			
			int levelNum = 1;
			
			while (reader.ready()) {
				lines.clear();
				line = reader.readLine();
				if (line == null) {
					break;
				}
				while (reader.ready() && (line.length() == 0 || NUMBERS.indexOf(line.substring(0, 1)) < 0)) {					
					lines.add(line);
					line = reader.readLine();
				}
				transform(lines, new File(targetDir, getLevelName(levelNum)));
				++levelNum;
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to transform.", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (Exception e) {					
				}
			}
		}
	}

	private String getLevelName(int num) {
		return "level" + zeros(num, 4) + ".s4jl";
	}

	private String zeros(int num, int zeros) {
		String result = "" + num;
		while (result.length() < zeros) result = "0" + result;
		return result;
	}

	private void transform(List<String> lines, File targetFile) {		
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
			return;
		}
		
		System.out.println("  +-- Writing: " + targetFile.getAbsolutePath());
		
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(targetFile);
			PrintWriter writer = new PrintWriter(fileWriter);
			
			// COMMENTS
			for (int i = mazeEnd; i < lines.size(); ++i) {
				if (lines.get(i).trim().length() > 0) {
					writer.print(";");
					writer.println(lines.get(i).trim());
				}
			}
			
			// DIMENSIONS
			writer.println(width + "," + height);
			
			// MAZE
			for (int i = mazeStart; i < mazeEnd; ++i) {
				writer.println(adjustMazeLine(lines.get(i), width));
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to save target file: " + targetFile.getAbsolutePath(), e);
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (Exception e) {					
				}
			}
		}
		
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

	public static void main(String[] args) {
		String sourceDirPath = "levels/sokobano.de";
		
		String targetDirPath = "levels";
		
		Sok2S4jl transformer = new Sok2S4jl(new File(sourceDirPath), new File(targetDirPath));
		
		transformer.transform();
		
		System.out.println("---/// DONE ///---");
	}


}
