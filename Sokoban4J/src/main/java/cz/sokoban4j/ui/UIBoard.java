package cz.sokoban4j.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.Tile;
import cz.sokoban4j.simulation.board.oop.entities.Entity;
import cz.sokoban4j.ui.atlas.SpriteAtlas;
import cz.sokoban4j.ui.entities.UIBox;
import cz.sokoban4j.ui.entities.UIPlayer;

public class UIBoard extends BaseRenderer {
		
	private Board board;
	
	private Color backgroundColor = new Color(0, 0, 0);
	
	private int uiBoardWidth = 0;
	private int uiBoardHeight = 0;
	
	public UIPlayer player = null;
	
	public Map<Entity, UIBox> entity2boxes = null;
	public Map<UIBox, Entity> boxes2entity = null;

	public UIBoard(SpriteAtlas sprites) {
		super(sprites);
	}
	
	public void init(Board board) {
		this.board = board;
		
		player = null;
		entity2boxes = new HashMap<Entity, UIBox>();
		boxes2entity = new HashMap<UIBox, Entity>();
		
		for (int x = 0; x < board.width; ++x) {
			for (int y = 0; y < board.height; ++y) {
				if (board.tile(x, y).isPlayer()) {
					player = new UIPlayer(board.tile(x, y).entity, sprites);
				} else
				if (board.tile(x, y).isSomeBox()) {
					UIBox box = new UIBox(board.tile(x, y).entity, sprites);
					if (board.tile(x, y).forBox(box.entity.getType())) {
						box.inPlace();
					} else {
						box.outOfPlace();
					}
					entity2boxes.put(board.tile(x, y).entity, box);
					boxes2entity.put(box, board.tile(x, y).entity);
				}
			}
		}
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public void renderClear(Graphics2D g) {
		uiBoardWidth = board.width * sprites.getTileWidth();
		uiBoardHeight = board.height * sprites.getTileHeight();
		
		g.setBackground(backgroundColor);
		g.clearRect(0, 0, uiBoardWidth, uiBoardHeight);
	}

	public void renderStatics(Graphics2D g) {
		for (int tileX = 0; tileX < board.width; ++tileX) {
			for (int tileY = 0; tileY < board.height; ++tileY) {
				Tile tile = board.tile(tileX, tileY);
				BufferedImage sprite;
				
				sprite = sprites.getSprite(tile.space.getSprite());
				renderSprite(g, sprite, tileX, tileY);
				
				sprite = sprites.getSprite(tile.place.getSprite());
				renderSprite(g, sprite, tileX, tileY);				
			}			
		}
	}
	
	public void renderEntities(Graphics2D g) {
		player.renderEntity(g);
		for (UIBox box : entity2boxes.values()) {
			box.renderEntity(g);
		}
	}
	
	public void render(Graphics2D g) {
		renderClear(g);
		renderStatics(g);
		renderEntities(g);
		renderVictory(g);
	}

	private void renderVictory(Graphics2D g) {
		if (!board.isVictory()) return;
		g.drawString("VICTORY!", 10, 20);
	}
	
}
