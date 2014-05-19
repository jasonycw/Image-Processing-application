import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class ImageProcessingApplication
{

	public static void main(String[] args)
	{
		String str = "DCO10103 (Semester A, 2011-12) - Programming in Java: Image Processing";
		ImageFrame iFrameOriginal = new ImageFrame(str);		
		iFrameOriginal.showWin();
		iFrameOriginal.setExtendedState(Frame.MAXIMIZED_BOTH);
		
	}
}