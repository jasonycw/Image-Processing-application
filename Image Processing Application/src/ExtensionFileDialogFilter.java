import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileDialogFilter extends FileFilter
{
	String ext;   
    
	public ExtensionFileDialogFilter(String ext)   
   {
		this.ext = ext;   
	}
    
	public boolean accept(File file)
   {
		if(file.isDirectory())
			return true;
      String fileName = file.getName();
      int index = fileName.lastIndexOf('.');
      if(index > 0 && index < fileName.length() - 1)
		{   
			String extension = fileName.substring(index + 1).toLowerCase();
			if(extension.equals(ext))   
				return true;
		}   
      return false;   
	}   
	
	public String getDescription()
	{
		if(ext.equals("jpg"))   
			return "JPEG File (*.jpg, *.jpeg, *.JPG, *.JPEG)";
		else if(ext.equals("bmp"))   
			return "BMP File (*.bmp, *.BMP)";
		else
      	return "";
	}
}
    
