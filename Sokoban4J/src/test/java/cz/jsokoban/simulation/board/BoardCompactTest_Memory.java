package cz.jsokoban.simulation.board;

import java.util.ArrayList;
import java.util.List;

import cz.sokoban4j.simulation.board.compact.BoardCompact;

public class BoardCompactTest_Memory {

	private static void printMemory() {
		int mb = 1024*1024;
		
		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		
		System.out.println("############################################");
		System.out.println("##### Heap utilization statistics [MB] #####");
		System.out.println("############################################");
		
		//Print used memory
		System.out.println("Used Memory:" 
			+ (runtime.totalMemory() - runtime.freeMemory()) / mb + " MB");

		//Print free memory
		System.out.println("Free Memory:" 
			+ runtime.freeMemory() / mb);
		
		//Print total available memory
		System.out.println("Total Memory:" + runtime.totalMemory() / mb + " MB");

		//Print Maximum available memory
		System.out.println("Max Memory:" + runtime.maxMemory() / mb + " MB");
		
		System.out.println("############################################");
	}
	
	public static void main(String[] args) {
		long count = 0;
		
		List<BoardCompact> boards = new ArrayList<BoardCompact>();
		
		BoardCompact board;
		
		while (true) {
			board = new BoardCompact(16,16);
			boards.add(board);
			++count;
			if (count % 100 == 0) {
				printMemory();
				System.out.println("COUNT = " + count);
				System.out.println("###########################################");
			}
		}
		
		// COUNT HITS 1120000 AT MY MACHINE @Jimmy ~ > 50% less than BoardSlimTest
		//                                         ~ > 65% less then BoardCompressedTest
	}
	
}
