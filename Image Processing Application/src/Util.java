import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;

public class Util {
	private static final String TAG = "Util";

	public static String readInputStream(InputStream is) {
		StringBuffer sb = new StringBuffer();
		byte[] buf = new byte[1024];

		long start = System.currentTimeMillis();

		try {
			while (true) {
				int r = is.read(buf, 0, buf.length);
				if (r == -1)
					break;
				sb.append(new String(buf, 0, r, "ISO-8859-1"));
			}
		} catch (IOException e) {
			throw new Error("cannot read input stream", e);
		}

		return sb.toString();
	}

	public static String readStringAttr(String img, String attr) {
		System.out.println("img.length: " + img.length());
		Pattern pat = Pattern.compile(attr + "=\"([^\"]*)\"",
				Pattern.CASE_INSENSITIVE);
		Matcher m = pat.matcher(img);

		if (!m.find())
			return null;
		return m.group(1);
	}

	public static double readFloatAttr(String img, String attr) {
		String s = readStringAttr(img, attr);
		if (s == null)
			return Double.NaN;
		return Double.parseDouble(s);
	}

	public static byte[] readBase64Attr(String img, String attr) {
		String v = readStringAttr(img, attr);
		if (v == null)
			return null;
		System.out.println("String.length: " + v.length());
//		System.out.println("String: "+v.toString());
		

		// Works with small image only
		// return DatatypeConverter.parseBase64Binary(v);
		
		return Base64.decodeBase64(v);
	}

	public static String removeBlockHeaders(String img) {
		final String marker = readStringAttr(img, "xmpNote:HasExtendedXMP");
		if (marker == null)
			return img;

		StringBuffer sb = new StringBuffer();

		Pattern pat = Pattern.compile("....http://ns.adobe.com/xmp/extension/."
				+ marker + "........");
		Matcher m = pat.matcher(img);

		while (m.find())
			m.appendReplacement(sb, "");
		m.appendTail(sb);

		return sb.toString();
	}

	// public static String encodeToString(BufferedImage image, String type) {
	// String imageString = null;
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//
	// try {
	// ImageIO.write(image, type, bos);
	// byte[] imageBytes = bos.toByteArray();
	//
	// imageString = DatatypeConverter.printBase64Binary(imageBytes);
	//
	// bos.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return imageString;
	// }
}
