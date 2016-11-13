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

	@Override
	public void newLevel() {
		actions = null;
		board = null;
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
	public void die() {
		stopThinkThread();
	}
	
	private void ensureThinkThread() {
		if (thread != null && thread.running) return;
		thread = new ThinkThread();
		thread.start();
	}
	
	private void stopThinkThread() {
		if (thread != null) {
			thread.shouldRun = false;
			thread.interrupt();
			thread = null;
		}
	}
	
	protected class ThinkThread extends Thread {
		
		public boolean running = false;
		
		public boolean shouldRun = true;
		
		public boolean think = false;
		
		public ThinkThread() {
			super("ThinkThread");
		}
		
		@Override
		public void run() {
			running = true;
			try {
				while (shouldRun && !interrupted()) {
					while (!think) {
						try {
							Thread.sleep(50);
						} catch (Exception e) {
							throw new RuntimeException("Interrupted on sleep");
						}
					}
					actions = think(board);
					synchronized(mutex) {
						think = false;
					}
				}
			} finally {
				running = false;
			}
		}
		
	}

	
}
