package cz.sokoban4j.simulation.board.oop.entities;

import cz.sokoban4j.simulation.board.oop.EEntity;
import cz.sokoban4j.simulation.board.oop.Tile;

public class BoxEntity extends Entity {
	
	public BoxEntity(EEntity type, Tile tile) {
		super(type, tile);
		if (!type.isSomeBox()) throw new RuntimeException("NOT A BOX");		
	}
	
	public boolean inPlace() {
		return tile != null && tile.forBox(type);
	}
	
	@Override
	public Entity clone() {
		return new BoxEntity(type, tile);
	}

}
