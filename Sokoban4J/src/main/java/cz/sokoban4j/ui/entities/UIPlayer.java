package cz.sokoban4j.ui.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.entities.Entity;
import cz.sokoban4j.ui.atlas.SpriteAtlas;

public class UIPlayer extends UIEntity {

	public static enum EAnimation {
		
		ANIM_LEFT("Character1.png", "Character1.png", "Character10.png", "Character10.png"),
		ANIM_RIGHT("Character2.png", "Character2.png", "Character3.png", "Character3.png"),
		ANIM_UP("Character8.png", "Character8.png", "Character9.png", "Character9.png"),
		ANIM_DOWN("Character5.png", "Character5.png", "Character6.png", "Character6.png");
	
		public final String[] sprites;
		
		private EAnimation(String... sprites) {
			this.sprites = sprites;
		}
		
	}
	
	public static enum EPosition {
		
		FRONT("Character4.png"),
		RIGHT("Character2.png"),
		LEFT("Character1.png"),
		BACK("Character7.png");
		
		public final String sprite;

		private EPosition(String sprite) {
			this.sprite = sprite;
		}
		
	}
	
	public static enum EMove {
		
		MOVE_LEFT(EPosition.LEFT, EAnimation.ANIM_LEFT, EPosition.LEFT),
		MOVE_RIGHT(EPosition.RIGHT, EAnimation.ANIM_RIGHT, EPosition.RIGHT),
		MOVE_UP(EPosition.BACK, EAnimation.ANIM_UP, EPosition.BACK),
		MOVE_DOWN(EPosition.FRONT, EAnimation.ANIM_DOWN, EPosition.FRONT);
		
		public final EPosition start;
		public final EAnimation anim;
		public final EPosition end;

		private EMove(EPosition start, EAnimation anim, EPosition end) {
			this.start = start;
			this.anim = anim;
			this.end = end;
		}	
		
		public static EMove getForDirection(EDirection dir) {
			switch(dir) {
			case DOWN: return EMove.MOVE_DOWN;
			case UP: return EMove.MOVE_UP;
			case LEFT: return EMove.MOVE_LEFT;
			case RIGHT: return EMove.MOVE_RIGHT;
			}
			return null;
		}
	}
	
	public static enum EState {
		
		STANDING,
		MOVING
		
	}
	
	public EState state = EState.STANDING;
	
	public EState move = null;
	
	public UIPlayer(Entity entity, SpriteAtlas sprites) {
		super(entity, sprites);
		if (!entity.getType().isPlayer()) throw new RuntimeException("NOT A PLAYER!");	
		currentSprite = EPosition.FRONT.sprite;
	}
	
	@Override
	public void renderEntity(Graphics2D g) {
		super.renderEntity(g);
		/*
		double x = entity.getTileX() * sprites.getTileWidth() + sprites.getTileWidth() / 2 + offsetX;
		double y = entity.getTileY() * sprites.getTileHeight() + sprites.getTileHeight() / 2 + offsetY;
		System.out.println("PLAYER - " + x + " | " + y);
		*/		
	}

}
