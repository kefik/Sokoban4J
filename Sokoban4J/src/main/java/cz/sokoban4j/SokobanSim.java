package cz.sokoban4j;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.oop.Board;

public class SokobanSim extends Thread {

	private Board board;
	private IAgent agent;
	
	private IAction agentAction;
	
	public int steps = 0;

	public boolean shouldRun = true;
	
	public SokobanSim(Board board, IAgent agent) {
		this.board = board;
		this.agent = agent;		
	}
	
	@Override
	public void run() {		
		agent.newLevel();
		
		while (shouldRun) {
			// EXTRACT COMPACT VERSION OF THE BOARD FOR AI
			BoardCompact compactBoard = board.makeBoardCompact();
			
			// PRESENT BOARD TO THE AGENT
			agent.observe(compactBoard);
			
			// GET AGENT ACTION
			EDirection whereToMove = agent.act();
			
			if (whereToMove == null || whereToMove == EDirection.NONE) continue;
			
			agentAction = MoveOrPush.getMoveOrPush(whereToMove);

			// AGENT ACTION VALID?
			if (agentAction != null && agentAction.isPossible(board)) {
				// PERFORM THE ACTION
				agentAction.perform(board);
				++steps;
				if (board.isVictory()) {
					agent.victory();
					return;
				}
			}
			
			// NULLIFY THE ACTION
			agentAction = null;
		}
		
		
		
	}
	
}
