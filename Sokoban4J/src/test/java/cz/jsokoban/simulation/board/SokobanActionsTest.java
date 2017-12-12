package cz.jsokoban.simulation.board;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cz.sokoban4j.simulation.board.oop.Board;

public class SokobanActionsTest {
	
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
	
	//@Test
	public void testBoardOOP() {
		System.out.println("=== TESTING ACTIONS ON BOARD - OOP ===");
		List<Board> boards = loadBoards();
		for (Board board : boards) {
			ValidatingAgent agent = new ValidatingAgent(board);
			System.out.println("Testing: " + board.level);
			agent.validateBoard();
		}
		System.out.println("---// TEST OK //---");
	}
	
	//@Test
	public void testBoardCompact() {
		System.out.println("=== TESTING ACTIONS ON BOARD - COMPACT ===");
		List<Board> boards = loadBoards();
		for (Board board : boards) {
			ValidatingAgent agent = new ValidatingAgent(board);
			System.out.println("Testing: " + board.level);
			agent.validateBoardCompact();
		}
		System.out.println("---// TEST OK //---");
	}
	
	public static void main(String[] args) {
		SokobanActionsTest test = new SokobanActionsTest();
		
		//test.testBoardOOP();
		test.testBoardCompact();
	}

}
