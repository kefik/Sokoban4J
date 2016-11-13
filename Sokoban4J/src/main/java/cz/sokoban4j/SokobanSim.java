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
	
	private boolean observe = true;
	
	private IAction agentAction;
	
	public int steps = 0;

	public boolean shouldRun = true;
	
	public SokobanSim(Board board, IAgent agent) {
		super("SokobanSim");
		
		this.board = board;
		this.agent = agent;		
	}
	
	@Override
	public void run() {		
		agent.newLevel();
		
		while (shouldRun) {
			if (observe) {
				// EXTRACT COMPACT VERSION OF THE BOARD FOR AI
				BoardCompact compactBoard = board.makeBoardCompact();
				// PRESENT BOARD TO THE AGENT
				agent.observe(compactBoard);
				observe = false;
			}
			
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
				observe = true;
			}
			
			// NULLIFY THE ACTION
			agentAction = null;
		}
		
		
		
	}
	
}
