package cz.sokoban4j.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Transforms .sok files from "http://sokobano.de/en/levels.php" into multiple 
 * files in .s4jl format.
 * 
 * Manual use - modify {@link SOK2S4JL#main(String[])} and run from IDE ;)
 * 
 * @author Jimmy
 */
public class SOK2S4JL {
	
	private static final String ALLOWED_ID_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
	
	private File sourceFile;
	
	private File targetDir;
	
	/**
	 * @param source may be .sok file or directory to process (no directory recursion)
	 * @param targetDir where to save respective S4JL files
	 */
	public SOK2S4JL(File source, File targetDir) {
		super();
		this.sourceFile = source;
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
		
		SokReader reader = new SokReader(sourceFile);
		
		TextLevelS4JL level = reader.readNext();
		while (level != null) {
			transform(level, new File(targetDir, getLevelName(reader.getNextLevelNumber()-1)));
			level = reader.readNext();
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

	private void transform(TextLevelS4JL level, File targetFile) {		
		System.out.println("  +-- Writing: " + targetFile.getAbsolutePath());
		
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(targetFile);
			PrintWriter writer = new PrintWriter(fileWriter);
			
			// COMMENTS
			for (String comment : level.getComments()) {
				writer.print(";");
				writer.println(comment.trim());				
			}
			
			// DIMENSIONS
			writer.println(level.getWidth() + "," + level.getHeight());
			
			// MAZE
			for (String maze : level.getMaze()) {
				writer.println(maze);
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

	public static void main(String[] args) {
		String sourceDirPath = "levels/sokobano.de";
		
		String targetDirPath = "levels";
		
		SOK2S4JL transformer = new SOK2S4JL(new File(sourceDirPath), new File(targetDirPath));
		
		transformer.transform();
		
		System.out.println("---/// DONE ///---");
	}

}
