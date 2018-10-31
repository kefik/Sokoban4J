package cz.jsokoban.simulation.board;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cz.sokoban4j.simulation.board.oop.Board;

public class SokobanActionsTest {
		
	//@Test
	public void testBoardOOP() {
		System.out.println("=== TESTING ACTIONS ON BOARD - OOP ===");
		List<Board> boards = SokobanBoards.loadBoards();
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
		List<Board> boards = SokobanBoards.loadBoards();
		for (Board board : boards) {
			ValidatingAgent agent = new ValidatingAgent(board);
			System.out.println("Testing: " + board.level);
			agent.validateBoardCompact();
		}
		System.out.println("---// TEST OK //---");
	}
	
	//@Test
	public void testBoardCompressed() {
		System.out.println("=== TESTING ACTIONS ON BOARD - COMPRESSED ===");
		List<Board> boards = SokobanBoards.loadBoards();
		for (Board board : boards) {
			ValidatingAgent agent = new ValidatingAgent(board);
			System.out.println("Testing: " + board.level);
			agent.validateBoardCompressed();
		}
		System.out.println("---// TEST OK //---");
	}
	
	//@Test
	public void testBoardSlim() {
		System.out.println("=== TESTING ACTIONS ON BOARD - SLIM ===");
		List<Board> boards = SokobanBoards.loadBoards();
		for (Board board : boards) {
			ValidatingAgent agent = new ValidatingAgent(board);
			System.out.println("Testing: " + board.level);
			agent.validateBoardSlim();
		}
		System.out.println("---// TEST OK //---");
	}
	
	public static void main(String[] args) {
		SokobanActionsTest test = new SokobanActionsTest();
		
		//test.testBoardOOP();
		//test.testBoardCompact();
		//test.testBoardSlim();
		test.testBoardCompressed();
	}

}
