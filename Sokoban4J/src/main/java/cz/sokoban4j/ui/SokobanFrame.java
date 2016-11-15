package cz.sokoban4j.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class SokobanFrame extends JFrame
{
    /**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -4722654501777832707L;
	
	private SokobanView view;
	
	private String level;
	
	private int steps = 0;
	
	private long timeLeftMillis = -1;
	
	private String prevTimeLeftMillisStr = "";

	public SokobanFrame(SokobanView view, String level)
    {		
    	this.view = view;
        getContentPane().add(BorderLayout.CENTER, view);
        this.setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        pack();
        
        center();        
        
        repaint();       
     
        setLevel(level);
    }
    
    public void setLevel(String level) {
    	this.level = level;
    	updateTitle();
	}
    
    public void setSteps(int steps) {
    	this.steps = steps;
    	updateTitle();
    }
    
    public void updateTitle() {
    	prevTimeLeftMillisStr = getTimeLeftStr();
    	setTitle("Sokoban4J - " + level + (steps > 0 ? " - Steps[" + steps + "]" : "") + (timeLeftMillis >= 0 ? " - " + prevTimeLeftMillisStr : ""));
    }
    
	public void setTimeLeftMillis(long timeLeftMillis) {
		this.timeLeftMillis = timeLeftMillis;
		if (timeLeftMillis < 0) return;
		if (getTimeLeftStr().equals(prevTimeLeftMillisStr)) return;
		updateTitle();
	}

	private String getTimeLeftStr() {
		long timeLeftSecs = timeLeftMillis / 1000;
		long timeLeftMins = timeLeftSecs / 60;
		timeLeftSecs = timeLeftSecs % 60;	
		long timeLeftMillis = this.timeLeftMillis % 1000;
		return timeLeftMins + ":" + (timeLeftSecs > 9 ? timeLeftSecs : "0" + timeLeftSecs) + "." + (timeLeftMillis / 100);
	}

	public void center() {
    	Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
    	this.setLocation((int)(screen.getWidth()- getWidth()) / 2,(int)(screen.getHeight() - getHeight()) / 2);
    }
    
}