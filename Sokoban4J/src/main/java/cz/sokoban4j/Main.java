package cz.sokoban4j;

import cz.sokoban4j.SokobanConfig.ELevelFormat;

public class Main {
	
	public static void main(String[] args) {
		// CHECK {@link Sokoban} CLASS and its static methods for quick Sokoban startup
		//Sokoban.playHumanDir("levels/Easy", ELevelFormat.S4JL);
		Sokoban.playHuman("levels/sokobano.de/Blazz.sok", ELevelFormat.SOK);		
		//Sokoban.playHuman("levels/sokobano.de/Andre_Bernier.sok", ELevelFormat.SOK);
	}
	
}
