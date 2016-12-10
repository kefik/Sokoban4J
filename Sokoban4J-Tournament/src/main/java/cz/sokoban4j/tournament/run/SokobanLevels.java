package cz.sokoban4j.tournament.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SokobanLevels {

	public List<SokobanLevel> levels = new ArrayList<SokobanLevel>();
	
	public void validate() {
		for (SokobanLevel level : levels) level.validate();
	}
	
	public static SokobanLevels fromString(String string) {
		SokobanLevels results = new SokobanLevels();
		
		String[] parts = string.split(";");
		
		if (parts.length % 2 != 0) {
			throw new RuntimeException("Level list string has invalid format; there is odd number of parts (should be even): " + parts.length);
		}
		
		int index = 0;
		
		while (index < parts.length) {
			
			String fileString = parts[index];
			
			File file = new File(fileString);
			
			String level = parts[index + 1];
			
			if (level.toLowerCase().equals("all")) {
				int levelCount = SokobanLevel.getLevelCount(new File(fileString));
				for (int i = 0; i < levelCount; ++i) {
					results.levels.add(new SokobanLevel(file, i));
				}
			} else {
				int levelNumber = Integer.parseInt(level);
				results.levels.add(new SokobanLevel(file, levelNumber));
			}
			
			index += 2;
		}
		
		return results;
	}
	
}
