import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SeamCarver {
	private BufferedImage inputImage;
	private BufferedImage carvedImage;
	private BufferedImage tempCarvedImage;
	private ArrayList<Seam> seams;
	private String imageDir;
	private String imageName;
	
	// constructor - read image and save in image buffer
	SeamCarver(String imagePath, String imageDir, String imageName) {
		this.imageDir = imageDir;
		this.imageName = imageName;

		try {
			this.inputImage = ImageIO.read(new File(imagePath));
			this.carvedImage = ImageIO.read(new File(imagePath));
			this.tempCarvedImage = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			System.out.println("Image file could not be opened!");
		}

		seams = new ArrayList<Seam>();
	}

	// reset carver after creating removal map
	void resetCarver(String imagePath) {
		try {
			this.inputImage = ImageIO.read(new File(imagePath));
			this.carvedImage = ImageIO.read(new File(imagePath));
			this.tempCarvedImage = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			System.out.println("Image file could not be opened!");
		}
		seams = new ArrayList<Seam>();
	}
	
	// make seam arraylist
	void carveImage(int width, int height) {
		// get the number of horizontal and vertical seams that we need to remove
		int numCarveHorizontal = carvedImage.getHeight() - height;
		int numCarveVertical = carvedImage.getWidth() - width;
		int totalCarve = numCarveHorizontal + numCarveVertical;
		boolean isRemove = true;
		
		// if output image is larger than the input 
        if(numCarveHorizontal < 0 || numCarveVertical < 0)
            isRemove = false;

		if (isRemove)
			removeSeam(numCarveHorizontal, numCarveVertical, totalCarve);
		else
			addSeam(numCarveHorizontal, numCarveVertical, totalCarve);
	}
	
	private void removeSeam(int numCarveHorizontal, int numCarveVertical, int totalCarve) {
		// Remove seams until reach the output size
		System.out.println("Removing seam...");
		while (numCarveHorizontal > 0 || numCarveVertical > 0) {
			Seam horizontalSeam;
			Seam verticalSeam;
	
			getProgress(totalCarve, numCarveHorizontal + numCarveVertical);
	
			// get the current energy table
			double[][] energyTable = calculateEnergyTable(carvedImage);
	
			// we can remove either horizontal or vertical seam after comparing their energy values
			if (numCarveHorizontal > 0 && numCarveVertical > 0) {
				horizontalSeam = getHorizontalSeam(energyTable);
				verticalSeam = getVerticalSeam(energyTable);
			
				if (horizontalSeam.getEnergy() < verticalSeam.getEnergy()) {
					seams.add(horizontalSeam);
					removeSeam(horizontalSeam);
					numCarveHorizontal--;
				} else {
					seams.add(verticalSeam);
					removeSeam(verticalSeam);
					numCarveVertical--;
				}
			} else if (numCarveVertical > 0) {
				verticalSeam = getVerticalSeam(energyTable);
				seams.add(verticalSeam);
				removeSeam(verticalSeam);
				numCarveVertical--;
			} else if (numCarveHorizontal > 0) {
				horizontalSeam = getHorizontalSeam(energyTable);
				seams.add(horizontalSeam);
				removeSeam(horizontalSeam);
				numCarveHorizontal--;
			}
		}
	}

	private void addSeam(int numCarveHorizontal, int numCarveVertical, int totalCarve) {
		// Add seams until reach the output size
		System.out.println("Adding seam...");
		while (numCarveHorizontal < 0 || numCarveVertical < 0) {
			Seam horizontalSeam;
			Seam verticalSeam;
	
			// getProgress(totalCarve, numCarveHorizontal + numCarveVertical);
	
			// get the current energy table
			double[][] energyTable = calculateEnergyTable(tempCarvedImage);
	
			// add seam
			if (numCarveVertical < 0) {
				verticalSeam = getVerticalSeam(energyTable);
				addSeam(verticalSeam);
				numCarveVertical++;
			} else {
				horizontalSeam = getHorizontalSeam(energyTable);
				addSeam(horizontalSeam);
				numCarveHorizontal++;
			}
		}
	}
	
	// calculate an energy table
	// get the energy map
	double[][] calculateEnergyTable(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		double[][] energyTable = new double[width][height];

		// loop over each pixel
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double xEnergy, yEnergy, totalEnergy;

				// get next and previous horizontal pixels
				int xPrevRGB = image.getRGB((i - 1 + width) % width, j);
				int xNextRGB = image.getRGB((i + 1 + width) % width, j);

				// calculate the horizontal energy
				xEnergy = getEnergy(xPrevRGB, xNextRGB);

				// get next and previous vertical pixels
				int yPrevRGB = image.getRGB(i, (j - 1 + height) % height);
				int yNextRGB = image.getRGB(i, (j + 1 + height) % height);

				// calculate the vertical energy
				yEnergy = getEnergy(yPrevRGB, yNextRGB);

				// get the total energy
				totalEnergy = xEnergy + yEnergy;
				energyTable[i][j] = totalEnergy;
			}
		}

		return energyTable;
	}

	// calculate the energy
	
	// method of collecting energy
	double getEnergy(int rgb1, int rgb2) {
		// get r, g, b values of rgb1
		double b1 = (rgb1) & 0xff;
		double g1 = (rgb1 >> 8) & 0xff;
		double r1 = (rgb1 >> 16) & 0xff;

		// get r, g, b values of rgb2
		double b2 = (rgb2) & 0xff;
		double g2 = (rgb2 >> 8) & 0xff;
		double r2 = (rgb2 >> 16) & 0xff;

		double energy = (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);

		return energy;
	}

	// find a horizontal seam
	Seam getHorizontalSeam(double[][] energyTable) {
		int width = energyTable.length;
		int height = energyTable[0].length;

		// initialize the seam that will be returned
		Seam seam = new Seam(width, "horizontal");

		// 2d array keeps the dynamic solution
		double[][] horizontalDP = new double[width][height];

		// 2d array for backtracking
		int[][] prev = new int[width][height];

		// loop over all the pixels
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double minValue;

				// base case
				if (i == 0) {
					horizontalDP[i][j] = energyTable[i][j];
					prev[i][j] = -1;
					continue;
				}

				// if on the edge, there are 2 pixels to take the minimum
				else if (j == 0) {
					minValue = Math.min(horizontalDP[i - 1][j], horizontalDP[i - 1][j + 1]);
					if (minValue == horizontalDP[i - 1][j]) {
						prev[i][j] = j;
					} else {
						prev[i][j] = j + 1;
					}
				}
				// if on the edge, there are 2 pixels to take the minimum
				else if (j == height - 1) {
					minValue = Math.min(horizontalDP[i - 1][j], horizontalDP[i - 1][j - 1]);
					if (minValue == horizontalDP[i - 1][j]) {
						prev[i][j] = j;
					} else {
						prev[i][j] = j - 1;
					}
				}
				// otherwise take the minimum of three neighbor pixels
				else {
					minValue = Math.min(horizontalDP[i - 1][j],
							Math.min(horizontalDP[i - 1][j - 1], horizontalDP[i - 1][j + 1]));

					if (minValue == horizontalDP[i - 1][j]) {
						prev[i][j] = j;
					} else if (minValue == horizontalDP[i - 1][j - 1]) {
						prev[i][j] = j - 1;
					} else {
						prev[i][j] = j + 1;
					}

				}

				// add min value to the current energy
				horizontalDP[i][j] = energyTable[i][j] + minValue;
			}
		}

		// find the minimum total energy on the edge and its coordinate
		double minEnergy = horizontalDP[width - 1][0];
		int minCoord = 0;
		for (int j = 0; j < height; j++) {
			if (minEnergy > horizontalDP[width - 1][j]) {
				minEnergy = horizontalDP[width - 1][j];
				minCoord = j;
			}
		}

		seam.setEnergy(minEnergy);

		// backtrack from the minimum, and build the seam
		for (int i = width - 1; i >= 0; i--) {
			seam.setPixels(i, minCoord);
			minCoord = prev[i][minCoord];
		}

		return seam;
	}
	
	// find a vertical seam
	Seam getVerticalSeam(double[][] energyTable) {
		int width = energyTable.length;
		int height = energyTable[0].length;

		// initialize the seam that will be returned
		Seam seam = new Seam(height, "vertical");

		// 2d array keeps the dynamic solution
		double[][] verticalDP = new double[width][height];

		// 2d array for backtracking
		int[][] prev = new int[width][height];

		// loop over all the pixels
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				double minValue;

				// base case
				if (j == 0) {
					verticalDP[i][j] = energyTable[i][j];
					prev[i][j] = -1;
					continue;
				}
				// if on the edge, there are 2 pixels to take the minimum
				else if (i == 0) {
					minValue = Math.min(verticalDP[i][j - 1], verticalDP[i + 1][j - 1]);
					if (minValue == verticalDP[i][j - 1]) {
						prev[i][j] = i;
					} else {
						prev[i][j] = i + 1;
					}
				}
				// if on the edge, there are 2 pixels to take the minimum
				else if (i == width - 1) {
					minValue = Math.min(verticalDP[i][j - 1], verticalDP[i - 1][j - 1]);
					if (minValue == verticalDP[i][j - 1]) {
						prev[i][j] = i;
					} else {
						prev[i][j] = i - 1;
					}
				}
				// otherwise take the minimum of three neighbor pixels
				else {
					minValue = Math.min(verticalDP[i][j - 1], Math.min(verticalDP[i - 1][j - 1], verticalDP[i + 1][j - 1]));

					if (minValue == verticalDP[i][j - 1]) {
						prev[i][j] = i;
					} else if (minValue == verticalDP[i - 1][j - 1]) {
						prev[i][j] = i - 1;
					} else {
						prev[i][j] = i + 1;
					}

				}

				// add min value to the current energy
				verticalDP[i][j] = energyTable[i][j] + minValue;
			}
		}

		// find the minimum total energy on the edge and its coordinate
		double minEnergy = verticalDP[0][height - 1];
		int minCoord = 0;
		for (int i = 0; i < width; i++) {
			if (minEnergy > verticalDP[i][height - 1]) {
				minEnergy = verticalDP[i][height - 1];
				minCoord = i;
			}
		}

		seam.setEnergy(minEnergy);

		// backtrack from the minimum, and build the seam
		for (int j = height - 1; j >= 0; j--) {
			seam.setPixels(j, minCoord);
			minCoord = prev[minCoord][j];
		}

		return seam;
	}

	// removes a seam
	void removeSeam(Seam seam) {
		int width = carvedImage.getWidth();
		int height = carvedImage.getHeight();
		BufferedImage imageNew;

		if (seam.getDirection().equals("horizontal")) {
			// decrement height by 1
			imageNew = new BufferedImage(width, height - 1, BufferedImage.TYPE_INT_RGB);

			// loop over all pixels
			for (int i = 0; i < width; i++) {
				boolean moveToNext = false;
				for (int j = 0; j < height - 1; j++) {
					// once we run into the pixel in the seam
					// skip it and keep copying from the next one
					if (seam.getPixels()[i] == j) {
						moveToNext = true;
					}
					if (moveToNext)
						imageNew.setRGB(i, j, carvedImage.getRGB(i, j + 1));
					else
						imageNew.setRGB(i, j, carvedImage.getRGB(i, j));
				}
			}
		} else {
			// decrement the width by 1
			imageNew = new BufferedImage(width - 1, height, BufferedImage.TYPE_INT_RGB);

			// loop over all pixels
			for (int j = 0; j < height; j++) {
				boolean moveToNext = false;
				for (int i = 0; i < width - 1; i++) {
					// once we run into the pixel in the seam
					// skip it and keep copying from the next one
					if (seam.getPixels()[j] == i) {
						moveToNext = true;
					}
					if (moveToNext) {
						imageNew.setRGB(i, j, carvedImage.getRGB(i + 1, j));
					} else {
						imageNew.setRGB(i, j, carvedImage.getRGB(i, j));
					}
				}
			}
		}

		// update the carved image
		carvedImage = imageNew;
		tempCarvedImage = imageNew;
	}

	// add a seam
	void addSeam(Seam seam) {
		int width = carvedImage.getWidth();
		int height = carvedImage.getHeight();
		BufferedImage imageNew, imageTemp;

		if (seam.getDirection().equals("horizontal")) {
			// increment height by 1
			imageNew = new BufferedImage(width, height + 1, BufferedImage.TYPE_INT_RGB);
			imageTemp = new BufferedImage(width, height + 1, BufferedImage.TYPE_INT_RGB);
					
			// loop over all pixels
			for (int i = 0; i < width; i++) {
				boolean moveToNext = false;
				for (int j = 0; j < height; j++) {
					// once we run into the pixel in the seam
					// skip it and keep copying from the next one
					if (seam.getPixels()[i] == j && j != 0 &&  j != height-1) {
						moveToNext = true;
						int rgb1 = carvedImage.getRGB(i, j - 1);
						int red1 = (rgb1 >> 16) & 0xFF;
						int green1 = (rgb1 >> 8) & 0xFF;
						int blue1 = (rgb1 >> 0) & 0xFF;
						
						int rgb2 = carvedImage.getRGB(i, j + 1);
						int red2 = (rgb2 >> 16) & 0xFF;
						int green2 = (rgb2 >> 8) & 0xFF;
						int blue2 = (rgb2 >> 0) & 0xFF;
						
						int finalColor = 0xFF000000 | ((red1+red2)/2 & 0xff) << 16 | ((green1+green2)/2 & 0xff) << 8 | ((blue1+blue2)/2 & 0xff) << 0;
						imageNew.setRGB(i, j, finalColor);
						imageNew.setRGB(i, j + 1, finalColor);
						
						int temprgb1 = tempCarvedImage.getRGB(i, j - 1);
						int temprgb2 = tempCarvedImage.getRGB(i, j + 1);
						
						imageTemp.setRGB(i, j, (temprgb1+temprgb2)/2);
						imageTemp.setRGB(i, j + 1, (temprgb1+temprgb2)/2);
						continue;
					}
					if (moveToNext) {
						imageNew.setRGB(i, j + 1, carvedImage.getRGB(i, j));
						imageTemp.setRGB(i, j + 1, tempCarvedImage.getRGB(i, j));
					}else {
						imageNew.setRGB(i, j, carvedImage.getRGB(i, j));
						imageTemp.setRGB(i, j, tempCarvedImage.getRGB(i, j));
					}
				}
			}
		} else {
			// increment the width by 1
			imageNew = new BufferedImage(width + 1, height, BufferedImage.TYPE_INT_RGB);
			imageTemp = new BufferedImage(width + 1, height, BufferedImage.TYPE_INT_RGB);
			
			// loop over all pixels
			for (int j = 0; j < height; j++) {
				boolean moveToNext = false;
				for (int i = 0; i < width; i++) {
					// once we run into the pixel in the seam
					// skip it and keep copying from the next one
					if (seam.getPixels()[j] == i && i != 0 && i != width-1) {
						moveToNext = true;
						int rgb1 = carvedImage.getRGB(i - 1, j);
						int red1 = (rgb1 >> 16) & 0xFF;
						int green1 = (rgb1 >> 8) & 0xFF;
						int blue1 = (rgb1 >> 0) & 0xFF;
						
						int rgb2 = carvedImage.getRGB(i + 1, j);
						int red2 = (rgb2 >> 16) & 0xFF;
						int green2 = (rgb2 >> 8) & 0xFF;
						int blue2 = (rgb2 >> 0) & 0xFF;
						int finalColor = 0xFF000000 | ((red1+red2)/2 & 0xff) << 16 | ((green1+green2)/2 & 0xff) << 8 | ((blue1+blue2)/2 & 0xff) << 0;
						
						imageNew.setRGB(i, j, finalColor);
						imageNew.setRGB(i + 1, j, finalColor);
						
						
						int temprgb1 = carvedImage.getRGB(i - 1, j);
						int temprgb2 = carvedImage.getRGB(i + 1, j);
						
						imageTemp.setRGB(i, j, (temprgb1+temprgb2)/2);
						imageTemp.setRGB(i + 1, j, (temprgb1+temprgb2)/2);
						continue;
					}
					if (moveToNext) {
						imageNew.setRGB(i + 1, j, carvedImage.getRGB(i, j));
						imageTemp.setRGB(i + 1, j, tempCarvedImage.getRGB(i, j));
					} else {
						imageNew.setRGB(i, j, carvedImage.getRGB(i, j));
						imageTemp.setRGB(i, j, tempCarvedImage.getRGB(i, j));
					}
				}
			}
		}

		// update the carved image
		carvedImage = imageNew;
		tempCarvedImage = imageTemp;
	}
	
	// Save the carved image
	void saveCarvedImage(String filePath, String type) {
		try {
			File outputFile = new File(filePath);
			ImageIO.write(carvedImage, type, outputFile);
			System.out.println("Carved image has saved completely.");
		} catch (IOException e) {
			System.out.println("Cannot open output file: " + filePath);
		}
	}
	
	// Save the temp carved image
	void saveTempCarvedImage(String filePath, String type) {
		try {
			File outputFile = new File(filePath);
			ImageIO.write(tempCarvedImage, type, outputFile);
			System.out.println("Temp carved image has saved completely.");
		} catch (IOException e) {
			System.out.println("Cannot open output file: " + filePath);
		}
	}

	// Save the energy image in the current path
	boolean saveEnergyTable() {
		BufferedImage image = inputImage;

		// calculate the energy table
		double[][] energyTable = calculateEnergyTable(image);

		// create the energy image
		BufferedImage energyImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

		// find the max value in the energy table
		double maxValue = energyTable[0][0];
		for (int i = 0; i < energyTable.length; i++)
			for (int j = 0; j < energyTable[0].length; j++)
				maxValue = Math.max(maxValue, energyTable[i][j]);

		// loop over each pixel
		for (int i = 0; i < energyImage.getWidth(); i++) {
			for (int j = 0; j < energyImage.getHeight(); j++) {
				// calculate the rgb value (scaled for grayscale)
				int gray = (int) ((energyTable[i][j] / maxValue) * 256);
				int rgb = (gray << 16) + (gray << 8) + gray;
				energyImage.setRGB(i, j, rgb);
			}
		}

		// save the energy image
		try {
			File outputFile = new File(imageDir + imageName + "_ENERGY.jpg");
			ImageIO.write(energyImage, "jpg", outputFile);
			System.out.println("Energy image has been created.");
			return true;
		} catch (IOException e) {
			System.out.println("Cannot create file for energy table.");
			return false;
		}
	}

	// Save the removal image
	boolean saveRemovalTable(String type) {
		int currentWidth = carvedImage.getWidth();
		int currentHeight = carvedImage.getHeight();
		int[][] removalImageArray = new int[inputImage.getWidth()][inputImage.getHeight()];
		int color = 255;
		int range = seams.size() / 255 + 1;

		// loop over the seams
		for (int s = seams.size() - 1; s >= 0; s--) {
			getProgress(seams.size(), s + 1);

			// for each seam
			Seam seam = seams.get(s);
			
			if (type.equals("horizontal")) {
				for (int i = 0; i < currentWidth; i++) {
					int coord = seam.getPixels()[i];
					for (int j = currentHeight; j > coord; j--)
						removalImageArray[i][j] = removalImageArray[i][j - 1];
					removalImageArray[i][coord] = 0xFF000000 | (color & 0xff) << 16 | (color & 0xff) << 8 | (color & 0xff) << 0;
				}
				currentHeight++;
			} else {
				for (int j = 0; j < currentHeight; j++) {
					int coord = seam.getPixels()[j];
					for (int i = currentWidth; i > coord; i--)
						removalImageArray[i][j] = removalImageArray[i - 1][j];
					removalImageArray[coord][j] = 0xFF000000 | (color & 0xff) << 16 | (color & 0xff) << 8 | (color & 0xff) << 0;
				}

				currentWidth++;
			}

			if (s % range == 0)
				color--;
		}
		
		// set color to the removal image
		BufferedImage removalImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < removalImage.getWidth(); i++) {
			for (int j = 0; j < removalImage.getHeight(); j++) {
				removalImage.setRGB(i, j, removalImageArray[i][j]);
			}
		}

		// save the Removal image
		try {
			File outputFile;
			if (type.equals("horizontal")) {
				outputFile = new File(imageDir + imageName + "_HORZONTAL_REMOVAL.jpg");
			}else {
				outputFile = new File(imageDir + imageName + "_VERTICAL_REMOVAL.jpg");
			}
			ImageIO.write(removalImage, "jpg", outputFile);
			System.out.println("Removal image has saved completely.");
			return true;
		} catch (IOException e) {
			System.out.println("Cannot create file for Removal table.");
			return false;
		}
	}

	// save the seam image
	void saveSeamTable() {
		System.out.println("Creating seam map...");

		// 2d array will keep the rgb values for the seam table image
		int[][] seamImageArray = new int[inputImage.getWidth()][inputImage.getHeight()];

		int currentWidth = carvedImage.getWidth();
		int currentHeight = carvedImage.getHeight();

		// copy the carved image to the array
		for (int i = 0; i < currentWidth; i++)
			for (int j = 0; j < currentHeight; j++)
				seamImageArray[i][j] = carvedImage.getRGB(i, j);

		// loop over the seams
		for (int s = seams.size() - 1; s >= 0; s--) {
			getProgress(seams.size(), s + 1);

			// for each seam
			Seam seam = seams.get(s);
			if (seam.getDirection().equals("horizontal")) {
				for (int i = 0; i < currentWidth; i++) {
					int coord = seam.getPixels()[i];
					for (int j = currentHeight; j > coord; j--)
						seamImageArray[i][j] = seamImageArray[i][j - 1];
					seamImageArray[i][coord] = (0xffffff);
				}
				currentHeight++;
			} else {
				for (int j = 0; j < currentHeight; j++) {
					int coord = seam.getPixels()[j];
					for (int i = currentWidth; i > coord; i--)
						seamImageArray[i][j] = seamImageArray[i - 1][j];
					seamImageArray[coord][j] = (0xffffff);
				}

				currentWidth++;
			}

		}

		// create the seam image
		BufferedImage seamImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

		// set rgb of the image using the array
		for (int i = 0; i < seamImage.getWidth(); i++) {
			for (int j = 0; j < seamImage.getHeight(); j++) {
				seamImage.setRGB(i, j, seamImageArray[i][j]);
			}
		}

		// save the seam image
		try {
			File outputFile = new File(imageDir + imageName + "_SEAM.jpg");
			ImageIO.write(seamImage, "jpg", outputFile);
			System.out.println("Seam image has saved completely.");
		} catch (IOException e) {
			System.out.println("Cannot create file for seam table.");
		}
	}

	// progress display
	private void getProgress(int t, int c) {
		double total = t;
		double current = c;

		String message = String.format("%.2f", (total - current) / total * 100);
		System.out.println(message + "%");
	}
}
