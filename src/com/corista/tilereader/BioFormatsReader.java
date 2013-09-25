package com.corista.tilereader;

import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.common.Region;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;

public class BioFormatsReader implements ImageReader {

	private ImporterOptions options;
	
	private ImagePlusReader reader;
	private int width;
	private int height;

	public BioFormatsReader(String filename) throws IOException {
		
		// set up options
		options = new ImporterOptions();
		options.setId(filename);
		options.setCrop(true);
		
		ImportProcess process = new ImportProcess(options);
		try {
			if (!process.execute()) {
				throw new IOException("Error executing ImportProcess.");
			}
		} catch (FormatException e1) {
			throw new IOException("Error executing ImportProcess.");
		}
		
		// get the width and height
		// create format reader
	    reader = new ImagePlusReader(process);
	    
	    // use an IFormatReader to get width and height
	    IFormatReader formatReader = new loci.formats.ImageReader();
	    
	    // set the ID (image file name)
	    try {
			formatReader.setId(filename);
		} catch (FormatException e) {
			formatReader.close();
			throw new IOException(e);
		}

	    // get the width and height
	    width = formatReader.getSizeX();
	    height = formatReader.getSizeY();
	    
	    formatReader.close();
	}

	@Override
	public BufferedImage read(int x, int y, int width, int height) throws IOException {

		// set a crop region
		options.setCropRegion(0, new Region(x, y, width, height));
		
		ImagePlus[] imps = null;
		try {
			imps = reader.openImagePlus();
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
