import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame{
	GUI gui;
	JButton chooseFileBtn;
	JLabel fileName;
	JTextField newWidth;
	JTextField newHeight;
	CustomizePanel displayPanel;
	CustomizePanel seamDisplayPanel;
	Util util;
	SeamCarver carver;
	int newImageWidth, newImageHeight;
	int inputImageWidth, inputImageHeight;
	String imageDir = "./image/";
	String imageName = "";
	boolean isSeam;
	
	// set the UI component
	GUI() {
		gui = this;
		this.setLayout(null);
		util = new Util();

		// choose file button
		chooseFileBtn = new JButton("Choose");
		chooseFileBtn.setBounds(50, 50, 100, 30);
		this.add(chooseFileBtn);

		// file name display
		JLabel fileNameLabel = new JLabel("File Name :");
		fileNameLabel.setBounds(170, 50, 70, 30);
		this.add(fileNameLabel);

		fileName = new JLabel("(fileName)");
		fileName.setEnabled(false);
		fileName.setBounds(250, 50, 100, 30);
		this.add(fileName);

		// new image width
		JLabel newImageWidthLabel = new JLabel("New Width: ");
		newImageWidthLabel.setBounds(50, 100, 100, 30);
		this.add(newImageWidthLabel);

		newWidth = new JTextField("0");
		newWidth.setBounds(150, 100, 50, 30);
		this.add(newWidth);

		JLabel newWidthPixelLabel = new JLabel("pixel");
		newWidthPixelLabel.setBounds(210, 100, 100, 30);
		this.add(newWidthPixelLabel);

		// new image height
		JLabel newImageHeightLabel = new JLabel("New Height: ");
		newImageHeightLabel.setBounds(50, 130, 100, 30);
		this.add(newImageHeightLabel);

		newHeight = new JTextField("0");
		newHeight.setBounds(150, 130, 50, 30);
		this.add(newHeight);

		JLabel newHeightPixelLabel = new JLabel("pixel");
		newHeightPixelLabel.setBounds(210, 130, 100, 30);
		this.add(newHeightPixelLabel);

		displayPanel = new CustomizePanel(null);
		displayPanel.setBounds(50, 180, 500, 500);
		this.add(displayPanel);
		
		seamDisplayPanel = new CustomizePanel(null);
		seamDisplayPanel.setBounds(530, 180, 400, 400);
		this.add(seamDisplayPanel);
		
		init();
	}

	void init() {
		// choose file event
		chooseFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// implement file chooser
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("./image")); // set the default directory
				fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter()); // remove the all file option
				fileChooser.addChoosableFileFilter( new FileNameExtensionFilter("Images (*.jpg *.png *.gif *.bmp)", "jpg", "png", "gif", "bmp"));
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					// put file name on UI & update imageName
					File selectedFile = fileChooser.getSelectedFile();
					fileName.setText(selectedFile.getName());
					imageName = fileName.getText().split("\\.")[0];
					
					// create seam carver object
					String filePath = selectedFile.getPath();
					
					// read the image on displayPanel
					Image img = util.readImage(filePath);
					displayPanel.changeImg(img);
					gui.repaint();
				
					// update the width and height
					newImageWidth = ((BufferedImage)img).getWidth();
					newImageHeight = ((BufferedImage)img).getHeight();
					newWidth.setText(String.valueOf(newImageWidth));
					newHeight.setText(String.valueOf(newImageHeight));
					inputImageWidth = newImageWidth;
					inputImageHeight = newImageHeight;
					
					// create carver object
					carver = util.setSeamCarver(filePath, imageDir, imageName);

					/*
					// make an energy image & put it on UI
					if (util.createEnergyImage(carver)) {
						ImageDisplay energyImage = new ImageDisplay("Energy Image",
								imageDir + imageName + "_ENERGY.jpg");
						energyImage.setSize(newImageWidth, newImageHeight);
						energyImage.setLocation(0, newImageHeight);
						energyImage.setVisible(true);
					}

					// make an removal image & put it on UI
					if (util.createVerticalSeamImage(carver, newImageHeight)) {
						ImageDisplay removalImage = new ImageDisplay("Vertical Removal Image",
								imageDir + imageName + "_VERTICAL_REMOVAL.jpg");
						removalImage.setSize(newImageWidth, newImageHeight);
						removalImage.setLocation(0, 0);
						removalImage.setVisible(true);
					}
					
					// reset carver
					util.resetCarver(carver, filePath);

					// make an removal image & put it on UI
					if (util.createHorizontalSeamImage(carver, newImageWidth)) {
						ImageDisplay removalImage = new ImageDisplay("Horizontal Removal Image",
								imageDir + imageName + "_HORZONTAL_REMOVAL.jpg");
						removalImage.setSize(newImageWidth, newImageHeight);
						removalImage.setLocation(newImageWidth, 0);
						removalImage.setVisible(true);
					}
					
					// reset carver
					util.resetCarver(carver, filePath);
					*/
				}
				chooseFileBtn.setFocusable(false);
				newWidth.setFocusable(false);
				newHeight.setFocusable(false);
				gui.setVisible(true);
				displayPanel.setFocusable(true);
				isSeam = true;
			}
		});

		// key listener for Left、Right、Up、Down
		displayPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(imageName.equals(""))
					return;
				if(e.getKeyCode() == KeyEvent.VK_UP){
					newImageHeight ++;
		        	newHeight.setText(String.valueOf(newImageHeight));
		        	util.startCarveImage(carver, newImageWidth, newImageHeight);
		        	util.createCarvedImage(carver, imageDir + imageName + "_RESULT.jpg", "jpg");
		        	util.createTempCarvedImage(carver, imageDir + imageName + "_TEMP_RESULT.jpg", "jpg");
		        	
		        	// repaint on display panel
		        	Image img = util.readImage(imageDir + imageName + "_RESULT.jpg");
		        	displayPanel.changeSize(newImageWidth, newImageHeight);
					displayPanel.changeImg(img);
					
					isSeam = false;
					seamDisplayPanel.setVisible(false);
					gui.repaint();
		        }else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
		        	if(newImageHeight == 2){
						JOptionPane.showMessageDialog(gui, "請放大");
						return;
					}
		        	newImageHeight --;
		        	newHeight.setText(String.valueOf(newImageHeight));
		        	util.startCarveImage(carver, newImageWidth, newImageHeight);
		        	util.createCarvedImage(carver, imageDir + imageName + "_RESULT.jpg", "jpg");
		        	
		        	// repaint on display panel
		        	Image img = util.readImage(imageDir + imageName + "_RESULT.jpg");
		        	displayPanel.changeSize(newImageWidth, newImageHeight);
					displayPanel.changeImg(img);
					
					if(isSeam) {
						carver.saveSeamTable();
						img = util.readImage(imageDir + imageName + "_SEAM.jpg");
						seamDisplayPanel.changeSize(inputImageWidth, inputImageHeight);
						seamDisplayPanel.setLocation(inputImageWidth + 100, 180);
						seamDisplayPanel.changeImg(img);
						seamDisplayPanel.setVisible(true);
					}
					gui.repaint();
		        }else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
		        	if(newImageWidth == 2){
						JOptionPane.showMessageDialog(gui, "請放大");
						return;
					}
		        	newImageWidth --;
		        	newWidth.setText(String.valueOf(newImageWidth));
		        	util.startCarveImage(carver, newImageWidth, newImageHeight);
		        	util.createCarvedImage(carver, imageDir + imageName + "_RESULT.jpg", "jpg");
		        	
		        	// repaint on display panel
		        	Image img = util.readImage(imageDir + imageName + "_RESULT.jpg");
		        	displayPanel.changeSize(newImageWidth, newImageHeight);
					displayPanel.changeImg(img);
					
					if(isSeam) {
						carver.saveSeamTable();
						img = util.readImage(imageDir + imageName + "_SEAM.jpg");
						seamDisplayPanel.changeSize(inputImageWidth, inputImageHeight);
						seamDisplayPanel.setLocation(inputImageWidth + 100, 180);
						seamDisplayPanel.changeImg(img);
						seamDisplayPanel.setVisible(true);
					}
					gui.repaint();
		        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
		        	newImageWidth ++;
		        	newWidth.setText(String.valueOf(newImageWidth));
		        	util.startCarveImage(carver, newImageWidth, newImageHeight);
		        	util.createCarvedImage(carver, imageDir + imageName + "_RESULT.jpg", "jpg");
		        	util.createTempCarvedImage(carver, imageDir + imageName + "_TEMP_RESULT.jpg", "jpg");
		        	
		        	// repaint on display panel
		        	Image img = util.readImage(imageDir + imageName + "_RESULT.jpg");
		        	displayPanel.changeSize(newImageWidth, newImageHeight);
					displayPanel.changeImg(img);
					
					isSeam = false;
					seamDisplayPanel.setVisible(false);
					gui.repaint();
		        }else {
		        	System.out.println("NONE");
		        }
			}
		});
		
		// input height 
		newHeight.addKeyListener(new KeyAdapter() {
			// only number
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyCode() == KeyEvent.VK_BACK_SPACE
						|| e.getKeyCode() == KeyEvent.VK_ENTER) {
					newHeight.setEditable(true);
				} else {
					newHeight.setEditable(false);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(imageName.equals(""))
						return;
					if(Integer.parseInt(newWidth.getText()) < 2 ||Integer.parseInt(newHeight.getText()) < 2){
						JOptionPane.showMessageDialog(gui, "請輸入>1的數字");
						return;
					}
					newImageWidth = Integer.parseInt(newWidth.getText());
					newImageHeight = Integer.parseInt(newHeight.getText());
		        	util.startCarveImage(carver, newImageWidth, newImageHeight);
		        	util.createCarvedImage(carver, imageDir + imageName + "_RESULT.jpg", "jpg");
		        	util.createTempCarvedImage(carver, imageDir + imageName + "_TEMP_RESULT.jpg", "jpg");
		        	
		        	// repaint on display panel
		        	Image img = util.readImage(imageDir + imageName + "_RESULT.jpg");
		        	displayPanel.changeSize(newImageWidth, newImageHeight);
					displayPanel.changeImg(img);
					
					isSeam = false;
					seamDisplayPanel.setVisible(false);
					gui.repaint();
					
					newWidth.setFocusable(false);
					newHeight.setFocusable(false);
					displayPanel.setFocusable(true);
				}
			}
		});
		
		// input width 
		newWidth.addKeyListener(new KeyAdapter() {
			// only number
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
					newWidth.setEditable(true);
				} else {
					newWidth.setEditable(false);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(imageName.equals(""))
						return;
					if(Integer.parseInt(newWidth.getText()) < 2 ||Integer.parseInt(newHeight.getText()) < 2){
						JOptionPane.showMessageDialog(gui, "請輸入>1的數字");
						return;
					}
					newImageWidth = Integer.parseInt(newWidth.getText());
					newImageHeight = Integer.parseInt(newHeight.getText());
		        	util.startCarveImage(carver, newImageWidth, newImageHeight);
		        	util.createCarvedImage(carver, imageDir + imageName + "_RESULT.jpg", "jpg");
		        	util.createTempCarvedImage(carver, imageDir + imageName + "_TEMP_RESULT.jpg", "jpg");
		        	
		        	// repaint on display panel
		        	Image img = util.readImage(imageDir + imageName + "_RESULT.jpg");
		        	displayPanel.changeSize(newImageWidth, newImageHeight);
					displayPanel.changeImg(img);
					
					isSeam = false;
					seamDisplayPanel.setVisible(false);
					gui.repaint();
					
					newWidth.setFocusable(false);
					newHeight.setFocusable(false);
					displayPanel.setFocusable(true);
				}
			}
		});
		
		// awaken newHeight text field
		newHeight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				newHeight.setFocusable(true);
			}
		});
		
		// awaken newWidth text field
		newWidth.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				newWidth.setFocusable(true);
			}
		});
		
		
	}
}