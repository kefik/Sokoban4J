package cz.sokoban4j.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
	
	private boolean renderRequested = false;
	
	public SokobanView(Board board, SpriteAtlas sprites, UIBoard uiBoard) {
		this.board = board;
		this.sprites = sprites;
		this.uiBoard = uiBoard;
		canvas = new BufferedImage(board.width * sprites.getTileWidth(), board.height * sprites.getTileHeight(), BufferedImage.TYPE_INT_ARGB);
		canvasGraphics = canvas.createGraphics();
		setSize(canvas.getWidth(), canvas.getHeight());
		setPreferredSize(new Dimension(canvas.getWidth(), canvas.getHeight()));
	}
	
	/**
	 * MUST BE CALLED VIA {@link SwingUtilities#invokeLater(Runnable)} !
	 */
	public void render() {
		uiBoard.render(canvasGraphics);		
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
		g.drawImage(canvas, 0, 0, this);
	}
	
}
