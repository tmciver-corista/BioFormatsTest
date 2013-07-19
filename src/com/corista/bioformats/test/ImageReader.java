package com.corista.bioformats.test;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageReader {
	BufferedImage read(int x, int y, int width, int height) throws IOException;
	int getWidth();
	int getHeight();
}
