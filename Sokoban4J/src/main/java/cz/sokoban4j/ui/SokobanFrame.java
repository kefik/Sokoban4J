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
    	setTitle("Sokoban4J - " + level + (steps > 0 ? " - Steps[" + steps + "]" : ""));
    }

	public void center() {
    	Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
    	this.setLocation((int)(screen.getWidth()- getWidth()) / 2,(int)(screen.getHeight() - getHeight()) / 2);
    }
    
}