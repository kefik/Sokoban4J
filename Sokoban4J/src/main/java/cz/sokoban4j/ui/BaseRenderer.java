package cz.sokoban4j.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class BaseRenderer {

	protected SpriteAtlas sprites;
	
	public BaseRenderer(SpriteAtlas sprites) {
		this.sprites = sprites;
	}
		
	protected void renderSprite(Graphics2D g, BufferedImage sprite, int tileX, int tileY) {
		renderSprite(g, sprite, tileX, tileY, 0, 0);
	}
	
	protected void renderSprite(Graphics2D g, BufferedImage sprite, int tileX, int tileY, double offsetX, double offsetY) {
		if (sprite == null) return; 
		
		int tileCenterX = sprites.getTileWidth() * tileX + sprites.getTileWidth() / 2;
		int tileCenterY = sprites.getTileHeight() * tileY + sprites.getTileHeight() / 2;

		int drawAtX = (int)Math.round(tileCenterX + centerOffsetX(sprite) + offsetX);
		int drawAtY = (int)Math.round(tileCenterY + centerOffsetY(sprite) + offsetY); 
		
		g.drawImage(
			// DATA TO RENDER
			sprite,
			// WHERE TO RENDER
			drawAtX, 
			drawAtY, 
			(int)Math.round(drawAtX + sprite.getWidth()),
			(int)Math.round(drawAtY + sprite.getHeight()),
			// WHAT REAGION FROM DATA TO RENDER
			0, 0, sprite.getWidth(), sprite.getHeight(),
			// IMAGE OBSEVER
			null
		);
	}
	
	protected int centerOffsetX(BufferedImage image) {
		return -(image.getWidth() / 2);
	}
	
	protected int centerOffsetY(BufferedImage image) {
		return -(image.getHeight() / 2);
	}
	
	
}
