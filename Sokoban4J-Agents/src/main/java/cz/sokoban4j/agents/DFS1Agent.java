package cz.sokoban4j.agents;

import java.util.ArrayList;
import java.util.List;

import cz.sokoban4j.Sokoban;
import cz.sokoban4j.SokobanConfig.ELevelFormat;
import cz.sokoban4j.simulation.SokobanResult;
import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.compact.CAction;
import cz.sokoban4j.simulation.actions.compact.CMove;
import cz.sokoban4j.simulation.actions.compact.CPush;
import cz.sokoban4j.simulation.board.compact.BoardCompact;

/**
 * The simplest Tree-DFS implementation
 * @author Jimmy
 */
public class DFS1Agent extends ArtificialAgent {

	protected List<EDirection> result;
	
	protected BoardCompact board;
	
	protected boolean solutionFound;
	
	protected int searchedNodes;
	
	protected long searchStartMillis;
	
	@Override
	protected List<EDirection> think(BoardCompact board) {
		// INIT SEARCH
		this.board = board;
		this.result = new ArrayList<EDirection>();
		this.solutionFound = false;
		this.searchedNodes = 0;		
		
		// DEBUG
		System.out.println("=================");
		System.out.println("===== BOARD =====");
		this.board.debugPrint();
		System.out.println("=================");
		
		// FIRE THE SEARCH
		
		searchStartMillis = System.currentTimeMillis();
		
		dfs(13); // the number marks how deep we will search (the longest plan we will consider)

		long searchTime = System.currentTimeMillis() - searchStartMillis;
		
		System.out.println("SEARCH TOOK:   " + searchTime + " ms");
		System.out.println("NODES VISITED: " + searchedNodes);
		System.out.println("PERFORMANCE:   " + ((double)searchedNodes / (double)searchTime * 1000) + " nodes/sec");
		System.out.println("SOLUTION:      " + (result.size() == 0 ? "NOT FOUND" : "FOUND in " + result.size() + " steps"));
		if (result.size() > 0) {
			System.out.print("STEPS:         ");
			for (EDirection winDirection : result) {
				System.out.print(winDirection + " -> ");
			}
			System.out.println("BOARD SOLVED!");
		}
		System.out.println("=================");
		
		if (result.size() == 0) {
			throw new RuntimeException("FAILED TO SOLVE THE BOARD...");
		}
				
		return result;
	}

	private boolean dfs(int level) {
		if (level <= 0) return false; // DEPTH-LIMITED
		
		++searchedNodes;
		
		// COLLECT POSSIBLE ACTIONS
		
		List<CAction> actions = new ArrayList<CAction>(4);
		
		// TODO: try to swap CMoves and CPushes => action ordering matters!
		for (CMove move : CMove.getActions()) {
			if (move.isPossible(board)) {
				actions.add(move);
			}
		}
		for (CPush push : CPush.getActions()) {
			if (push.isPossible(board)) {
				actions.add(push);
			}
		}
		
		// TRY ACTIONS
		for (CAction action : actions) {
			// PERFORM THE ACTION
			result.add(action.getDirection());
			action.perform(board);
			
			// DEBUG
			//System.out.println("PERFORMED: " + action);
			//board.debugPrint();
			
			// CHECK VICTORY
			if (board.isVictory()) {
				// SOLUTION FOUND!
				return true;
			}
			
			// CONTINUE THE SEARCH
			if (dfs(level-1)) {
				// SOLUTION FOUND!
				return true;
			}
			
			// REVESE ACTION
			result.remove(result.size()-1);
			action.reverse(board);
			
			// DEBUG
			//System.out.println("REVERSED: " + action + " -> " + action.getDirection().opposite());
			//board.debugPrint();
		}
		
		return false;
	}
		
	public static void main(String[] args) {
		SokobanResult result;
		
		// VISUALIZED GAME
		
		// WE CAN SOLVE FOLLOWING TWO LEVELS
		result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0001.s4jl", new DFS1Agent());   //  5 steps required
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0002.1.s4jl", new DFS1Agent()); // 13 steps required
		
		// THESE ARE OO MUCH FOR THIS IMPLEMENTATION
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0002.2.s4jl", new DFS1Agent()); // 25 steps required
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0002.3.s4jl", new DFS1Agent()); // 37 steps required
		
		// HEADLESS == SIMULATED-ONLY GAME
		//result = Sokoban.simAgentLevel("../Sokoban4J/levels/Easy/level0001.s4jl", new DFS1Agent());
		
		System.out.println("DFS1Agent result: " + result.getResult());
		
		System.exit(0);
	}

	

}
