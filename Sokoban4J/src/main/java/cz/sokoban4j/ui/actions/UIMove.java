package cz.sokoban4j.ui.actions;

import cz.sokoban4j.simulation.actions.EDirection;
import cz.sokoban4j.simulation.board.oop.Board;
import cz.sokoban4j.ui.UIBoard;
import cz.sokoban4j.ui.atlas.SpriteAtlas;
import cz.sokoban4j.ui.entities.UIPlayer;
import cz.sokoban4j.ui.entities.UIPlayer.EMove;
import cz.sokoban4j.ui.utils.TimeDelta;

public class UIMove implements IUIAction {

	private Board board;
	private UIBoard uiBoard;
	private SpriteAtlas sprites;
	
	private UIPlayer player;
	
	private EMove move;
	
	private EDirection dir;
	
	private int animFrame = -1;
	
	private double nextAnim;
	
	private double moveMillis;
	
	private int animFrameCount;
	
	private double oneFrameMillis;
	
	private double moveSpeedX;
	
	private double moveSpeedY;
	
	private double offsetTargetX;
	
	private double offsetTargetY;
	
	public UIMove(Board board, UIBoard uiBoard, SpriteAtlas sprites, EDirection dir, double moveMillis, int animFrameCount) {
		this.board = board;
		this.uiBoard = uiBoard;
		this.sprites = sprites;
		this.dir = dir;
		this.moveMillis = moveMillis;
		this.animFrameCount = animFrameCount;
		this.oneFrameMillis = moveMillis / ((double)animFrameCount);
		this.moveSpeedX = dir.dX * ((double)sprites.getTileWidth()) / moveMillis;
		this.moveSpeedY = dir.dY * ((double)sprites.getTileHeight()) / moveMillis;
		this.offsetTargetX = dir.dX * sprites.getTileWidth();
		this.offsetTargetY = dir.dY * sprites.getTileHeight();
	}
	
	public void start() {
		player = uiBoard.player;
		move = EMove.getForDirection(dir);
		nextAnim = oneFrameMillis;
		animFrame = 0;
		nextAnim = 0;
		nextAnim();
	}
	
	@Override
	public void tick(TimeDelta time) {
		if (animFrame == -1) {
			start();
			move(time);
			return;
		}
		
		nextAnim -= time.deltaMillis();
		if (nextAnim < 0) nextAnim();
		move(time);
	}

	@Override
	public boolean isFinished() {
		if (player == null) return false;
		double dX = Math.abs(offsetTargetX) - Math.abs(player.offsetX);
		double dY = Math.abs(offsetTargetY) - Math.abs(player.offsetY);
		return dX <= 0.01 && dY <= 0.01;
	}
	
	private void move(TimeDelta time) {
		if (player != null) {
			player.offsetX += ((double)time.deltaMillis()) * moveSpeedX;
			player.offsetY += ((double)time.deltaMillis()) * moveSpeedY;
		}
	}
	
	private void nextAnim() {
		player.currentSprite = move.anim.sprites[animFrame % move.anim.sprites.length];
		nextAnim += oneFrameMillis;
		animFrame += 1;
	}

	@Override
	public void finish() {
		if (player != null) {
			player.offsetX = 0;
			player.offsetY = 0;
		}		
	}

}
