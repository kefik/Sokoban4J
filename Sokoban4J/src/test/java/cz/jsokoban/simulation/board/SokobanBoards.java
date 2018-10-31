package cz.jsokoban.simulation.board;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.sokoban4j.simulation.board.oop.Board;

public class SokobanBoards {

	public static List<Board> loadBoards() {
		List<Board> boards = new ArrayList<Board>();
		
		File sokFile = new File("Levels/Sokobano.de/A.K.K._Informatika.sok");
		
		int levelNumber = 0;
		while (true) {			
			Board board;
			
			try {
				board = Board.fromFileSok(sokFile, levelNumber);
				if (board != null) {
					boards.add(board);
				} else {
					break;
				}
			} catch (Exception e) {
				break;
			}
			++levelNumber;
		}
		
		return boards;
	}
	
	
}
