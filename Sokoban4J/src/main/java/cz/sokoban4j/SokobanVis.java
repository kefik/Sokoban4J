package cz.sokoban4j;

import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

import cz.sokoban4j.simulation.SokobanResult;
import cz.sokoban4j.simulation.SokobanResult.SokobanResultType;
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

public class SokobanVis implements ISokobanGame, Runnable {

	// SETUP
	
	private Board board;
	private IAgent agent;
	private SpriteAtlas sprites;
	private UIBoard uiBoard;
	private SokobanView view;
	private SokobanFrame frame;
	private long timeoutMillis;
	
	// THREAD
	
	private Thread gameThread;
	
	// RUNTIME
	
	private SokobanGameState state;
	
	private TimeDelta timeDelta;
	
	private IAction agentAction;
	private IUIAction uiAction;
	
	private boolean observe = true;
	
	private boolean firstPack = true;

	private boolean shouldRun = true;
	
	// RESULT
	
	private SokobanResult result = new SokobanResult();
	
	/**
	 * @param id
	 * @param board
	 * @param agent
	 * @param sprites
	 * @param uiBoard
	 * @param view
	 * @param frame
	 * @param timeoutMillis negative number or zero == no time; in milliseconds
	 */
	public SokobanVis(String id, Board board, IAgent agent, SpriteAtlas sprites, UIBoard uiBoard, SokobanView view, SokobanFrame frame, long timeoutMillis) {
		// SETUP
		
		if (id == null) id = "SokobanVis";		
		this.board = board;
		this.agent = agent;
		this.sprites = sprites;
		this.uiBoard = uiBoard;
		this.view = view;
		this.frame = frame;
		this.timeoutMillis = timeoutMillis;
		
		// RUNTIME
		
		this.state = SokobanGameState.INIT;
		
		this.timeDelta = new TimeDelta();
		
		if (agent instanceof KeyListener) {
			frame.addKeyListener((KeyListener)agent);
		}
		
		// RESULT
		
		result.setId(id);
		result.setAgent(agent);
		result.setLevel(board.level == null ? "N/A" : board.level);
	}
	
	@Override
	public void startGame() {
		if (state != SokobanGameState.INIT) return;
		try { 
			state = SokobanGameState.RUNNING;
			gameThread = new Thread(this, "SokobanVis");
			gameThread.start();
		} catch (Exception e) {
			stopGame();  
			onSimulationException(e);
		}
	}
	
	@Override
	public void stopGame() {
		if (state != SokobanGameState.RUNNING) return;
		try {
			shouldRun = false;
			gameThread.interrupt();
			try {
				if (gameThread.isAlive()) {
					gameThread.join();
				}
			} catch (Exception e) {			
			}
			gameThread = null;
			onTermination();
		} catch (Exception e) {
			onSimulationException(e);
		}
	}
	
	private boolean renderUpdating = false;
	
	@Override
	public void run() {
		try {
			frame.setVisible(true);
		
			result.setSimStartMillis(System.currentTimeMillis());
			
			try {
				agent.newLevel();
			} catch (Exception e) {
				onAgentException(e);
				return;
			}
			
			timeDelta.reset();
			
			renderUpdating = false;
			
			while (shouldRun && !Thread.interrupted()) {
				if (timeoutMillis > 0) {
					// TIMEOUT?
					long timeLeftMillis = timeoutMillis - (System.currentTimeMillis() - result.getSimStartMillis());
					if (timeLeftMillis <= 0) {						
						onTimeout();
						return;
					}					
					// UPDATE FRAME					
					frame.setTimeLeftMillis(timeLeftMillis);
				}
				
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
					if (!renderUpdating) {
						renderUpdating = true;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								view.render();
								view.repaint();
								frame.repaint();
								renderUpdating = false;
							}
						});
					}
				}
							
				if (board.isVictory()) {					
					onVictory();				 
					return;
				}
				
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					// WE HAVE TO STOP...
					onTermination();
					return;
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
						result.setSteps(result.getSteps()+1);
						frame.setSteps(result.getSteps());
					}
				} else {
					agentAction = null;
				}
			}
		} catch (Exception e) {
			onSimulationException(e);
		} finally {
			frame.setVisible(false);			
		}
	}

	private void onSimulationException(Exception e) {
		result.setSimEndMillis(System.currentTimeMillis());
		result.setResult(SokobanResultType.SIMULATION_EXCEPTION);
		result.setExecption(e);
		try {
			agent.stop();
		} catch (Exception e2) {						
		}		
		shouldRun = false;
		state = SokobanGameState.FAILED;
	}

	private void onTermination() {
		result.setSimEndMillis(System.currentTimeMillis());
		result.setResult(SokobanResultType.TERMINATED);
		try {
			agent.stop();
		} catch (Exception e) {						
		}
		shouldRun = false;
		state = SokobanGameState.TERMINATED;
	}

	private void onVictory() {
		result.setSimEndMillis(System.currentTimeMillis());
		result.setResult(SokobanResultType.VICTORY);
		try {
			agent.victory();
		} catch (Exception e) {
			onAgentException(e);
			return;
		}		
		state = SokobanGameState.FINISHED;
		try {
			agent.stop();
		} catch (Exception e) {						
		}
	}

	private void onTimeout() {
		frame.setTimeLeftMillis(0);
		result.setSimEndMillis(System.currentTimeMillis());		
		result.setResult(SokobanResultType.TIMEOUT);		
		try {
			agent.stop();
		} catch (Exception e) {						
		}		
		shouldRun = false;		
		state = SokobanGameState.FINISHED;
	}

	private void onAgentException(Exception e) {
		result.setSimEndMillis(System.currentTimeMillis());
		result.setResult(SokobanResultType.AGENT_EXCEPTION);
		result.setExecption(e);
		try {
			agent.stop();
		} catch (Exception e2) {						
		}		
		shouldRun = false;
		state = SokobanGameState.FAILED;
	}

	@Override
	public SokobanGameState getGameState() {
		return state;
	}

	@Override
	public SokobanResult getResult() {
		if (state == SokobanGameState.INIT || state == SokobanGameState.RUNNING) return null;
		return result;
	}

	@Override
	public SokobanResult waitFinish() throws InterruptedException {
		switch (state) {
		case INIT:
			return null;
			
		case RUNNING:
			if (gameThread != null && gameThread.isAlive()) this.gameThread.join();
			return getResult();
		
		default:
			return result;
		}
	}
	
}
