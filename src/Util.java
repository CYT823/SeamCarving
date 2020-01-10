import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Util {
	// create a seamCarver object
	SeamCarver setSeamCarver(String filePath, String imageDir, String imageName) {
		SeamCarver carver = new SeamCarver(filePath, imageDir, imageName);
		return carver;
	}
	
	void startCarveImage(SeamCarver carver, int width, int height) {
		carver.carveImage(width, height);
	}
	
	// make energy image; return true = saved already 
	boolean createEnergyImage(SeamCarver carver) {
		return carver.saveEnergyTable();
	}
	
	// make vertical removal image; return true = saved already 
	boolean createVerticalSeamImage(SeamCarver carver, int height) {
		carver.carveImage(1, height);
    	return carver.saveRemovalTable("vertical");
	}
	
	// make horizontal removal image; return true = saved already
	boolean createHorizontalSeamImage(SeamCarver carver, int width) {
		carver.carveImage(width, 1);
    	return carver.saveRemovalTable("horizontal");
	}
	
	// make carved Image(result)
	void createCarvedImage(SeamCarver carver, String filePath, String type) {
		carver.saveCarvedImage(filePath, "jpg");
	}
	
	// make temp carved Image
	void createTempCarvedImage(SeamCarver carver, String filePath, String type) {
		carver.saveTempCarvedImage(filePath, "jpg");
	}
	
	// reset carver object
	void resetCarver(SeamCarver carver, String filePath) {
		carver.resetCarver(filePath); 
	}
	
	// return the image buffer
	Image readImage(String path) {
		Image img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("Cannot read/open the image file!");
			return null;
		}
		return img;
	}
	

}
