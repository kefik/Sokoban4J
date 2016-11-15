package cz.sokoban4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import cz.sokoban4j.ISokobanGame.SokobanGameState;
import cz.sokoban4j.agents.HumanAgent;
import cz.sokoban4j.simulation.SokobanResult;
import cz.sokoban4j.simulation.SokobanResult.SokobanResultType;
import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.ui.SokobanFrame;
import cz.sokoban4j.ui.SokobanView;
import cz.sokoban4j.ui.UIBoard;
import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class Sokoban {
	
	private List<SokobanResult> results = new ArrayList<SokobanResult>();
	
	private SokobanConfig config;
	
	private SpriteAtlas sprites;	
	private Board board;
	private UIBoard uiBoard;
	private SokobanView view;
	private SokobanFrame frame;	
	private ISokobanGame game;
	
	/**
	 * Resets everything except {@link #sprites} and {@link #results}.
	 */
	public void reset() {
		if (game != null) {
			game.stopGame();
			game = null;
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
		config = null;
	}
	
	/**
	 * Resets everything including {@link #sprites} and {@link #results}.
	 */
	public void resetAll() {
		reset();
		sprites = null;
	}
	
	/**
	 * Current config we're using for the {@link #getGame()}.
	 * @return
	 */
	public SokobanConfig getConfig() {
		return config;
	}

	/**
	 * Sprites we're currently using if any.
	 * @return
	 */
	public SpriteAtlas getSprites() {
		return sprites;
	}

	/**
	 * Board we're currently using according to {@link #getConfig()} if any.
	 * @return
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * UIBoard we're currently using for rendering of {@link #getBoard()} if any.
	 * @return
	 */
	public UIBoard getUiBoard() {
		return uiBoard;
	}

	/**
	 * {@link JComponent} visualizing {@link #getUiBoard()}  if any.
	 * @return
	 */
	public SokobanView getView() {
		return view;
	}

	/**
	 * Current {@link SokobanGame} if any.
	 * @return
	 */
	public SokobanFrame getFrame() {
		return frame;
	}
	
	/**
	 * Current game that is running if any.
	 * @return
	 */
	public ISokobanGame getGame() {
		return game;
	}
	
	/**
	 * Results this instance have aggregated so far.
	 * @return
	 */
	public List<SokobanResult> getResults() {
		return results;
	}
	
	/**
	 * Returns first result from {@link #getResults()} if any.
	 * @return
	 */
	public SokobanResult getResult() {
		if (results == null || results.size() == 0) return null;
		return results.get(0);
	}
	
	/**
	 * Returns last result from {@link #getResults()} if any.
	 * @return
	 */
	public SokobanResult getLastResult() {
		if (results == null || results.size() == 0) return null;
		return results.get(results.size()-1);
	}
	
	private void validateConfig() {
		if (config == null) throw new RuntimeException("Config is null! Have you forget to setConfig()?");
		config.validate();
	}
	
	private void setConfig(SokobanConfig config) {
		if (this.config != null) {
			throw new RuntimeException("Config already set! You have to reset() first!");
		}
		this.config = config;
	}
	
	private SpriteAtlas initSprites() {
		if (sprites != null) return sprites;
		SpriteAtlas result = new SpriteAtlas();
		result.load();
		return sprites = result;
	}
	
	private Board initBoard() {
		if (board != null) return board;
		// PREREQ
		validateConfig();		
		// IMPL
		Board result = Board.fromFile(config.level);
		result.validate();
		return board = result;
	}
	
	private UIBoard initUIBoard() {
		if (uiBoard != null) return uiBoard;
		// PREREQ
		initSprites();
		initBoard();
		// IMPL
		UIBoard result = new UIBoard(sprites);
		result.init(board);
		return uiBoard = result;
	}
	
	private SokobanView initView() {		
		if (view != null) return view;
		// PREREQ
		initUIBoard();
		// IMPL
		return view = new SokobanView(board, sprites, uiBoard);
	}
	
	private SokobanFrame initFrame() {
		if (frame != null) return frame;
		// PREREQ
		initView();
		// IMPL
		return frame = new SokobanFrame(view, config.level.getName());
	}
	
	/**
	 * Runs SOKOBAN according to the config.
	 * 
	 * Result of the game or games is going to be stored within {@link #getResults()}.
	 * Use {@link #getResults()}, {@link #getResult()} and {@link #getLastResult()} to obtain it/them.
	 * 
	 * @param config
	 * @return
	 */
	public void run(SokobanConfig config) {
		if (game != null) {
			throw new RuntimeException("Cannot run game as the game instance already exists; did you forget to reset()?");
		}
		
		setConfig(config);
		validateConfig();
		
		if (config.level.isDirectory()) {
			runDir();			
		} else {
			runOne();			
		}
	}
	
	private void runDir() {
		// READ LEVELS
		List<File> levels = new ArrayList<File>();		
		SokobanConfig config = this.config;
		File levelDir = config.level;
		try {
			for (File file : config.level.listFiles()) {
				if (file.getAbsolutePath().endsWith(".s4jl")) levels.add(file);
			}
			Collections.sort(levels, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}			
			});
			
			// PLAY THROUGH LEVELS
			for (File level : levels) {
				// BIND LEVEL
				this.config = config;
				this.config.level = level;
				// RUN GAME
				runOne();
				// RESET INSTANCE (does not reset this.results)
				reset();
				// WAIT A BIT BETWEEN GAMES
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					throw new RuntimeException("Interrupted on Thread.sleep(100) in between levels.");
				}
				if (getLastResult() == null || getLastResult().getResult() != SokobanResultType.VICTORY) {
					// AGENT FAILED TO PASS THE LEVEL...
					break;
				}
			}	
		} finally {
			if (config != null) config.level = levelDir;
		}
	}
	
	private void runOne() {
		if (config.visualization) {
			runVisualization();
		} else {
			runSimulation();
		}		
	}
		
	private void runSimulation() {
		// PREREQS
		validateConfig();
		initBoard();		
		// IMPL
		
		// START GAME W/O VISUALIZATION
		runGame(new SokobanSim(config.id, board, config.agent, config.timeoutMillis));		
	}
	
	private void runVisualization() {
		// PREREQS
		validateConfig();
		initFrame();
		// IMPL
		
		// OPEN FRAME
		view.renderLater();
		frame.setVisible(true);
		
		// START GAME WITH VISUALIZATION
		runGame(new SokobanVis(config.id, board, config.agent, sprites, uiBoard, view, frame, config.timeoutMillis));
	}
	
	private void runGame(ISokobanGame game) {
		this.game = game;
		game.startGame();
		try {
			game.waitFinish();
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted on game.waitFinish()");
		}
		if (game.getResult() != null) {
			SokobanResult result = game.getResult();
			results.add(result);
			if (result.getResult() == SokobanResultType.SIMULATION_EXCEPTION) {
				throw new RuntimeException("Game failed.", result.getExecption());
			}
		}	
	}
	
	// ================================
	// STATIC METHODS FOR EASY START-UP 
	// ================================
	
	// --------------------
	// HEADLESS SIMULATIONS
	// --------------------
	
	/**
	 * 'agent' will play one level from 'levelPath'.
	 * @param levelFilePath
	 * @param agent
	 * @return
	 */
	public static SokobanResult simAgent(String levelFilePath, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();	
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		config.visualization = false;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResult();
	}
	
	public static List<SokobanResult> simAgentDir(String levelDirPath, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();	
		config.agent = agent;
		config.level = new File(levelDirPath);
		if (!config.level.exists() || !config.level.isDirectory()) throw new RuntimeException("Not a directory at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelDirPath);
		config.visualization = false;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResults();
	}
	
	public static SokobanResult simAgent(String id, String levelFilePath, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		config.id = id;
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		config.visualization = false;
		config.timeoutMillis = timeoutMillis;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResult();
	}
	
	public static List<SokobanResult> simAgentDir(String id, String levelDirPath, int timeoutMillisPerLevel, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		config.id = id;
		config.agent = agent;
		config.level = new File(levelDirPath);
		if (!config.level.exists() || !config.level.isDirectory()) throw new RuntimeException("Not a directory at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelDirPath);
		config.visualization = false;
		config.timeoutMillis = timeoutMillisPerLevel;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResults();
	}
	
	// ----------------------
	// VISUALIZED SIMULATIONS
	// ----------------------
	
	public static SokobanResult playAgent(String levelFilePath, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();	
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		config.visualization = true;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResult();
	}
	
	public static List<SokobanResult> playAgentDir(String levelDirPath, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();	
		config.agent = agent;
		config.level = new File(levelDirPath);
		if (!config.level.exists() || !config.level.isDirectory()) throw new RuntimeException("Not a directory at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelDirPath);
		config.visualization = true;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResults();
	}
	
	public static SokobanResult playAgent(String id, String levelFilePath, IAgent agent, long timeoutMillis) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();	
		config.id = id;
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		config.visualization = true;
		config.timeoutMillis = timeoutMillis;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResult();
	}
	
	public static List<SokobanResult> playAgentDir(String id, String levelDirPath, IAgent agent, long timeoutMillis) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();	
		config.id = id;
		config.agent = agent;
		config.level = new File(levelDirPath);
		if (!config.level.exists() || !config.level.isDirectory()) throw new RuntimeException("Not a directory at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelDirPath);
		config.visualization = true;
		config.timeoutMillis = timeoutMillis;
		
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		
		return sokoban.getResults();
	}
	
	// ----------------------
	// HUMAN PLAYING THE GAME
	// ----------------------
	
	public static SokobanResult playHuman(String levelFilePath) {
		return playAgent(levelFilePath, new HumanAgent());
	}
	
	public static SokobanResult playHuman(String levelFilePath, long timeoutMillis) {
		return playAgent("SokobanHuman", levelFilePath, new HumanAgent(), timeoutMillis);
	}
	
	public static List<SokobanResult> playHumanDir(String levelDirPath) {
		return playAgentDir(levelDirPath, new HumanAgent());
	}
	
	public static List<SokobanResult> playHumanDir(String levelDirPath, long timeoutMillisPerLevel) {
		return playAgentDir("SokobanHuman", levelDirPath, new HumanAgent(), timeoutMillisPerLevel);
	}
	
	// ===========
	// MAIN METHOD
	// ===========
	
	public static void main(String[] args) {
		// PLAY SINGLE LEVEL
		
		//playHuman("levels/level0001.s4jl");
		//playHuman("levels/level0002.1.s4jl");
		//playHuman("levels/level0002.2.s4jl");
		//playHuman("levels/level0002.3.s4jl");
		//playHuman("levels/level0003.s4jl");
		
		// PLAY ALL LEVELS
		
		playHumanDir("levels");
	}
	
}
