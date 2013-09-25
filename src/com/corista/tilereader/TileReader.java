package com.corista.tilereader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

public class TileReader {
	
	private static final String READ_WIDTH_PARAM_NAME = "readWidth";
	private static final String READ_HEIGHT_PARAM_NAME = "readHeight";
	
	private static final String USAGE = "</path/to/slide-data-file> [tile-dimension]";

	private static final int DEFAULT_READ_WIDTH = 256;
	private static final int DEFAULT_READ_HEIGHT = 256;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// make sure we have an argument
		if (args.length < 1 || args.length > 2) {
			System.err.println(USAGE);
			System.exit(1);
		}
		
		// make sure the image file exists
		String imageFilePath = args[0];
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			System.err.println("Image file does not exist.");
			System.exit(1);
		}
		
		ImageReader reader;
		try {
			reader = new BioFormatsReader(imageFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// set the tile dimensions
		int readWidth = DEFAULT_READ_WIDTH;
		int readHeight = DEFAULT_READ_HEIGHT;
		if (args.length > 1) {
			int tileDimension = Integer.parseInt(args[1]);
			readWidth = tileDimension;
			readHeight = tileDimension;
		} else {
			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("tilereader.properties");
			if (stream != null) {
				Properties props = new Properties();
				props.load(stream);
				String readWidthStr = props.getProperty(READ_WIDTH_PARAM_NAME);
				String readHeightStr = props.getProperty(READ_HEIGHT_PARAM_NAME);
				if (readWidthStr != null) {
					readWidth = Integer.parseInt(readWidthStr);
				}
				if (readHeightStr != null) {
					readHeight = Integer.parseInt(readHeightStr);
				}
			}
		}

		// calc x and y tile numbers
		int xTiles = reader.getWidth() / readWidth;
		int yTiles = reader.getHeight() / readHeight;
		int numTiles = xTiles * yTiles;
		
		// create the output directory
		File outputDir= new File(imageFilePath.substring(0, imageFilePath.lastIndexOf('.')) + "_tiles");
		if (!outputDir.mkdir()) {
			System.err.println("Could not create output directory.");
			return;
		}
		
		System.out.println("Proceeding to read " + numTiles + " tiles (" + yTiles + " rows, " + xTiles + " columns) . . .");

		for (int xTile = 0; xTile < xTiles; ++xTile) {
			int x = xTile * readWidth;
			for (int yTile = 0; yTile < yTiles; ++yTile) {
				int y = yTile * readHeight;
				BufferedImage image;
				try {
					image = reader.read(x, y, readWidth, readHeight);
				} catch (Exception e) {
					System.err.println("Caught an exception while trying to read tile (" + xTile + ", " + yTile + "), " + e);
					continue;
				}
				
				// write it to file
				String tileName = String.format("%d_%d.jpg", xTile, yTile);
				ImageIO.write(image, "jpg", new File(outputDir, tileName));
				
				System.out.println("Wrote tile (" + xTile + ", " + yTile + ") to file.");
			}
		}
	}
}
