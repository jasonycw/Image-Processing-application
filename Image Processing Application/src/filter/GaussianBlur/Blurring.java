package filter.GaussianBlur;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public class Blurring {
	protected float radius;
	public static int ZERO_EDGES = 0;
	public static int CLAMP_EDGES = 1;
	public static int WRAP_EDGES = 2;
	protected static Kernel kernel = null;
	public static boolean alpha = true;

	/**
	 * Construct a blur filter
	 */
	public Blurring() {
		this(2);
	}

	/**
	 * Construct a blur filter
	 * 
	 * @param radius
	 *            blur radius in pixels
	 */
	public Blurring(float radius) {
		setRadius(radius);
	}

	/**
	 * Set the radius of the kernel, and hence the amount of blur. The bigger
	 * the radius, the longer this filter will take.
	 * 
	 * @param radius
	 *            the radius of the blur in pixels.
	 */
	public void setRadius(float radius) {
		this.radius = radius;
		kernel = makeKernel(radius);
	}

	/**
	 * Make a Gaussian blur kernel.
	 */
	public static Kernel makeKernel(float radius) {
		int r = (int) Math.ceil(radius);
		int rows = r * 2 + 1;
		float[] matrix = new float[rows];
		float sigma = radius / 3;
		float sigma22 = 2 * sigma * sigma;
		float sigmaPi2 = (float) (2 * Math.PI * sigma);
		float sqrtSigmaPi2 = (float) Math.sqrt(sigmaPi2);
		float radius2 = radius * radius;
		float total = 0;
		int index = 0;
		for (int row = -r; row <= r; row++) {
			float distance = row * row;
			if (distance > radius2)
				matrix[index] = 0;
			else
				matrix[index] = (float) Math.exp(-(distance) / sigma22)
						/ sqrtSigmaPi2;
			total += matrix[index];
			index++;
		}
		for (int i = 0; i < rows; i++)
			matrix[i] /= total;

		return new Kernel(rows, 1, matrix);
	}

	private static BufferedImage convert(Image img) {
		BufferedImage bi = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(img, 0, 0, null);
		bg.dispose();
		return bi;
	}

	public static BufferedImage gaussianBlur(Image img) {
		BufferedImage src = convert(img);
		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		int width = src.getWidth();
		int height = src.getHeight();

		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		src.getRGB(0, 0, width, height, inPixels, 0, width);

		convolveAndTranspose(kernel, inPixels, outPixels, width, height, alpha,
				CLAMP_EDGES);
		convolveAndTranspose(kernel, outPixels, inPixels, height, width, alpha,
				CLAMP_EDGES);

		dst.setRGB(0, 0, width, height, inPixels, 0, width);
		return dst;
	}

	public static void convolveAndTranspose(Kernel kernel, int[] inPixels,
			int[] outPixels, int width, int height, boolean alpha,
			int edgeAction) {
		float[] matrix = kernel.getKernelData(null);
		int cols = kernel.getWidth();
		int cols2 = cols / 2;

		for (int y = 0; y < height; y++) {
			int index = y;
			int ioffset = y * width;
			for (int x = 0; x < width; x++) {
				float r = 0, g = 0, b = 0, a = 0;
				int moffset = cols2;
				for (int col = -cols2; col <= cols2; col++) {
					float f = matrix[moffset + col];

					if (f != 0) {
						int ix = x + col;
						if (ix < 0) {
							if (edgeAction == CLAMP_EDGES)
								ix = 0;
							else if (edgeAction == WRAP_EDGES)
								ix = (x + width) % width;
						} else if (ix >= width) {
							if (edgeAction == CLAMP_EDGES)
								ix = width - 1;
							else if (edgeAction == WRAP_EDGES)
								ix = (x + width) % width;
						}
						int rgb = inPixels[ioffset + ix];
						a += f * ((rgb >> 24) & 0xff);
						r += f * ((rgb >> 16) & 0xff);
						g += f * ((rgb >> 8) & 0xff);
						b += f * (rgb & 0xff);
					}
				}
				int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 0xff;
				int ir = PixelUtils.clamp((int) (r + 0.5));
				int ig = PixelUtils.clamp((int) (g + 0.5));
				int ib = PixelUtils.clamp((int) (b + 0.5));
				outPixels[index] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
				index += height;
			}
		}
	}

}
