import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class DepthMap {

	private double blurAtInfinity;
	private double focalDistance;
	private double focalPointX;
	private double focalPointY;

	private String depthFormat;
	private String depthMime;
	private double depthNear;
	private double depthFar;
	private byte[] depthImage;

	public String colourMime;
	public byte[] colourImage;

	public DepthMap() {
		blurAtInfinity = Double.NaN;
		focalDistance = Double.NaN;
		focalPointX = Double.NaN;
		focalPointY = Double.NaN;
		depthFormat = null;
		depthMime = null;
		depthNear = Double.NaN;
		depthFar = Double.NaN;
		depthImage = null;
		colourMime = null;
		colourImage = null;
	}

	public DepthMap(File file) {
		// Try turn BufferedImage to img
		// DataBufferByte data = (DataBufferByte)
		// img.getRaster().getDataBuffer();
		// byte[] imageByte = data.getData();

		// try {
		// ByteArrayOutputStream os = new ByteArrayOutputStream();
		// ImageIO.write(img, "jpg", os);
		// InputStream fis = new ByteArrayInputStream(os.toByteArray());
		// String imageData = Util.readInputStream(fis);
		// // Fail to load correct imageData
		// loadImageData(imageData);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// String imageData = Util.encodeToString(img,"jpg");

		InputStream is;
		try {
			is = new FileInputStream(file);
			String imageData = Util.readInputStream(is);
			loadImageData(imageData);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isValid() {
		if (blurAtInfinity != Double.NaN)
			System.out.println("1.  blurAtInfinity: " + blurAtInfinity);
		if (focalDistance != Double.NaN)
			System.out.println("2.  focalDistance: " + focalDistance);
		if (focalPointX != Double.NaN)
			System.out.println("3.  focalPointX: " + focalPointX);
		if (focalPointY != Double.NaN)
			System.out.println("4.  focalPointY: " + focalPointY);
		if (depthFormat != null)
			System.out.println("5.  depthFormat: " + depthFormat);
		if (depthMime != null)
			System.out.println("6.  depthMime: " + depthMime);
		if (depthNear != Double.NaN)
			System.out.println("7.  depthNear: " + depthNear);
		if (depthFar != Double.NaN)
			System.out.println("8.  depthFar: " + depthFar);
		if (depthImage != null)
			System.out.println("9.  depthImage: " + depthImage);
		if (colourMime != null)
			System.out.println("10. colourMime: " + colourMime);
		if (colourImage != null)
			System.out.println("11. colourImage: " + colourImage);

		return (blurAtInfinity != Double.NaN && focalDistance != Double.NaN
				&& focalPointX != Double.NaN && focalPointY != Double.NaN
				&& depthFormat != null && depthMime != null
				&& depthNear != Double.NaN && depthFar != Double.NaN
				&& depthImage != null && colourMime != null && colourImage != null);
	}

	private void loadImageData(String img) {

		System.out.println("loadImage got " + img.length() + " bytes");
		img = Util.removeBlockHeaders(img);
		System.out
				.println("removed block headers (" + img.length() + " bytes)");

		this.colourMime = Util.readStringAttr(img, "GImage:Mime");
		if (this.colourMime == null)
			return; /* Early fail */

		this.blurAtInfinity = Util.readFloatAttr(img, "GFocus:BlurAtInfinity");
		this.focalDistance = Util.readFloatAttr(img, "GFocus:FocalDistance");
		this.focalPointX = Util.readFloatAttr(img, "GFocus:FocalPointX");
		this.focalPointY = Util.readFloatAttr(img, "GFocus:FocalPointY");

		this.depthFormat = Util.readStringAttr(img, "GDepth:Format");
		this.depthMime = Util.readStringAttr(img, "GDepth:Mime");
		this.depthNear = Util.readFloatAttr(img, "GDepth:Near");
		this.depthFar = Util.readFloatAttr(img, "GDepth:Far");

		this.colourImage = Util.readBase64Attr(img, "GImage:Data");
		if (colourImage != null)
			System.out.println(colourImage.length);
		this.depthImage = Util.readBase64Attr(img, "GDepth:Data");
		if (depthImage != null)
			System.out.println(depthImage.length);
	}

	public BufferedImage getDepthImage() {
		return readBufferImage(this.depthImage);
	}

	private BufferedImage readBufferImage(byte[] imageDataInByte) {
		if (this.isValid()) {
			InputStream in = new ByteArrayInputStream(imageDataInByte);
			try {
				BufferedImage result = ImageIO.read(in);
				return result;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		} else {
			System.out.println("Image is invalid");
			return null;
		}
	}
}
