package com.corista.bioformats.test;

//import ij.io.OpenDialog;
import loci.plugins.BF;
import ij.ImagePlus;
import loci.formats.FormatException;
import loci.common.Region;
import loci.plugins.in.ImporterOptions;
import java.io.IOException;

public class TestReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*OpenDialog od = new OpenDialog("Open Image File...", arg);
	    String dir = od.getDirectory();
	    String name = od.getFileName();
	    String id = dir + name;*/
		
		int mb = 1024 * 1024;
		System.out.println("Total memory: " + Runtime.getRuntime().totalMemory() / mb + " MB");
		System.out.println("Max memory: " + Runtime.getRuntime().maxMemory() / mb + " MB");
		
		String imageFile = "/Users/tmciver/Documents/corista-images/Unsupported/Olympus/GI test01.vsi";
		
		// create a crop region
		Region cropRegion = new Region(5757, 13076, 1000, 1000);
		
		// set up ImporterOptions
		ImporterOptions importOptions = null;
		try {
			importOptions = new ImporterOptions();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		importOptions.setId(imageFile);
		importOptions.setCropRegion(0, cropRegion);
		
		ImagePlus[] imps = null;
		try {
			imps = BF.openImagePlus(importOptions);
		} catch (FormatException fe) {
			System.err.println("Caught FormatException");
		} catch (IOException ioe) {
			System.err.println("Caught IOException");
		}
		
		for	(ImagePlus imp : imps) {
			imp.show();
		}
	}
}
