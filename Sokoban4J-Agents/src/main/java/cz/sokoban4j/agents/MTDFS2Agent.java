package cz.sokoban4j.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cz.sokoban4j.Sokoban;
import cz.sokoban4j.simulation.SokobanResult;
import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.compact.CAction;
import cz.sokoban4j.simulation.actions.compact.CMove;
import cz.sokoban4j.simulation.actions.compact.CPush;
import cz.sokoban4j.simulation.actions.oop.EActionType;
import cz.sokoban4j.simulation.board.compact.BoardCompact;

/**
 * Multi-threaded version of {@link DFS2Agent}
 * @author Jimmy
 */
public class MTDFS2Agent extends ArtificialAgent {

	protected BoardCompact board;

	protected Object solutionFoundMutex = new Object();
	
	/**
	 * Here we will mark the fact that some {@link DFS2Thread} found a solution.
	 */
	protected boolean solutionFound = false;

	/**
	 * Profiling ... how many nodes we have managed to search.
	 */
	protected AtomicInteger searchedNodes = new AtomicInteger();
	
	@Override
	protected List<EDirection> think(BoardCompact board) {
		// INIT SEARCH
		this.board = board;
		this.solutionFound = false;
		this.searchedNodes.set(0);
		
		// SEARCH CONFIGURATION
		int spareThreads = 8;
		int searchLevel = 37;
		
		// DEBUG
		System.out.println("=================");
		System.out.println("===== BOARD =====");
		this.board.debugPrint();
		System.out.println("=================");
		
		// FIRE THE SEARCH
				
		long searchStartMillis = System.currentTimeMillis();
		
		DFS2Thread rootThread = new DFS2Thread(board, searchLevel, spareThreads-1, EDirection.NONE);
		rootThread.start();
		try {
			rootThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted on rootThread.join()", e);
		}
		
		List<EDirection> result;
		if (rootThread.solutionFound) result = rootThread.result;
		else result = new ArrayList<EDirection>();
				
		long searchTime = System.currentTimeMillis() - searchStartMillis;
		
		System.out.println("SEARCH TOOK:   " + searchTime + " ms");
		System.out.println("NODES VISITED: " + searchedNodes);
		System.out.println("PERFORMANCE:   " + ((double)searchedNodes.get() / (double)searchTime * 1000) + " nodes/sec");
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

	public class DFS2Thread extends Thread {
		
		public List<EDirection> preActions = new ArrayList<EDirection>();
		
		public List<EDirection> result = new ArrayList<EDirection>();
		
		public BoardCompact board;
		
		public boolean solutionFound = false;

		private int level;

		private int spareThreads;

		private EDirection previousMove;
		
		public DFS2Thread(BoardCompact board, int level, int spareThreads, EDirection previousAction) {
			super("DFSThread");
			this.board = board;
			this.level = level;
			this.spareThreads = spareThreads;
			this.previousMove = previousAction;
		}
		
		@Override
		public void run() {
			if (level <= 0) return; // DEPTH-LIMITED
			if (MTDFS2Agent.this.solutionFound) return; // SOLUTION ALREADY FOUND IN DIFFERENT THREAD
			
			if (spareThreads == 0) {
				// no more thread branching ...
				dfs(level, previousMove);
				return;
			}
			
			List<CAction> actions = new ArrayList<CAction>(4);
			
			// cutoff "single action option"
			while (true) {
			
				for (CPush push : CPush.getActions()) {
					if (push.isPossible(board)) {
						actions.add(push);
					}
				}
				for (CMove move : CMove.getActions()) {
					if (move.getDirection() == previousMove.opposite()) {
						// DO NOT CONSIDER THE ACTION THE MOVES BACK
						continue;
					}
					if (move.isPossible(board)) {
						actions.add(move);
					}
				}
			
				if (actions.size() == 1) {
					this.preActions.add(actions.get(0).getDirection());
					actions.get(0).perform(board);
				} else {
					break;
				}
				
			}
			
			// WE HAVE MORE THAN 1 ACTION TO CHOOSE FROM ...
			
			Map<DFS2Thread, EDirection> threads = new HashMap<DFS2Thread, EDirection>();
			
			// LAUNCH SPARE THREADS, leave some action to be investigated by this thread
			while (spareThreads > 0 && actions.size() > 1) {
				EDirection dir = actions.get(0).getDirection();
				BoardCompact threadBoard = board.clone();
				actions.get(0).perform(threadBoard);
				
				int nextSpareThreads;
				// DECIDE ON NUMBER OF THREADS GIVEN TO THE NEXT DFSThread TO USE
				if      (actions.size() == 2) nextSpareThreads = spareThreads-1;
				else if (spareThreads > 2)    nextSpareThreads = 1;
				else                          nextSpareThreads = 0;
				
				DFS2Thread thread = new DFS2Thread(threadBoard, level-1, nextSpareThreads, dir);
				
				thread.start();
				threads.put(thread, dir);
				
				// PREPARE NEXT ITERATION
				spareThreads = spareThreads - 1 - nextSpareThreads;
				actions.remove(0);
			}
			
			// DFS THE REST OF ACTIONS
			dfs(actions, level);
			
			if (solutionFound) {
				// SOLUTION FOUND HERE...
				preActions.addAll(result);
				result = preActions;
				return;
			} else {
				// JOIN ON THREADS
				for (DFS2Thread thread : threads.keySet()) {
					try {
						thread.join();
					} catch (Exception e) {
						return;
					}
				}
				// EXAMINE RESULTS
				for (DFS2Thread thread : threads.keySet()) {
					if (thread.solutionFound) {
						// SOLUTION FOUND BY THIS THREAD
						// REWRITE ACTIONS ...
						EDirection firstAction = threads.get(thread);
						
						result.clear();
						result.addAll(preActions);
						result.add(firstAction);
						result.addAll(thread.result);
						
						solutionFound = true;
					}
				}
			}
		}
		
		private boolean dfs(int level, EDirection previousMove) {
			if (level <= 0) return false; // DEPTH-LIMITED
			if (MTDFS2Agent.this.solutionFound) {
				return false; // SOLUTION ALREADY FOUND IN DIFFERENT THREAD
			}
			
			List<CAction> actions = new ArrayList<CAction>(4);
			
			for (CPush push : CPush.getActions()) {
				if (push.isPossible(board)) {
					actions.add(push);
				}
			}
			for (CMove move : CMove.getActions()) {
				if (move.getDirection() == previousMove.opposite()) {
					// DO NOT CONSIDER THE ACTION THE MOVES BACK
					continue;
				}
				if (move.isPossible(board)) {
					actions.add(move);
				}
			}
						
			return dfs(actions, level);
		}
		
		private boolean dfs(List<CAction> actions, int level) {
			if (level <= 0) return false; // DEPTH-LIMITED
			
			// TODO: try commenting this out to see the full performance boost
			MTDFS2Agent.this.searchedNodes.incrementAndGet();
			
			// TRY ACTIONS
			for (CAction action : actions) {
				if (MTDFS2Agent.this.solutionFound) {
					return false; // SOLUTION ALREADY FOUND IN DIFFERENT THREAD
				}
				
				// PERFORM THE ACTION
				result.add(action.getDirection());
				action.perform(board);
				
				// DEBUG
				//System.out.println("PERFORMED: " + action);
				//board.debugPrint();
				
				// CHECK VICTORY
				if (board.isVictory()) {
					// SOLUTION FOUND!
					solutionFound();					
					return true;
				}
				
				// CONTINUE THE SEARCH
				if (dfs(level-1, (action.getType() == EActionType.MOVE ? action.getDirection() : EDirection.NONE))) {
					// SOLUTION FOUND!
					solutionFound();
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

		private void solutionFound() {
			synchronized(MTDFS2Agent.this.solutionFoundMutex) {
				if (!MTDFS2Agent.this.solutionFound) {
					MTDFS2Agent.this.solutionFound = true;
					solutionFound = true;
				}
			}
		}
		
	}
		
	public static void main(String[] args) {
		SokobanResult result;
		
		// VISUALIZED GAME
		
		// WE CAN SOLVE FOLLOWING 4 LEVELS
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0001.s4jl", new MTDFS2Agent());   //  5 steps required
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0002.1.s4jl", new MTDFS2Agent()); // 13 steps required
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0002.2.s4jl", new MTDFS2Agent()); // 25 steps required
		result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0002.3.s4jl", new MTDFS2Agent()); // 37 steps required
		
		// THIS LEVEL IS BIT TOO MUCH
		//result = Sokoban.playAgentLevel("../Sokoban4J/levels/Easy/level0003.s4jl", new MTDFS2Agent()); // 60 steps required
		
		// HEADLESS == SIMULATED-ONLY GAME
		//result = Sokoban.simAgentLevel("../Sokoban4J/levels/Easy/level0001.s4jl", new MTDFS2Agent());
		
		System.out.println("MTDFS2Agent result: " + result.getResult());
		
		System.exit(0);
	}

	

}
