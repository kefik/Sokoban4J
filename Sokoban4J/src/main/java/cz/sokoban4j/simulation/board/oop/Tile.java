package cz.sokoban4j.simulation.board.oop;

import cz.sokoban4j.simulation.board.oop.entities.Entity;
import cz.sokoban4j.simulation.board.oop.entities.EntityFactory;

public class Tile implements Cloneable {

	public ESpace space;
	public EPlace place;
	public Entity entity;
	
	public int tileX;
	public int tileY;	
	
	public Tile() {
	}

	public Tile(int x, int y, ESpace space, EPlace place, EEntity entity) {
		super();
		this.tileX = x;
		this.tileY = y;
		this.entity = EntityFactory.createEntity(entity, this);
		this.place = place;
		this.space = space;
	}
	
	/**
	 * Deep-copy, clones Entity as well
	 */
	@Override
	public Tile clone() {
		Tile result = new Tile(tileX, tileY, space, place, entity == null ? EEntity.NONE : entity.getType());
		return result;
	}
	
	public boolean isFree() {
		return space.isWalkable() && (entity == null || entity.getType() == EEntity.NONE);
	}
	
	public boolean isWalkable() {
		return isFree() || isPlayer();
	}
	
	public boolean isWall() {
		return space == ESpace.WALL;
	}
	
	public boolean isPlayer() {
		return entity != null && entity.getType().isPlayer();
	}
	
	public boolean isSomeBox() {
		return entity != null && entity.getType().isSomeBox();
	}
	
	public boolean isBox(EEntity box) {
		if (!box.isSomeBox()) return false;
		return entity != null && entity.getType() == box;
	}
	
	public boolean isBox(int boxNum) {
		return entity != null && entity.getType().isBox(boxNum);
	}
	
	public boolean forSomeBox() {
		return place.forSomeBox();
	}
	
	public boolean forAnyBox() {
		return place.forAnyBox();
	}
	
	public boolean forBox(EEntity box) {
		return place.forBox(box);
	}
	
	public boolean forBox(int boxNum) {
		return place.forBox(boxNum);
	}
		
	/**
	 * Returns integer representation of the tile.
	 * @return
	 */
	public int computeTileFlag() {
		return space.getFlag() | (entity == null ? EEntity.NONE.getFlag() : entity.getType().getFlag()) | place.getFlag();
	}
	
	/**
	 * Expensive! Creates a new {@link Tile} based on given 'tileFlag'. 
	 * tileFlag can be computed via {@link #computeTileFlag()}.
	 * @return
	 */
	public static Tile fromFlag(int tileX, int tileY, int tileFlag) {
		return new Tile(tileX, tileY, ESpace.fromFlag(tileFlag), EPlace.fromFlag(tileFlag), EEntity.fromFlag(tileFlag));
	}
	
}
