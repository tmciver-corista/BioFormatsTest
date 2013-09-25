package com.corista.tilereader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TileReader {
	
	private static final String READ_WIDTH_PARAM_NAME = "readWidth";
	private static final String READ_HEIGHT_PARAM_NAME = "readHeight";
	
	private static final String USAGE = "</path/to/slide-data-file> [read-width] [read-height]";

	private static final int DEFAULT_READ_WIDTH = 256;
	private static final int DEFAULT_READ_HEIGHT = 256;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// make sure we have an argument
		if (args.length < 1) {
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
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("bioformats-test.properties");
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
		
		// override readWidth and readHeight with values given on the command line, if they were given
		if (args.length > 1) {
			readWidth = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			readHeight = Integer.parseInt(args[2]);
		}
		
		System.out.println("read width: " + readWidth + ", read height: " + readHeight);
		
		// calc x and y tile numbers
		int xTiles = reader.getWidth() / readWidth;
		int yTiles = reader.getHeight() / readHeight;
		int numTiles = xTiles * yTiles;
		
		// create the threads
		Thread[] threads = new Thread[] {
				new TileThread(reader, readWidth, readHeight, 0, 0, xTiles / 2, yTiles / 2),
				new TileThread(reader, readWidth, readHeight, xTiles / 2, 0, xTiles / 2, yTiles / 2),
				new TileThread(reader, readWidth, readHeight, 0, yTiles / 2, xTiles / 2, yTiles / 2),
				new TileThread(reader, readWidth, readHeight, xTiles / 2, yTiles / 2, xTiles / 2, yTiles / 2)};
		
		// start the threads
		for (Thread thread : threads) {
			thread.start();
		}
		
		// and wait until they finish
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class TileThread extends Thread {
	
	private ImageReader reader;
	private int tileWidth;
	private int tileHeight;
	private int topLeftTileX;
	private int topLeftTileY;
	private int numTilesX;
	private int numTilesY;

	public TileThread(ImageReader reader, int tileWidth, int tileHeight, int topLeftTileX, int topLeftTileY,
			int numTilesX, int numTilesY) {
		this.reader = reader;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.topLeftTileX = topLeftTileX;
		this.topLeftTileY = topLeftTileY;
		this.numTilesX = numTilesX;
		this.numTilesY = numTilesY;
	}

	@Override
	public void run() {
		
		// start timer
		long startMillis = System.currentTimeMillis();
		
		int tileNum = 1;
		int numTiles = numTilesX * numTilesY;
		for (int xTile = topLeftTileX; xTile < topLeftTileX + numTilesX; ++xTile) {
			int x = xTile * tileWidth;
			for (int yTile = topLeftTileY; yTile < topLeftTileY + numTilesY; ++yTile) {
				int y = yTile * tileHeight;
				try {
					BufferedImage image = reader.read(x, y, tileWidth, tileHeight);
				} catch (Exception e) {
					System.err.println("In thread: " + this.getName() + ", Caught an exception while trying to read tile (" + xTile + ", " + yTile + "), " + e);
					continue;
				}
				tileNum++;
			}
		}
		System.out.println(this + " processed " + tileNum + " tiles.");
	}

	@Override
	public String toString() {
		return "TileThread [topLeftTileX=" + topLeftTileX + ", topLeftTileY="
				+ topLeftTileY + ", numTilesX=" + numTilesX + ", numTilesY="
				+ numTilesY + "]";
	}
}
