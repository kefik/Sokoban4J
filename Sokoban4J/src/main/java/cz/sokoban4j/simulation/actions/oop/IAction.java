package cz.sokoban4j.simulation.actions.oop;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.oop.Board;

public interface IAction {
	
	public EActionType getType(Board board);
	
	public EDirection getDirection();
	
	public boolean isPossible(Board board);
	
	public boolean perform(Board board);

}
