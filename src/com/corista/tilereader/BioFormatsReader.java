package com.corista.tilereader;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.gui.BufferedImageReader;

public class BioFormatsReader implements ImageReader {
	
	private BufferedImageReader imageReader;
	private int imageNumber;
	private int width;
	private int height;

	public BioFormatsReader(String filename, int seriesNumber, int imageNumber) throws IOException {

		loci.formats.IFormatReader formatReader = new loci.formats.ImageReader();
	    
	    // set the ID (image file name)
	    try {
	    	formatReader.setId(filename);
		} catch (FormatException e) {
			formatReader.close();
			throw new IOException(e);
		}
	    
	    imageReader = new BufferedImageReader(formatReader);
	    
	    // set the series number
	    imageReader.setSeries(seriesNumber);

	    // get the width and height
	    width = imageReader.getSizeX();
	    height = imageReader.getSizeY();
	    
	    imageReader.close();
	}

	@Override
	public BufferedImage read(int x, int y, int width, int height) throws IOException {

		// get the image
		BufferedImage image;
		try {
			image = imageReader.openImage(imageNumber, x, y, width, height);
		} catch (FormatException e) {
			throw new IOException(e);
		}
		
		return image;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
}
