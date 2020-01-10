import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

class ImageDisplay extends JFrame{
	private String title;
	private String imagePath;
	private CustomizePanel displayPanel;
	Image img;
	Util util;
	
	ImageDisplay(String title, String imagePath){
		this.title = title;
		this.imagePath = imagePath;
		
		util = new Util();
		img = util.readImage(imagePath);
		
		this.setTitle(title);
		
		displayPanel = new CustomizePanel(img);
		this.add(displayPanel);
	}
}

