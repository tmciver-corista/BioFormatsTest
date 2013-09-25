/**
 * 
 */
package com.corista.tilereader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author tmciver
 *
 */
public class TileReader {
	
	private ImageReader imageReader;
	private int tileWidth, tileHeight;
	private int xTiles, yTiles;
	
	public TileReader(ImageReader imageReader, int tileDimension) {
		this(imageReader, tileDimension, tileDimension);
	}
	
	/**
	 * @param imageReader
	 * @param tileWidth
	 * @param tileHeight
	 */
	public TileReader(ImageReader imageReader, int tileWidth, int tileHeight) {
		this.imageReader = imageReader;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		xTiles = (int)Math.ceil((double)imageReader.getWidth() / tileWidth);
		yTiles = (int)Math.ceil((double)imageReader.getHeight() / tileHeight);
	}
	
	public BufferedImage read(int tileX, int tileY) throws IOException {
		
		// validate that they're within the image
		if (tileX >= xTiles || tileY >= yTiles) {
			throw new IllegalArgumentException("One or more of the read coordinates are out of the image bounds.");
		}
		
		// calc the x and y pixel coordinates
		int x = tileX * tileWidth;
		int y = tileY * tileHeight;

		// calc read height and width as they are not equal to tile height and
		// width at the right and bottom edges of the slide image
		int readWidth = tileWidth;
		int readHeight = tileHeight;
		int widthToGo = imageReader.getWidth() - x;
		int heightToGo = imageReader.getHeight() - y;
		if (widthToGo < tileWidth) {
			readWidth = widthToGo;
		}
		if (heightToGo < tileHeight) {
			readHeight = heightToGo;
		}
		
		// read the image
		BufferedImage image = imageReader.read(x, y, readWidth, readHeight);
		
		// if the image does not have the correct dimensions, write it into another
		// image that does
		if (image.getWidth() != tileWidth ||
				image.getHeight() != tileHeight) {
			BufferedImage smallerImage = image;
			image = new BufferedImage(tileWidth, tileHeight, smallerImage.getType());
			Graphics2D g = image.createGraphics();
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, image.getWidth(), image.getHeight());
			g.drawImage(smallerImage, 0, 0, null);
			g.dispose();
		}
		
		return image;
	}

	public int getNumXTiles() {
		return xTiles;
	}

	public int getNumYTiles() {
		return yTiles;
	}

}
