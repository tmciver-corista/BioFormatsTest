package com.corista.bioformats.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestReader {
	
	private static int READ_DIMENSION = 1024;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// make sure we have an argument
		if (args.length < 1) {
			System.err.println("You must supply an image file path.");
			System.exit(1);
		}
		
		// make sure the image file exists
		String imageFilePath = args[0];
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists()) {
			System.err.println("Image file does not exist.");
			System.exit(1);
		}
		
		// get the image directory (output directory)
		File imageDir = imageFile.getParentFile();
		File outputDir = new File(imageDir, imageFile.getName() + "_" + System.currentTimeMillis());
		
		ImageReader reader;
		try {
			reader = new BioFormatsReader(imageFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// calc x and y tile numbers
		int xTiles = reader.getWidth() / READ_DIMENSION;
		int yTiles = reader.getHeight() / READ_DIMENSION;
		int numTiles = xTiles * yTiles;
		
		// start timer
		long startMillis = System.currentTimeMillis();
		
		BufferedImage image = null;
		File outputFile = null;
		int x, y;
		int tileNum = 1;
		long seconds;
		double minutes;
		for (int xTile = 0; xTile < xTiles; ++xTile) {
			x = xTile * READ_DIMENSION;
			for (int yTile = 0; yTile < yTiles; ++yTile) {
				y = yTile * READ_DIMENSION;
				try {
					image = reader.read(x, y, READ_DIMENSION, READ_DIMENSION);
				} catch (IOException e) {
					System.err.println("Caught an exception while trying to read a tile; continuing.");
					continue;
				}
				
				// write image to file
//				outputFile = new File(outputDir, "tile_" + xTile + "_" + yTile + ".jpg");
//				try {
//					ImageIO.write(image, "jpg", outputFile);
//				} catch (IOException e) {
//					System.err.println("Got an exception while trying to write image to file; continuing.");
//					continue;
//				}
				
				seconds = (System.currentTimeMillis() - startMillis) / 1000;
				minutes = seconds / 60.0;
				double percentComplete = (double)tileNum / numTiles *100;
				System.out.println(String.format("Read tile %d of %d (%.1f%%) in %d seconds (%.1f minutes)", tileNum, numTiles, percentComplete, seconds, minutes));
				tileNum++;
			}
		}
		
		// calc run time
		seconds = (System.currentTimeMillis() - startMillis) / 1000;
		minutes = seconds / 60.0;
		System.out.println("Duration: " + seconds + " seconds (" + minutes + " minutes)");
	}
}
