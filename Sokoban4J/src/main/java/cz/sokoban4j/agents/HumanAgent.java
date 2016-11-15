package cz.sokoban4j.agents;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.simulation.board.compact.BoardCompact;

public class HumanAgent implements IAgent, KeyListener {

	EDirection direction = EDirection.NONE;
	
	@Override
	public void newLevel() {
	}
	
	@Override
	public void observe(BoardCompact board) {		
	}

	@Override
	public EDirection act() {
		return direction == null ? EDirection.NONE : direction;
	}

	@Override
	public void victory() {
	}
	
	@Override
	public void stop() {
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if      (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) direction = EDirection.UP;
        else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) direction = EDirection.RIGHT;
        else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) direction = EDirection.DOWN;
        else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) direction = EDirection.LEFT;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if      (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
			if (direction == EDirection.UP) direction = EDirection.NONE;
		}
        else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
        	if (direction == EDirection.RIGHT) direction = EDirection.NONE;
        }
        else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
        	if (direction == EDirection.DOWN) direction = EDirection.NONE;
        }
        else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
        	if (direction == EDirection.LEFT) direction = EDirection.NONE;
        }
		
	}
	
}
