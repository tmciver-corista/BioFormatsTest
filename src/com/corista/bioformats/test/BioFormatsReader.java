package com.corista.bioformats.test;

import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

public class BioFormatsReader implements ImageReader {

	private ImporterOptions options;
	private int width;
	private int height;

	public BioFormatsReader(String filename) throws IOException {
		
		// set up options
		options = new ImporterOptions();
		options.setId(filename);
		options.setCrop(true);
		
		// get the width and height
		// create format reader
	    IFormatReader reader = new loci.formats.ImageReader();
	    
	    // set the ID (image file name)
	    try {
			reader.setId(filename);
		} catch (FormatException e) {
			reader.close();
			throw new IOException(e);
		}

	    // get the width and height
	    width = reader.getSizeX();
	    height = reader.getSizeY();
	    
	    reader.close();
	}

	@Override
	public BufferedImage read(int x, int y, int width, int height) throws IOException {

		// set a crop region
		options.setCropRegion(0, new Region(x, y, width, height));
		
		ImagePlus[] imps = null;
		try {
			imps = BF.openImagePlus(options);
		} catch (FormatException fe) {
			System.err.println("Caught FormatException");
			throw new IOException(fe);
		} catch (IOException ioe) {
			System.err.println("Caught IOException");
			throw ioe;
		}
		
		if (imps.length < 1) {
			throw new IOException("Could not read image data.");
		}
		
		// get the image
		BufferedImage image = (BufferedImage)imps[0].getImage();
		
		return image;
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
