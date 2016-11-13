package cz.sokoban4j.ui.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cz.sokoban4j.simulation.board.oop.entities.Entity;
import cz.sokoban4j.ui.BaseRenderer;
import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class UIEntity extends BaseRenderer {

	public Entity entity;
	
	public double offsetX = 0;
	public double offsetY = 0;
	
	public String currentSprite;

	public UIEntity(Entity entity, SpriteAtlas sprites) {
		super(sprites);
		this.entity = entity;
		this.currentSprite = entity.getType().getSprite();
	}
	
	public void renderEntity(Graphics2D g) {
		BufferedImage sprite = sprites.getSprite(currentSprite);
		if (sprite != null) renderSprite(g, sprite, entity.getTileX(), entity.getTileY(), offsetX, offsetY);
	}		
	
}
