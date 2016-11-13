package cz.sokoban4j.ui.atlas;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import cz.sokoban4j.ui.atlas.TextureAtlas.SubTexture;

public class SpriteAtlas {
	
	private static final String RESOURCE_PREFIX = "cz/sokoban4j/ui/atlas/resources";
	
	private Map<String, BufferedImage> sprites = new HashMap<String, BufferedImage>();
	
	public SpriteAtlas() {		
	}
	
	public void load() {
		InputStream xmlStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_PREFIX + "/sprites.xml");
		TextureAtlas atlas = TextureAtlas.loadXML(xmlStream);

		// LOAD SHEET
		BufferedImage sheet;
		try {
		    sheet = ImageIO.read(getClass().getClassLoader().getResourceAsStream(RESOURCE_PREFIX + "/" + atlas.imagePath));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read: " + atlas.imagePath);
		}
		
		for (SubTexture subTexture : atlas.subTextures) {
			BufferedImage image = sheet.getSubimage(subTexture.x, subTexture.y, subTexture.width, subTexture.height);
			sprites.put(subTexture.name, image);
		}
		
		// ALL LOADED...
	}
	
	public BufferedImage getSprite(String key) {
		if (key == null) return null;
		BufferedImage result = sprites.get(key);
		if (result == null) throw new RuntimeException("Failed to get sprite '" + key + "'.");
		return result;
	}
	
	public int getTileWidth() {
		return 64;
	}
	
	public int getTileHeight() {
		return 64;
	}

}
