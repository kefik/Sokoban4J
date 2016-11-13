package cz.sokoban4j;

import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.actions.oop.IAction;
import cz.sokoban4j.simulation.actions.oop.MoveOrPush;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.simulation.board.compact.BoardCompact;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.ui.SokobanFrame;
import cz.sokoban4j.ui.SokobanView;
import cz.sokoban4j.ui.UIBoard;
import cz.sokoban4j.ui.actions.IUIAction;
import cz.sokoban4j.ui.actions.UIActionFactory;
import cz.sokoban4j.ui.atlas.SpriteAtlas;
import cz.sokoban4j.ui.utils.TimeDelta;

public class SokobanVis extends Thread {

	private Board board;
	private IAgent agent;
	private SpriteAtlas sprites;
	private UIBoard uiBoard;
	private SokobanView view;
	private SokobanFrame frame;
	
	private TimeDelta timeDelta;
	
	private IAction agentAction;
	private IUIAction uiAction;
	
	private boolean observe = true;
	
	private boolean firstPack = true;

	public boolean shouldRun = true;
	
	public int steps = 0;
	
	public SokobanVis(Board board, IAgent agent, SpriteAtlas sprites, UIBoard uiBoard, SokobanView view, SokobanFrame frame) {
		super("SokobanVis");
		
		this.board = board;
		this.agent = agent;
		this.sprites = sprites;
		this.uiBoard = uiBoard;
		this.view = view;
		this.frame = frame;
		this.timeDelta = new TimeDelta();
		
		if (agent instanceof KeyListener) {
			frame.addKeyListener((KeyListener)agent);
		}
	}
	
	@Override
	public void run() {		
		agent.newLevel();
		timeDelta.reset();
		while (shouldRun) {
			timeDelta.tick();
			
			if (uiAction != null) {
				// ADVANCE UI ACTION
				if (!uiAction.isFinished()) uiAction.tick(timeDelta);
				// HANDLE FINISHED ACTION
				if (uiAction.isFinished()) {
					agentAction.perform(board);
					uiAction.finish();
					uiAction = null;
					agentAction = null;
					observe = true;
				}
				
				// UPDATE RENDER
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						view.render();
						view.repaint();
						frame.repaint();						
					}
				});
			}
						
			if (board.isVictory()) {
				agent.victory();
				return;
			}
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted on SLEEP");
			}
			
			if (uiAction != null) {
				// WE HAVE UI ACTION IN EFFECT ... wait till it finishes
				continue;
			}
			
			if (firstPack) {
				frame.pack();
				firstPack = false;
			}
			
			// OTHERWISE QUERY AGENT FOR THE NEXT ACTION
			
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
				// START PERFORMIING THE ACTION
				uiAction = UIActionFactory.createUIAction(board, sprites, uiBoard, agentAction);
				if (uiAction == null) {
					// INVALID ACTION
					agentAction = null;					 
				} else {
					++steps;
					frame.setSteps(steps);
				}
			} else {
				agentAction = null;
			}
		}
		
	}
	
}
