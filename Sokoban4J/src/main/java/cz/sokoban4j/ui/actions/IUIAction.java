package cz.sokoban4j.ui.actions;

import cz.sokoban4j.ui.utils.TimeDelta;

public interface IUIAction {
	
	public void start();
	
	public void tick(TimeDelta time);
	
	public boolean isFinished();
	
	public void finish();
	

}
