package cz.sokoban4j.ui;

import java.awt.geom.Rectangle2D;

public class UICamera {
	
	public int x = 0;
	public int y = 0;
	
	public int viewportWidth = 640;
	public int viewportHeight = 480;

	
	public int cameraX(int localX) {
		return x + localX;
	}
	
	public int cameraY(int localY) {
		return y + localY;
	}
	
	public boolean inViewportX(int localX) {
		return localX >= 0 && localX < viewportWidth;
	}
	
	public boolean inViewportY(int localY) {
		return localY >= 0 && localY < viewportWidth;
	}
	
	public boolean inViewport(int localX, int localY) {
		return inViewportX(localX) && inViewportY(localY);
	}
	
}
