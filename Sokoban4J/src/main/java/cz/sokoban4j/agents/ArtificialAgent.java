package cz.sokoban4j.agents;

import java.util.List;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.simulation.board.compact.BoardCompact;

public abstract class ArtificialAgent implements IAgent {

	private List<EDirection> actions;
	
	private BoardCompact board;
		
	private ThinkThread thread;
	
	private Object mutex = new Object();
	
	private RuntimeException agentException;

	@Override
	public void newLevel() {
		actions = null;
		board = null;
		agentException = null;
	}

	@Override
	public void observe(BoardCompact board) {
		this.board = board;
	}

	@Override
	public EDirection act() {
		// HAVE ACTION?
		if (actions != null && actions.size() != 0) {
			// => EXECUTE
			return actions.remove(0);
		}
		
		// OTHERWISE THINK!
		synchronized(mutex) {
			if (agentException != null) {
				throw agentException;
			}
			ensureThinkThread();
			if (actions == null || actions.size() == 0) {
				thread.think = true;
			}
			if (actions != null && actions.size() != 0) {
				// => EXECUTE
				return actions.remove(0);
			}
		}
		return null;
	}
	
	protected abstract List<EDirection> think(final BoardCompact board);

	@Override
	public void victory() {
		stopThinkThread();		
	}
	
	@Override
	public void stop() {
		stopThinkThread();
	}
	
	private void ensureThinkThread() {
		synchronized(mutex) {
			if (thread != null && thread.running) return;
			thread = new ThinkThread();
			thread.start();
		}
	}
	
	private void stopThinkThread() {
		synchronized(mutex) {
			if (thread != null) {
				thread.shouldRun = false;
				thread.interrupt();
				thread = null;
			}
		}
	}
	
	protected class ThinkThread extends Thread {
		
		public boolean running = true;
		
		public boolean shouldRun = true;
		
		public boolean think = false;
		
		public ThinkThread() {
			super("ThinkThread");
		}
		
		@Override
		public void run() {
			try {
				while (shouldRun && !interrupted()) {
					while (!think) {
						try {
							Thread.sleep(50);
						} catch (Exception e) {
							throw new RuntimeException("Interrupted on sleep");
						}
					}
					List<EDirection> thinkActions = think(board);
					synchronized(mutex) {
						if (ArtificialAgent.this.thread == this) {
							actions = thinkActions;
						}
						think = false;
					}
				}
			} catch (Exception e) {
				synchronized(mutex) {
					agentException = new RuntimeException("ThinkThread failed.", e);
				}
			} finally {
				running = false;
			}
		}
		
	}

	
}
