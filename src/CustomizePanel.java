import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class CustomizePanel extends JPanel{
	Image img;
	CustomizePanel(Image img){
		this.img = img;
	}

	public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(img != null){
            g.drawImage(img, 0, 0, this);
        }
    }
	
	void changeImg(Image img) {
		this.img = img;
	}
	
	void changeSize(int width, int height) {
		this.setSize(width, height);
	}
}
