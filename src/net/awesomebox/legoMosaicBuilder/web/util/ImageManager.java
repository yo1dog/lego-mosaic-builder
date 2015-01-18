package net.awesomebox.legoMosaicBuilder.web.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.Part;

public class ImageManager
{
	/**
	 * Verifies a multipart encoded HTTP request part is a valid image and is of a valid format.
	 * 
	 * @param part
	 * 	Part to verify.
	 * 
	 * @return
	 * 	If the image is valid and of an acceptable format (<code>true</code>) or not
	 * 	(<code>false</code>).
	 * 
	 * @throws IOException
	 * 
	 * @see #verifyImage(InputStream)
	 */
	public static boolean verifyImagePart(Part part) throws IOException
	{
		InputStream fileInputStream = part.getInputStream();
		
		try
		{
			return verifyImage(fileInputStream);
		}
		finally
		{
			fileInputStream.close();
		}
	}
	
	/**
	 * Verifies an image is a valid image and is of a valid format.
	 * 
	 * @param fileInputStream
	 * 	Input stream to the file to verify.
	 * 
	 * @return
	 * 	If the image is valid and of an acceptable format (<code>true</code>) or not
	 * 	(<code>false</code>).
	 * 
	 * @throws IOException
	 */
	public static boolean verifyImage(InputStream fileInputStream) throws IOException
	{
		// create image input stream from file input stream
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(fileInputStream);
		boolean isImage;
		
		try
		{
			// get the image readers
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
			
			// make sure we have a reader
			if (!readers.hasNext())
			{
				System.err.println("Invalid image upload. No readers.");
				return false;
			}
			
			ImageReader reader = readers.next();
			
			// get the format names of the file
			String[] formatNames = reader.getOriginatingProvider().getFormatNames();
			
			// make sure the format matches one of the valid types
			isImage = false;
			for (int i = 0; i < formatNames.length && !isImage; ++i)
			{
				for (int j = 0; j < Config.IMAGE_VALID_TYPES.length && !isImage; ++j)
					isImage = formatNames[i].equals(Config.IMAGE_VALID_TYPES[j]);
			}
		}
		finally
		{
			if (imageInputStream != null)
				imageInputStream.close();
		}
		
		return isImage;
	}
	
	
	public static BufferedImage createImageFromMultipart(Part part) throws IOException
	{
		// get the input stream from the part
		InputStream partInputStream = part.getInputStream();
		
		BufferedImage image;
		try
		{
			// create the image
			image = ImageIO.read(partInputStream);
		}
		finally
		{
			partInputStream.close();
		}
		
		return image;
	}
}
