/*
Name:        Yu Chun Wah
Student ID:  52633870
Lab Section: Tuesday

Part A: Random Pixel Movement
Part B: Convert image to ASCII image

Warning!!
This is the main core processor for the image processing application!!!
Edit with CARE!!!!
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.lang.Math;

public class ImageProcessor {
	// The BufferedImage class describes an Image with an accessible buffer of
	// image data
	public static BufferedImage convert(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(img, 0, 0, null);
		bg.dispose();
		return bi;
	}

	public static BufferedImage cloneImage(BufferedImage img) {
		BufferedImage resultImg = new BufferedImage(img.getWidth(),
				img.getHeight(), BufferedImage.TYPE_INT_RGB);
		WritableRaster WR1 = Raster.createWritableRaster(img.getSampleModel(),
				null);
		WritableRaster WR2 = img.copyData(WR1);
		resultImg.setData(WR2);
		return resultImg;
	}

	// TODO: finish the blurring effect
	public static BufferedImage bokehBlurring(Image img) {
		// GaussianFilter
//		 Blurring blr = new Blurring(5);
//		 BufferedImage bufImg = blr.gaussianBlur(img);

		// Lens Blur
		LensBlurFilter lensBlur = new LensBlurFilter();
		BufferedImage inImg = convert(img);
		BufferedImage bufImg = new BufferedImage(inImg.getWidth(),
				inImg.getHeight(), BufferedImage.TYPE_INT_RGB);
		lensBlur.setSides(20);
		lensBlur.setBloom(1);
		lensBlur.setRadius((float) (0.01*inImg.getHeight()));
		lensBlur.setBloomThreshold(210);
		lensBlur.filter(inImg, bufImg);

		// return back the resulting image in BufferedImage type
		return bufImg;
	}

	// A method to convert color image to grayscale image
	public static BufferedImage toGrayScale(Image img)// <=====GRAY SCALE!!!!!
	{
		// Convert image from type Image to BufferedImage
		BufferedImage bufImg = convert(img);

		// Scan through each row of the image
		for (int j = 0; j < bufImg.getHeight(); j++) {
			// Scan through each columns of the image
			for (int i = 0; i < bufImg.getWidth(); i++) {
				// Returns an integer pixel in the default RGB color model
				int values = bufImg.getRGB(i, j);
				// Convert the single integer pixel value to RGB color
				Color oldColor = new Color(values);

				int red = oldColor.getRed(); // get red value
				int green = oldColor.getGreen(); // get green value
				int blue = oldColor.getBlue(); // get blue value

				// Convert RGB to grayscale using formula
				// gray = 0.299 * R + 0.587 * G + 0.114 * B
				double grayVal = 0.299 * red + 0.587 * green + 0.114 * blue;

				// Assign each channel of RGB with the same value
				Color newColor = new Color((int) grayVal, (int) grayVal,
						(int) grayVal);

				// Get back the integer representation of RGB color
				// and assign it back to the original position
				bufImg.setRGB(i, j, newColor.getRGB());
			}
		}
		// return back the resulting image in BufferedImage type
		return bufImg;
	}

	public static BufferedImage invertImage(Image img) {
		BufferedImage bufImg = convert(img);

		for (int j = 0; j < bufImg.getHeight(); j++) {
			for (int i = 0; i < bufImg.getWidth(); i++) {
				int values = bufImg.getRGB(i, j);
				Color oldColor = new Color(values);

				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue();

				Color newColor = new Color(255 - red, 255 - green, 255 - blue);

				bufImg.setRGB(i, j, newColor.getRGB());
			}
		}
		return bufImg;
	}

	public static BufferedImage brighteningImage(Image img, int nBrightness) {
		BufferedImage bufImg = convert(img);

		for (int j = 0; j < bufImg.getHeight(); j++) {
			for (int i = 0; i < bufImg.getWidth(); i++) {
				int values = bufImg.getRGB(i, j);
				Color oldColor = new Color(values);

				int red = oldColor.getRed();
				int green = oldColor.getGreen();
				int blue = oldColor.getBlue();

				int newRed = (red + nBrightness > 255) ? 255 : red
						+ nBrightness;
				int newGreen = (green + nBrightness > 255) ? 255 : green
						+ nBrightness;
				int newBlue = (blue + nBrightness > 255) ? 255 : blue
						+ nBrightness;

				newRed = (newRed < 0) ? 0 : newRed;
				newGreen = (newGreen < 0) ? 0 : newGreen;
				newBlue = (newBlue < 0) ? 0 : newBlue;

				Color newColor = new Color(newRed, newGreen, newBlue);

				bufImg.setRGB(i, j, newColor.getRGB());
			}
		}
		return bufImg;
	}

	public static BufferedImage offsetFilter(Image img, Point[][] offset) {
		// Temporary! This is only for us to make the program runnable
		return null;
	}

	public static BufferedImage offsetFilterAbs(Image img, Point[][] offset) {
		// Temporary! This is only for us to make the program runnable
		return null;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Start of Part
	// A~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! Grade this method please.
	// :) ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !
	// Done and Optimized~~~~~~
	public static BufferedImage randomPixelMovement(Image img, int nDegree) {
		// Open a new image for playing graphic magic~ :)
		BufferedImage bufImg = convert(img);
		BufferedImage resultImg = new BufferedImage(bufImg.getWidth(),
				bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

		Random randomGem = new Random();

		// Scanning and making magic one by one, pixel by pixel~
		int xnew, ynew;
		for (int j = 0; j < bufImg.getHeight(); j++) {
			for (int i = 0; i < bufImg.getWidth(); i++) {
				// declare and set some variables.....
				xnew = i;
				ynew = j;
				int x, y;

				// get the random value that will not out of coordinate!!!!
				do {
					int randomX = randomGem.nextInt(nDegree);
					int randomY = randomGem.nextInt(nDegree);
					int offsetX = randomX - nDegree / 2;
					int offsetY = randomY - nDegree / 2;
					x = xnew + offsetX;
					y = ynew + offsetY;
					// The next four line of codes are for slow computers.
					// If your computer is fast, please ignore them for a better
					// quality output even in HIGHLY randomized picture.
					/*
					 * if(x<0 || x>(bufImg.getWidth()-1)) x = i; if(y<0 ||
					 * y>(bufImg.getHeight()-1)) y = j;
					 */
				} while (x < 0 || x > (bufImg.getWidth() - 1) || y < 0
						|| y > (bufImg.getHeight() - 1));

				// put the colour of x,y pixel from the original image to the
				// xnew,ynew pixel of the result image
				int values = bufImg.getRGB(x, y);
				Color color = new Color(values);
				resultImg.setRGB(xnew, ynew, color.getRGB());
			}
		}
		return resultImg;// Simpliy return the product and happy~ ^^
	}

	// ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! End ! ! ! ! ! ! ! ! ! ! !
	// ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !

	// Done.......................
	public static BufferedImage spinning(Image img, double fDegree) {
		BufferedImage bufImg = convert(img);
		BufferedImage resultImg = new BufferedImage(bufImg.getWidth(),
				bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

		// get height and width
		int height = bufImg.getHeight();
		int width = bufImg.getWidth();

		double midX = width / 2;
		double midY = height / 2;

		int ynew = 0;
		for (int j = 0; j < height; j++) {
			double trueY = ynew - midY;
			int xnew = 0;
			for (int i = 0; i < width; i++) {
				double trueX = xnew - midX;

				double theta = Math.atan2(trueY, trueX);
				double radius = Math.sqrt(trueX * trueX + trueY * trueY);
				double degree = theta + fDegree * radius;
				int x = (int) (midX + radius * Math.cos(degree));
				int y = (int) (midY + radius * Math.sin(degree));

				// for debugging System.out.println(" ");
				// put the colour of x,y pixel from the original image to the
				// xnew,ynew pixel of the buffered image
				if (x >= 0 && y >= 0 && x < width && y < height)
				// if(xnew>=0 && ynew>=0 && xnew<width && ynew<height)
				{
					int values = bufImg.getRGB(x, y);
					// int values = bufImg.getRGB(xnew,ynew);
					Color color = new Color(values);
					resultImg.setRGB(xnew, ynew, color.getRGB());
					// resultImg.setRGB(x, y, color.getRGB());
				} else {
					// System.out.println("x: "+x+"  y: "+y+" "+trueX+" "+trueY+" "+midX+" "+midY+" "+theta+" "+radius+" "+degree);
					int values = bufImg.getRGB(i, j);
					Color newColor = new Color(0, 0, 255); // for debugging
					resultImg.setRGB(xnew, ynew, newColor.getRGB());// for
																	// debugging
					// Color color = new Color(values);
					// bufImg.setRGB(xnew, ynew, color.getRGB());
				}
				xnew++;
			}
			ynew++;
		}
		return resultImg;
	}

	// Done............................................
	public static BufferedImage pinching(Image img) {
		BufferedImage bufImg = convert(img);
		BufferedImage resultImg = new BufferedImage(bufImg.getWidth(),
				bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

		int height = bufImg.getHeight();
		int width = bufImg.getWidth();

		int midX = width / 2;
		int midY = height / 2;

		int ynew = 0;
		for (int j = 0; j < height; j++) {
			int trueY = ynew - midY;
			int xnew = 0;
			for (int i = 0; i < width; i++) {
				int trueX = xnew - midX;
				double theta = Math.atan2(trueY, trueX);
				double radius = Math.sqrt(trueX * trueX + trueY * trueY);

				double newRadius = radius * radius / Math.max(midX, midY);
				int x = midX + (int) (newRadius * Math.cos(theta));
				int y = midY + (int) (newRadius * Math.sin(theta));

				/*
				 * for debugging only if(xnew==195&&ynew==250)
				 * System.out.println
				 * ("theta: "+theta+"\nradius: "+radius+"\nnewRadius: "
				 * +newRadius+"\nx: "+x+"    y: "+y+"\n newxy: "+xnew+" "+ynew);
				 * 
				 * //put the colout of x,y pixel from the original image to the
				 * xnew,ynew pixel of the buffered image if(x<0)
				 * do{x++;}while(x<0); if(x>bufImg.getWidth())
				 * do{x--;}while(x>bufImg.getWidth()); if(y<0)
				 * do{y++;}while(y<0); if(y>bufImg.getHeight())
				 * do{y--;}while(y>bufImg.getHeight());
				 */

				if (x >= 0 && y >= 0 && x < width && y < height) {
					int values = bufImg.getRGB(x, y);
					Color color = new Color(values);
					resultImg.setRGB(xnew, ynew, color.getRGB());
				} else {
					int values = bufImg.getRGB(i, j);
					Color color = new Color(values);
					bufImg.setRGB(xnew, ynew, color.getRGB());
					// Color newColor = new Color(0, 0, 255); //for debugging
					// resultImg.setRGB(xnew, ynew, newColor.getRGB());//for
					// debugging
				}
				xnew++;
			}
			ynew++;
		}
		return resultImg;
	}

	// Done and
	// Optimized~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static BufferedImage pixelate(Image img, int pixel) {
		// Open a new image for playing graphic magic~ :)
		BufferedImage bufImg = convert(img);
		BufferedImage resultImg = new BufferedImage(bufImg.getWidth(),
				bufImg.getHeight(), BufferedImage.TYPE_INT_RGB);

		// Declare and set some variables...
		int height = bufImg.getHeight();
		int width = bufImg.getWidth();
		int x, y;

		// Doing magic one by one, pixel by pixel
		int ynew = 0;
		for (int j = 0; j < height; j++) {
			int xnew = 0;
			for (int i = 0; i < width; i++) {
				// formulas for pixelating~ :D
				int a = pixel - xnew % pixel;
				if (a == pixel)
					x = 0;
				else if (xnew + a < width)
					x = xnew + a;
				else
					x = xnew;
				int b = pixel - ynew % pixel;
				if (b == pixel)
					y = 0;
				else if (ynew + b < height)
					y = ynew + b;
				else
					y = ynew;

				// put the colout of x,y pixel from the original image to the
				// xnew,ynew pixel of the buffered image
				int values = bufImg.getRGB(x, y);
				Color color = new Color(values);
				resultImg.setRGB(xnew, ynew, color.getRGB());

				xnew++;
			}
			ynew++;
		}
		return resultImg;// Simpliy return the product and happy~ ^^
	}

	// ----------------------------------------------------+! End of ALL Part A
	// questions !+-----------------------------------------------

	public static void RGBtoHSV(int R, int G, int B, float[] HSV) {
		// R,G,B in [0,255]
		float H = 0, S = 0, V = 0;
		float cMax = 255.0f;
		int cHi = Math.max(R, Math.max(G, B));
		int cLo = Math.min(R, Math.min(G, B));
		int cRng = cHi - cLo;

		// compute value V
		V = cHi / cMax;

		// compute saturation S
		if (cHi > 0)
			S = (float) cRng / cHi;

		// compute hue H
		if (cRng > 0) {
			float rr = (float) (cHi - R) / cRng;
			float gg = (float) (cHi - G) / cRng;
			float bb = (float) (cHi - B) / cRng;
			float hh;
			if (R == cHi)
				hh = bb - gg;
			else if (G == cHi)
				hh = rr - bb + 2.0f;
			else
				hh = gg - rr + 4.0f;
			if (hh < 0)
				hh = hh + 6;
			H = hh / 6;
		}

		if (HSV == null)
			HSV = new float[3];
		HSV[0] = H;
		HSV[1] = S;
		HSV[2] = V;
	}

	public static int HSVtoRGB(float h, float s, float v) {
		// h,s,v in [0,1]
		float rr = 0, gg = 0, bb = 0;
		float hh = (6 * h) % 6;
		int c1 = (int) hh;
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;
		switch (c1) {
		case 0:
			rr = v;
			gg = z;
			bb = x;
			break;
		case 1:
			rr = y;
			gg = v;
			bb = x;
			break;
		case 2:
			rr = x;
			gg = v;
			bb = z;
			break;
		case 3:
			rr = x;
			gg = y;
			bb = v;
			break;
		case 4:
			rr = z;
			gg = x;
			bb = v;
			break;
		case 5:
			rr = v;
			gg = x;
			bb = y;
			break;
		}
		int N = 256;
		int r = Math.min(Math.round(rr * N), N - 1);
		int g = Math.min(Math.round(gg * N), N - 1);
		int b = Math.min(Math.round(bb * N), N - 1);
		int rgb = ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
		return rgb;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Start of Part
	// B~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public static BufferedImage preservingPartColor(Image img,
			boolean[][] mask, int colorVal, int rgValue, int rbValue,
			int gbValue) {
		// Temporary! This is only for us to make the program runnable
		return null;
	}

	// ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! Grade this method please.
	// :) ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !
	// Done and Optimized~~~~~~
	public static char[][] imageToASCII(Image img) {
		BufferedImage bufImg = toGrayScale(img);
		double grayVal;

		// Get the size of grouping~
		String mStr = JOptionPane
				.showInputDialog("How many pixel to you want one ASCII code to represent?\n    Width :");
		int m = Integer.parseInt(mStr);
		String nStr = JOptionPane
				.showInputDialog("How many pixel to you want one ASCII code to represent?\n   Height :");
		int n = Integer.parseInt(nStr);

		// Size of the result array is declared~
		String[][] asciiPixel = new String[(int) Math.ceil(bufImg.getHeight()
				/ n)][(int) Math.ceil(bufImg.getWidth() / m)];
		char[][] asciiChar = new char[(int) Math.ceil(bufImg.getHeight() / n)][(int) Math
				.ceil(bufImg.getWidth() / m)];

		// Conver the group of pixels one by one, group by group~
		int b = 0;
		for (int j = 0; j < bufImg.getHeight(); j += n) {
			int a = 0;
			for (int i = 0; i < bufImg.getWidth(); i += m) {
				// Calculate the total of all the color values in each rows
				// inside the inputed group~ :)
				int[] colorY = new int[n];
				for (int y = 0; y < n; y++) {
					int totalColorX = 0;
					for (int x = 0; x < m; x++) {
						if ((i + x) >= bufImg.getWidth()
								|| (j + y) >= bufImg.getHeight())
							totalColorX += 0;
						else {
							int values = bufImg.getRGB(i + x, j + y);
							Color colorX = new Color(values);
							totalColorX += colorX.getRed();
						}
					}
					colorY[y] = totalColorX;
				}

				// Average the pixels' color value~
				double totalColor = 0.0;
				for (int noOfArray = 0; noOfArray < colorY.length; noOfArray++) {
					totalColor += colorY[noOfArray];
				}
				double average = totalColor / (m * n);
				grayVal = average;

				// Turn different kind of gray to different labels~ :D
				if (grayVal >= 50)
					asciiPixel[b][a] = "#";
				if (grayVal >= 70)
					asciiPixel[b][a] = "8";
				if (grayVal >= 100)
					asciiPixel[b][a] = "&";
				if (grayVal >= 130)
					asciiPixel[b][a] = "o";
				if (grayVal >= 160)
					asciiPixel[b][a] = ":";
				if (grayVal >= 180)
					asciiPixel[b][a] = "*";
				if (grayVal >= 200)
					asciiPixel[b][a] = ".";
				if (grayVal >= 230)
					asciiPixel[b][a] = " ";
				if (grayVal < 50)
					asciiPixel[b][a] = "@";

				// Change all those strings into characters~
				asciiChar[b][a] = asciiPixel[b][a].charAt(0);

				// Counter~
				if (a < ((int) Math.ceil(bufImg.getWidth() / m) - 1))
					a++;
			}
			// Counter~
			if (b < ((int) Math.ceil(bufImg.getHeight() / n) - 1))
				b++;
		}
		return asciiChar;// Simpliy return the product and happy~ ^^
	}
}
// ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! End ! ! ! ! ! ! ! ! ! ! ! ! !
// ! ! ! ! ! ! ! ! ! ! ! ! ! !
// ----------------------------------------------------+! End of ALL Part B
// questions !+-----------------------------------------------
