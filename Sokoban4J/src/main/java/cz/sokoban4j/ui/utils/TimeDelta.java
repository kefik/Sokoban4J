package cz.sokoban4j.ui.utils;

public class TimeDelta {
	
	private long lastMillis;
	
	private long currMillis;
	
	private long deltaMillis;
	
	private long deltaMillisCap = 40;
	
	public TimeDelta() {
		reset();
	}
	
	public void reset() {
		lastMillis = currMillis = System.currentTimeMillis();
		deltaMillis = 0;
	}
	
	public void tick() {		
		lastMillis = currMillis;
		currMillis = System.currentTimeMillis();
		deltaMillis = currMillis - lastMillis;
		if (deltaMillis > deltaMillisCap) deltaMillis = deltaMillisCap;
	}
	
	public long deltaMillis() {
		return deltaMillis;
	}
	
	public long realDeltaMills() {
		return System.currentTimeMillis() - lastMillis;
	}	

}
