package cz.sokoban4j.tournament.run;

import java.io.File;

import cz.sokoban4j.SokobanConfig.ELevelFormat;
import cz.sokoban4j.utils.S4JLReader;
import cz.sokoban4j.utils.SokReader;

public class SokobanLevel {
	
	public File file;
	
	public int levelNumber;

	public SokobanLevel(File file, int levelNumber) {
		super();
		this.file = file;
		this.levelNumber = levelNumber;
	}
	
	public static int getLevelCount(File file) {
		if (file == null) {
			throw new RuntimeException("'file' is null");
		}
		if (!file.exists()) {
			throw new RuntimeException("'file' does not exist at: " + file.getAbsolutePath());
		}
		if (!file.isFile()) {
			throw new RuntimeException("'file' is not a file: " + file.getAbsolutePath());
		}
		ELevelFormat format = ELevelFormat.getExpectedLevelFormat(file);
		switch (format) {
		case S4JL: return S4JLReader.getLevelNumber(file);
		case SOK: return SokReader.getLevelNumber(file);
		default:
			throw new RuntimeException("Unexpected file extension: " + file.getAbsolutePath());
		}
	}
	
	public void validate() {
		if (ELevelFormat.getExpectedLevelFormat(file) == null) throw new RuntimeException("Bad file format in " + this);
		if (levelNumber < 0) throw new RuntimeException("Bad level number in " + this);
	}
	
	@Override
	public String toString() {
		return "SokobanLevel[" + levelNumber + ";" + (file == null ? "null" : file.getAbsolutePath()) + "]";
	}
	
}
