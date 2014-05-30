package filter.bokeh;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Bokeh {
	private BufferedImage originalImage;// Reference to the original image
	private BufferedImage bokehImage;// New image calculate using original image
	private int height;
	private int width;
	private int bokehRadius = 2;
	private int brightnessThreshold = 150;

	public Bokeh() {
		this.originalImage = null;
		this.bokehImage = null;
	}

	public Bokeh(BufferedImage image) {
		this.bokehImage = renderBokeh(image);
	}

	public Bokeh(BufferedImage image, int radius) {
		setRadius(radius);
		this.bokehImage = renderBokeh(image);
	}

	public void setImage(BufferedImage image) {
		this.originalImage = image;
		this.height = image.getHeight();
		this.width = image.getWidth();
		System.out.println("width: "+ this.width);
		System.out.println("height: "+ this.height);
	}

	public void setRadius(int radius) {
		this.bokehRadius = radius;
	}

	public void setBrightnessThreshold(int brightnessThreshold) {
		this.brightnessThreshold = brightnessThreshold;
	}

	public BufferedImage getOriginalImage() {
		return originalImage;
	}

	public BufferedImage getBokehEffectImage() {
		return bokehImage;
	}

	private BufferedImage renderBokeh(BufferedImage image) {
		if (image == null)
			return null;
		setImage(image);
		BufferedImage resultImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (isBrighterThanSurrounding(i, j, image))
					drawBokehEffect(i, j, resultImage);
			}
		}
		return resultImage;
	}

	private boolean isBrighterThanSurrounding(int x, int y, BufferedImage image) {
		if (image == null)
			return false;

		// TODO Auto-generated method stub
		int rgb = image.getRGB(x, y);
		Color pointColor = new Color(rgb);

		// Gather the color of the surrounding
		int totalRED = 0;
		int totalGREEN = 0;
		int totalBLUE = 0;
		int numberOfPixelGathered = 0;
		for (int i = this.bokehRadius; i > 0; i--)
			for (int j = this.bokehRadius; j >= 0; j--) {
				// Initialize 4 point for taking color around the pointColor
				int xBottomRight = x + j;
				int yBottomRight = y + i;

				int xBottomLeft = x - i;
				int yBottomLeft = y + j;

				int xTopRight = x + i;
				int yTopRight = y - j;

				int xTopLeft = x - j;
				int yTopLeft = y - i;

				// Collect the colors
				if (xBottomLeft >= 0 && yBottomLeft < this.height) {
//					System.out.println("x: "+ xBottomRight);
//					System.out.println("y: "+ yBottomRight);
					rgb = image.getRGB(xBottomLeft, yBottomLeft);
					totalRED += new Color(rgb).getRed();
					totalGREEN += new Color(rgb).getGreen();
					totalBLUE += new Color(rgb).getBlue();
					numberOfPixelGathered++;
				}
				if (xBottomRight < this.width && yBottomRight < this.height) {
//					System.out.println("x: "+ xBottomRight);
//					System.out.println("y: "+ yBottomRight);
					rgb = image.getRGB(xBottomRight, yBottomRight);
					totalRED += new Color(rgb).getRed();
					totalGREEN += new Color(rgb).getGreen();
					totalBLUE += new Color(rgb).getBlue();
					numberOfPixelGathered++;
				}
				if (xTopLeft >= 0 && yTopLeft >= 0) {
//					System.out.println("x: "+ xBottomRight);
//					System.out.println("y: "+ yBottomRight);
					rgb = image.getRGB(xTopLeft, yTopLeft);
					totalRED += new Color(rgb).getRed();
					totalGREEN += new Color(rgb).getGreen();
					totalBLUE += new Color(rgb).getBlue();
					numberOfPixelGathered++;
				}
				if (xTopRight < this.width && yTopRight >= 0) {
//					System.out.println("x: "+ xBottomRight);
//					System.out.println("y: "+ yBottomRight);
					rgb = image.getRGB(xTopRight, yTopRight);
					totalRED += new Color(rgb).getRed();
					totalGREEN += new Color(rgb).getGreen();
					totalBLUE += new Color(rgb).getBlue();
					numberOfPixelGathered++;
				}
			}
		Color averageColor = new Color(totalRED / numberOfPixelGathered, totalGREEN / numberOfPixelGathered, totalBLUE
		        / numberOfPixelGathered);

		// Calculate the brightness of the color
		int pointBrightness = brightnessOf(pointColor);
		int averageBrightness = brightnessOf(averageColor);

		return pointBrightness - averageBrightness >= brightnessThreshold;
	}

	private static int brightnessOf(Color c) {
		return (int) Math.sqrt(c.getRed() * c.getRed() * .241 + c.getGreen() * c.getGreen() * .691 + c.getBlue() * c.getBlue()
		        * .068);
	}

	private void drawBokehEffect(int x, int y, BufferedImage resultImg) {
		// TODO Auto-generated method stub
		Graphics2D g2d = resultImg.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(new Color(this.originalImage.getRGB(x, y)));
		int length = this.bokehRadius * 2 + 1;
		g2d.fillOval(x, y, length, length);
		g2d.dispose();
	}
}
