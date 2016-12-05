package cz.sokoban4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import cz.sokoban4j.SokobanConfig.ELevelFormat;
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
		Board result = null;
		switch (config.levelFormat) {
		case S4JL: result = Board.fromFileS4JL(config.level, config.levelNumber); break;
		case SOK: result = Board.fromFileSok(config.level, config.levelNumber); break;
		}
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
		return frame = new SokobanFrame(view, board.level);
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
		} else 
		if (config.level.isFile() && config.levelNumber == -1) {
			runFile();
		} else {
			runLevel();			
		}
	}
	
	private void runDir() {
		// READ LEVELS
		List<File> levels = new ArrayList<File>();		
		SokobanConfig config = this.config;
		File levelDir = config.level;
		ELevelFormat levelFormat = config.levelFormat;
		try {
			for (File file : config.level.listFiles()) {
				if (config.levelFormat != null) {
					if (file.getAbsolutePath().endsWith(config.levelFormat.getExtension())) levels.add(file);
				} else {
					ELevelFormat format = determineLevelFormat(file.getName());
					if (format != null) {
						levels.add(file);
					}
				}
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
				this.config.levelFormat = ELevelFormat.getExpectedLevelFormat(level);
				this.config.levelNumber = -1;
				// RUN GAME
				runFile();				
			}	
		} finally {
			if (config != null) {
				config.level = levelDir;
				config.levelFormat = levelFormat;
			}
		}
	}
	
	private void runFile() {
		// RUN ALL LEVELS WITHIN ONE FILE
		if (config.levelNumber >= 0) {
			// run particular level
			runLevel();				
		} else {
			// run all levels
			SokobanConfig config = this.config;
			int levelNumber = 0;
			try {
				while (true) {
					// RESET INSTANCE (does not reset this.results)
					reset();
					// WAIT A BIT BETWEEN GAMES
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						throw new RuntimeException("Interrupted on Thread.sleep(100) in between levels.");
					}
					if (levelNumber != 0 && (getLastResult() == null || getLastResult().getResult() != SokobanResultType.VICTORY)) {
						// AGENT FAILED TO PASS THE LEVEL...
						break;
					}
					// INIT CONFIG					
					setConfig(config);					
					config.levelNumber = levelNumber;
					// TRY TO LOAD THE BOARD
					try {
						initBoard();
					} catch (Exception e) {
						// FAILED TO LOAD THE LEVEL => end of file hopefully
						break;
					}
					runLevel();				
					++levelNumber;
				}
			} finally {
				config.levelNumber = -1;
			}
			
		}
	}
		
	private void runLevel() {
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
	
	private static ELevelFormat determineLevelFormat(String pathToFile) {
		ELevelFormat levelFormat = ELevelFormat.getExpectedLevelFormat(new File(pathToFile));
		if (levelFormat == null) {
			throw new RuntimeException("Could not determine ELevelFormat for: " + pathToFile);
		}
		return levelFormat;
	}
	
	private static String determineId(IAgent agent) {
		return agent == null ? "NULL" : agent.getClass().getSimpleName();
	}
	
	// ----------------
	// GENERIC STARTUPS
	// ----------------

	/**
	 * Runs Sokoban game according to the 'config'; method assumes the configuration is going to play single level only.
	 * 
	 * @param config
	 * @return
	 */
	public static SokobanResult runAgentLevel(SokobanConfig config) {
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		return sokoban.getResult();
	}
	
	/**
	 * Runs Sokoban game according to the 'config'; method assumes the configuration is going to play one or more levels.
	 * If there are multiple levels to be played, the run will stop when agent fails to solve the level.
	 * 
	 * @param config
	 * @return
	 */
	public static List<SokobanResult> runAgentLevels(SokobanConfig config) {
		Sokoban sokoban = new Sokoban();
		sokoban.run(config);
		return sokoban.getResults();
	}
	
	// --------------------
	// HEADLESS SIMULATIONS
	// --------------------
	
	/**
	 * 'agent' will play (headless == simulation only) 'levelNumber' (0-based) level from file on 'levelFilePath' assuming 'levelFormat'.
	 * An agent will be given 'timeoutMillis' time to solve the level.
	 * 
	 * @param id id to be given to the config; may be null
	 * @param levelFilePath file to load the level from
	 * @param levelFormat expected format of the file (if it is null, it will be auto-determined)
	 * @param levelNumber 0-based; a level to be played
	 * @param timeoutMillis time given to the agent to solve every level; non-positive number == no timeout
	 * @param agent
	 * @return
	 */
	public static SokobanResult simAgentLevel(String id, String levelFilePath, ELevelFormat levelFormat, int levelNumber, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		if (id == null) id = determineId(agent);
		config.id = id;
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		if (levelFormat == null) levelFormat = determineLevelFormat(levelFilePath);
		config.levelFormat = levelFormat;
		config.levelNumber = levelNumber;		
		config.visualization = false;
		config.timeoutMillis = timeoutMillis;
		
		return runAgentLevel(config);
	}
	
	/**
	 * 'agent' will play (headless == simulation only) all levels from file on 'levelFilePath' assuming 'levelFormat'.
	 * An agent will be given 'timeoutMillis' time to solve every level.
	 * The run will stop on the level the agent fail to solve.
	 * 
	 * @param id id to be given to the config; may be null
	 * @param levelFilePath file to load the level from
	 * @param levelFormat expected format of the file; if it is null, it will be auto-determined using {@link ELevelFormat#getExpectedLevelFormat(File)}
	 * @param levelNumber 0-based; a level to be played
	 * @param timeoutMillis time given to the agent to solve every level; non-positive number == no timeout
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> simAgentFile(String id, String levelFilePath, ELevelFormat levelFormat, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		if (id == null) id = determineId(agent);
		config.id = id;		
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		if (levelFormat == null) levelFormat = determineLevelFormat(levelFilePath);
		config.levelFormat = levelFormat;
		config.visualization = false;
		config.timeoutMillis = timeoutMillis;
		
		return runAgentLevels(config);
	}
	
	/**
	 * 'agent' will play (headless == simulation only) all levels found within directory on 'levelDirPath'.
	 * An agent will be given 'timeoutMillis' time to solve every level.
	 * The run will stop on the level the agent fail to solve.
	 * Files will be loaded in alphabetical order using {@link String#compareTo(String)}.
	 * 
	 * @param id id to be given to the config; may be null
	 * @param levelDirPath directory to load level files from
	 * @param levelNumber 0-based; a level to be played
	 * @param timeoutMillis time given to the agent to solve every level; non-positive number == no timeout
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> simAgentDir(String id, String levelDirPath, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		if (id == null) id = determineId(agent);
		config.id = id;
		config.agent = agent;
		config.level = new File(levelDirPath);
		if (!config.level.exists() || !config.level.isDirectory()) throw new RuntimeException("Not a directory at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelDirPath);
		config.visualization = false;
		config.timeoutMillis = timeoutMillis;
		
		return runAgentLevels(config);
	}
	
	/**
	 * 'agent' will play (headless == simulation only) FIRST level from file on 'levelFilePath'.
	 * 
	 * @param levelFilePath file to load
	 * @param agent
	 * @return
	 */
	public static SokobanResult simAgentLevel(String levelFilePath, IAgent agent) {
		return simAgentLevel(determineId(agent), levelFilePath, null, 0, -1, agent);
	}
	
	/**
	 * 'agent' will play (headless == simulation only) all levels from file on 'levelFilePath'.
	 * The run will stop on the level the agent fail to solve.
	 * 
	 * @param levelFilePath file to load
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> simAgentFile(String levelFilePath, IAgent agent) {
		return simAgentFile(null, levelFilePath, null, -1, agent);
	}
	
	/**
	 * 'agent' will play (headless == simulation only) levels found within the dir on 'levelDirPath'.
	 * The run will stop on the level the agent fail to solve.
	 * Files will be loaded in alphabetical order using {@link String#compareTo(String)}.
	 * 
	 * @param levelDirPath directory to read levels from
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> simAgentDir(String levelDirPath, IAgent agent) {
		return simAgentDir(null, levelDirPath, -1, agent);
	}
	
	// ----------------------
	// VISUALIZED SIMULATIONS
	// ----------------------
	
	/**
	 * 'agent' will play (visualized) 'levelNumber' (0-based) level from file on 'levelFilePath' assuming 'levelFormat'.
	 * An agent will be given 'timeoutMillis' time to solve the level.
	 * 
	 * @param id id to be given to the config; may be null
	 * @param levelFilePath file to load the level from
	 * @param levelFormat expected format of the file (if it is null, it will be auto-determined)
	 * @param levelNumber 0-based; a level to be played
	 * @param timeoutMillis time given to the agent to solve every level; non-positive number == no timeout
	 * @param agent
	 * @return
	 */
	public static SokobanResult playAgentLevel(String id, String levelFilePath, ELevelFormat levelFormat, int levelNumber, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		if (id == null) id = determineId(agent);
		config.id = id;
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		if (levelFormat == null) levelFormat = determineLevelFormat(levelFilePath);
		config.levelFormat = levelFormat;
		config.levelNumber = levelNumber;		
		config.visualization = true;
		config.timeoutMillis = timeoutMillis;
		
		return runAgentLevel(config);
	}
	
	/**
	 * 'agent' will play (visualized) all levels from file on 'levelFilePath' assuming 'levelFormat'.
	 * An agent will be given 'timeoutMillis' time to solve every level.
	 * The run will stop on the level the agent fail to solve.
	 * 
	 * @param id id to be given to the config; may be null
	 * @param levelFilePath file to load the level from
	 * @param levelFormat expected format of the file; if it is null, it will be auto-determined using {@link ELevelFormat#getExpectedLevelFormat(File)}
	 * @param levelNumber 0-based; a level to be played
	 * @param timeoutMillis time given to the agent to solve every level; non-positive number == no timeout
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> playAgentFile(String id, String levelFilePath, ELevelFormat levelFormat, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		if (id == null) id = determineId(agent);
		config.id = id;
		config.agent = agent;
		config.level = new File(levelFilePath);
		if (!config.level.exists() || !config.level.isFile()) throw new RuntimeException("Not a level file at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelFilePath);
		if (levelFormat == null) levelFormat = determineLevelFormat(levelFilePath);
		config.levelFormat = levelFormat;
		config.visualization = true;
		config.timeoutMillis = timeoutMillis;
		
		return runAgentLevels(config);
	}
	
	/**
	 * 'agent' will play (visualized) all levels found within directory on 'levelDirPath'.
	 * An agent will be given 'timeoutMillis' time to solve every level.
	 * The run will stop on the level the agent fail to solve.
	 * Files will be loaded in alphabetical order using {@link String#compareTo(String)}.
	 * 
	 * @param id id to be given to the config; may be null
	 * @param levelDirPath directory to load level files from
	 * @param levelNumber 0-based; a level to be played
	 * @param timeoutMillis time given to the agent to solve every level; non-positive number == no timeout
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> playAgentDir(String id, String levelDirPath, int timeoutMillis, IAgent agent) {
		// CREATE CONFIG
		SokobanConfig config = new SokobanConfig();
		if (id == null) id = determineId(agent);
		config.id = id;
		config.agent = agent;
		config.level = new File(levelDirPath);
		if (!config.level.exists() || !config.level.isDirectory()) throw new RuntimeException("Not a directory at '" + config.level.getAbsolutePath() + "'\nResolved from: " + levelDirPath);
		config.visualization = true;
		config.timeoutMillis = timeoutMillis;
		
		return runAgentLevels(config);
	}
	
	/**
	 * 'agent' will play (visualized) FIRST level from file on 'levelFilePath'.
	 * 
	 * @param levelFilePath file to load
	 * @param agent
	 * @return
	 */
	public static SokobanResult playAgentLevel(String levelFilePath, IAgent agent) {
		return playAgentLevel(null, levelFilePath, null, 0, -1, agent);
	}
	
	/**
	 * 'agent' will play (visualized) all levels from file on 'levelFilePath'.
	 * The run will stop on the level the agent fail to solve.
	 * 
	 * @param levelFilePath file to load
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> playAgentFile(String levelFilePath, IAgent agent) {
		return playAgentFile(null, levelFilePath, null, -1, agent);
	}
	
	/**
	 * 'agent' will play (visualized) levels found within the dir on 'levelDirPath'.
	 * The run will stop on the level the agent fail to solve.
	 * Files will be loaded in alphabetical order using {@link String#compareTo(String)}.
	 * 
	 * @param levelDirPath directory to read levels from
	 * @param agent
	 * @return
	 */
	public static List<SokobanResult> playAgentDir(String levelDirPath, IAgent agent) {
		return playAgentDir(null, levelDirPath, -1, agent);
	}
	
	// ----------------------
	// HUMAN PLAYING THE GAME
	// ----------------------
	
	/**
	 * Human will play the first level found within the file on 'levelFilePath'.
	 * @param levelFilePath path to the level file to load
	 * @return
	 */
	public static SokobanResult playHumanLevel(String levelFilePath) {
		return playAgentLevel(levelFilePath, new HumanAgent());
	}
	
	/**
	 * Human will play 'levelNumber' found within the file on 'levelFilePath'.
	 * 
	 * @param levelFilePath path to the level file to load
	 * @param levelNumber level number to be played; 0-based
	 * @return
	 */
	public static SokobanResult playHumanLevel(String levelFilePath, int levelNumber) {
		return playAgentLevel(levelFilePath, new HumanAgent());
	}
	
	/**
	 * Human will play all levels found within the file on 'levelFilePath'.
	 * @param levelFilePath path to the level file to load
	 * @return
	 */
	public static List<SokobanResult> playHumanFile(String levelFilePath) {
		return playAgentFile(levelFilePath, new HumanAgent());
	}
	
	/**
	 * Human will play all levels found within the dir on 'levelDirPath'.
	 * Files will be loaded in alphabetical order using {@link String#compareTo(String)}.
	 * 
	 * @param levelDirPath path to the directory containing levels to play
	 * @return
	 */
	public static List<SokobanResult> playHumanDir(String levelDirPath) {
		return playAgentDir(levelDirPath, new HumanAgent());
	}
	
	// ===========
	// MAIN METHOD
	// ===========
	
	public static void main(String[] args) {
		// PLAY SINGLE LEVEL
		
		//playHumanFile("levels/Easy/level0001.s4jl");
		//playHumanFile("levels/Easy/level0002.1.s4jl");
		//playHumanFile("levels/Easy/level0002.2.s4jl");
		//playHumanFile("levels/Easy/level0002.3.s4jl");
		//playHumanFile("levels/Easy/level0003.s4jl");
		//playHumanFile("levels/Easy/level0004.s4jl");
		//playHumanFile("levels/Easy/level0005.s4jl");
		//playHumanFile("levels/Easy/level0006.s4jl");
		//playHumanFile("levels/Easy/level0007.s4jl");
		//playHumanFile("levels/Easy/level0008.s4jl");
		//playHumanFile("levels/Easy/level0009.s4jl");
		//playHumanFile("levels/MultiBox/level0001.s4jl");
				
		// PLAY ALL LEVELS
		
		playHumanDir("levels/Easy");
		//playHumanFile("levels/sokobano.de/Blazz.sok");
	}
	
}
