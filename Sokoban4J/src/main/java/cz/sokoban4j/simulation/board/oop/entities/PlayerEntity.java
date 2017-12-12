package cz.sokoban4j.simulation.board.oop.entities;

import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.Tile;

public class PlayerEntity extends Entity {
	
	public PlayerEntity(EEntity type, Tile tile) {
		super(type, tile);
		if (!type.isPlayer()) throw new RuntimeException("NOT A PLAYER");		
	}
	
	@Override
	public PlayerEntity clone() {
		return new PlayerEntity(type, tile);
	}

}
