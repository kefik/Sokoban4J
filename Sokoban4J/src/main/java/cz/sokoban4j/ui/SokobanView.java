package cz.sokoban4j.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class SokobanView extends JComponent {

	private Board board;
	private SpriteAtlas sprites;
	private UIBoard uiBoard;
	
	private BufferedImage canvas;
	private Graphics2D canvasGraphics;
	
	private int scaleDown = 1;
	private BufferedImage scaledDownCanvas;
	private Graphics2D scaledDownCanvasGraphics;
	private AffineTransform scaleDownAT;
	private AffineTransformOp scaleDownOP;
	
	private boolean renderRequested = false;
	
	public SokobanView(Board board, SpriteAtlas sprites, UIBoard uiBoard) {
		this.board = board;
		this.sprites = sprites;
		this.uiBoard = uiBoard;
		canvas = new BufferedImage(board.width * sprites.getTileWidth(), board.height * sprites.getTileHeight(), BufferedImage.TYPE_INT_ARGB);
		canvasGraphics = canvas.createGraphics();
		
		scaleDown = 1;
		Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();		
		while (scaleDown < 8 && canvas.getWidth() / scaleDown > screen.getWidth()-40) scaleDown *= 2;
		while (scaleDown < 8 && canvas.getHeight() / scaleDown > screen.getHeight()-40) scaleDown *= 2;
		
		setSize(canvas.getWidth() / scaleDown, canvas.getHeight() / scaleDown);
		setPreferredSize(new Dimension(canvas.getWidth() / scaleDown, canvas.getHeight() / scaleDown));
		
		if (scaleDown > 1) {
			scaledDownCanvas = new BufferedImage(canvas.getWidth() / scaleDown, canvas.getHeight() / scaleDown, BufferedImage.TYPE_INT_ARGB);
			scaleDownAT = new AffineTransform();
			scaleDownAT.scale((double)1 / (double)(scaleDown), (double)1 / (double)(scaleDown));
			scaleDownOP = new AffineTransformOp(scaleDownAT, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		}
	}
	
	/**
	 * MUST BE CALLED VIA {@link SwingUtilities#invokeLater(Runnable)} !
	 */
	public void render() {
		uiBoard.render(canvasGraphics);
		scaleDown();
	}
	
	private void scaleDown() {
		if (scaleDown <= 1) return;				
		scaledDownCanvas = scaleDownOP.filter(canvas, scaledDownCanvas);
	}

	public void renderLater() {
		synchronized(this) {
			if (renderRequested) return;
			renderRequested = true;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try { 
					render();
				} finally {
					renderRequested = false;
				}
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (scaleDown == 1) {
			g.drawImage(canvas, 0, 0, this);
		} else {
			g.drawImage(scaledDownCanvas, 0, 0, this);
		}
	}
	
}
