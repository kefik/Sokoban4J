package cz.sokoban4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.SwingUtilities;

import cz.sokoban4j.agents.HumanAgent;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.ui.SokobanFrame;
import cz.sokoban4j.ui.SokobanView;
import cz.sokoban4j.ui.UIBoard;
import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class Sokoban {
	
	private File levelFile;
	private SpriteAtlas sprites;	
	private Board board;
	private UIBoard uiBoard;
	private SokobanView view;
	private SokobanFrame frame;
	private IAgent agent;
	
	private SokobanSim simulation;
	private SokobanVis visualization;
	
	/**
	 * Resets everything except {@link #sprites} and {@link #agent}.
	 */
	public void reset() {
		if (visualization != null) {
			visualization.shouldRun = false;
			visualization.interrupt();
			visualization = null;
		}
		if (simulation != null) {
			simulation.shouldRun = false;
			simulation.interrupt();
			simulation = null;
		}
		if (frame != null) {
			final SokobanFrame frameToDispose = frame; 
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frameToDispose.setVisible(false);
						frameToDispose.dispose();									
					}
				}
			);
			frame = null;
		}
		view = null;
		uiBoard = null;
		board = null;
		levelFile = null;
	}
	
	public File getLevelFile() {
		return levelFile;
	}

	public SpriteAtlas getSprites() {
		return sprites;
	}

	public Board getBoard() {
		return board;
	}

	public UIBoard getUiBoard() {
		return uiBoard;
	}

	public SokobanView getView() {
		return view;
	}

	public SokobanFrame getFrame() {
		return frame;
	}
	
	public SokobanSim getSimulation() {
		return simulation;
	}

	public SokobanVis getVisualization() {
		return visualization;
	}

	public void setLevelFile(File levelFile) {
		this.levelFile = levelFile;
	}
	
	public SpriteAtlas initSprites() {
		if (sprites != null) return sprites;
		SpriteAtlas result = new SpriteAtlas();
		result.load();
		return sprites = result;
	}
	
	public Board initBoard() {
		if (board != null) return board;
		if (levelFile == null) throw new RuntimeException("levelFile not specified! Did you forget to call Sokoban.setLevelFile()?");
		// PREREQ
		initSprites();
		// IMPL
		Board result = Board.fromFile(levelFile);
		result.validate();
		return board = result;
	}
	
	public UIBoard initUIBoard() {
		if (uiBoard != null) return uiBoard;
		// PREREQ
		initSprites();
		initBoard();
		// IMPL
		UIBoard result = new UIBoard(sprites);
		result.init(board);
		return uiBoard = result;
	}
	
	public SokobanView initView() {		
		if (view != null) return view;
		// PREREQ
		initUIBoard();
		// IMPL
		return view = new SokobanView(board, sprites, uiBoard);
	}
	
	public SokobanFrame initFrame() {
		if (frame != null) return frame;
		// PREREQ
		initView();
		// IMPL
		return frame = new SokobanFrame(view, levelFile.getName());
	}
	
	public void setAgent(IAgent agent) {
		this.agent = agent;
	}
	
	public void runSimulation() {
		if (agent == null) throw new RuntimeException("'agent' cannot be null! Did you forget to call setAgent() first?");
		// PREREQ
		initBoard();		
		// IMPL
		
		// START GAME W/O VISUALIZATION
		simulation = new SokobanSim(board, agent);
		simulation.start();
	}
	
	public void runVisualization() {
		if (agent == null) throw new RuntimeException("'agent' cannot be null! Did you forget to call setAgent() first?");
		// PREREQ
		initFrame();
		// IMPL
		
		// OPEN FRAME
		view.renderLater();
		frame.setVisible(true);
		
		// START GAME WITH VISUALIZATION
		visualization = new SokobanVis(board, agent, sprites, uiBoard, view, frame);
		visualization.start();
	}
	
	public void runVisualization(File levelsDirectiry) {
		if (agent == null) throw new RuntimeException("'agent' cannot be null! Did you forget to call setAgent() first?");

		// READ LEVELS
		List<File> levels = new ArrayList<File>();
		for (File file : levelsDirectiry.listFiles()) {
			if (file.getAbsolutePath().endsWith(".s4jl")) levels.add(file);
		}
		levels.sort(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}			
		});
		
		// PLAY THROUGH LEVELS
		for (File level : levels) {
			// BIND LEVEL
			setLevelFile(level);
			// INIT FRAME
			initFrame();
			// OPEN FRAME
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					view.render();
					frame.setVisible(true);
				}
			});
			// START GAME WITH VISUALIZATION
			visualization = new SokobanVis(board, agent, sprites, uiBoard, view, frame);
			visualization.start();
			// WAIT FOR THE VICTORY
			try {
				visualization.join();				
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while visualization.join()");
			}
			reset();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	// ================================
	// STATIC METHODS FOR EASY START-UP 
	// ================================
	
	public static void simAgent(String levelFilePath, IAgent agent) {
		File levelFile = new File(levelFilePath);
		if (!levelFile.exists() || !levelFile.isFile()) throw new RuntimeException("File does not exist: " + levelFile.getAbsolutePath() + "\nResolved from: " + levelFilePath);
		
		Sokoban sokoban = new Sokoban();

		sokoban.setLevelFile(levelFile);
		sokoban.setAgent(agent);
		
		sokoban.runSimulation();
	}

	
	public static void playAgent(String levelFilePath, IAgent agent) {
		File levelFile = new File(levelFilePath);
		if (!levelFile.exists() || !levelFile.isFile()) throw new RuntimeException("File does not exist: " + levelFile.getAbsolutePath() + "\nResolved from: " + levelFilePath);
		
		Sokoban sokoban = new Sokoban();

		sokoban.setLevelFile(levelFile);
		sokoban.setAgent(agent);
		
		sokoban.runVisualization();
	}
	
	public static void playAgentDir(String levelDirPath, IAgent agent) {
		File levelDir = new File(levelDirPath);
		if (!levelDir.exists() || !levelDir.isDirectory()) throw new RuntimeException("Directory does not exist: " + levelDir.getAbsolutePath() + "\nResolved from: " + levelDirPath);
		Sokoban sokoban = new Sokoban();

		sokoban.setAgent(agent);
		
		sokoban.runVisualization(levelDir);
	}
	
	public static void playHuman(String levelFilePath) {
		playAgent(levelFilePath, new HumanAgent());
	}
	
	public static void playHumanDir(String levelDirPath) {
		playAgentDir(levelDirPath, new HumanAgent());
	}
	
	public static void main(String[] args) {
		//playHuman("levels/level0003.s4jl");
		playHumanDir("levels");
	}
	
}
