package net.awesomebox.legoMosaicBuilder.web.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import net.awesomebox.legoMosaicBuilder.builder.Mosaic;
import net.awesomebox.legoMosaicBuilder.builder.MosaicBuilder;
import net.awesomebox.legoMosaicBuilder.builder.MosaicBuilder.MosaicBuilderException;
import net.awesomebox.legoMosaicBuilder.lego.LegoShape;
import net.awesomebox.legoMosaicBuilder.web.api.datamodel.MosaicModel;
import net.awesomebox.legoMosaicBuilder.web.util.ImageManager;

import net.awesomebox.servletmanager.ManagedHttpServlet;
import net.awesomebox.servletmanager.ServletHelper;
import net.awesomebox.servletmanager.exceptions.InternalErrorException;
import net.awesomebox.servletmanager.exceptions.InvalidHTTPRequestParameterException;

@WebServlet("/api/buildMosaic")
@MultipartConfig(
		maxFileSize    = 5242880,  // 5MB
		maxRequestSize = 11534336) // 11MB
public class BuildMosaic extends ManagedHttpServlet
{
	private static final long serialVersionUID = 1L;
    
	@Override
	protected void _doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// get the width and the height
		int mosaicStudWidth  = ServletHelper.getRequiredParameterInt(request, "mosaicWidth");
		int mosaicStudHeight = ServletHelper.getRequiredParameterInt(request, "mosaicHeight");
		
		if (mosaicStudWidth > 200)  throw new InvalidHTTPRequestParameterException("mosaicWidth",  "Must be less than or equal to 200.");
		if (mosaicStudHeight > 200) throw new InvalidHTTPRequestParameterException("mosaicHeight", "Must be less than or equal to 200.");
		if (mosaicStudWidth < 1)    throw new InvalidHTTPRequestParameterException("mosaicWidth",  "Must be greater than or equal to 1.");
		if (mosaicStudHeight < 1)   throw new InvalidHTTPRequestParameterException("mosaicHeight", "Must be greater than or equal to 1.");
		
		// get the image part
		Part mosaicImagePart = ServletHelper.getRequiredPartNotEmpty(request, "mosaicImage");
		
		// verify image
		if (!ImageManager.verifyImagePart(mosaicImagePart))
			throw new InvalidHTTPRequestParameterException("mosaicImage", "Not a valid image type.");
		
		// create image from part
		BufferedImage mosaicImage = ImageManager.createImageFromMultipart(mosaicImagePart);
		
		if (mosaicImage == null)
			throw new InvalidHTTPRequestParameterException("mosaicImage", "Not a valid image.");
		
		// create the mosaic
		Mosaic mosaic;
		
		try
		{
			mosaic = MosaicBuilder.buildMosaic(mosaicImage, mosaicStudWidth, mosaicStudHeight, LegoShape._1x1, false, 2);
		}
		catch (MosaicBuilderException e)
		{
			throw new InternalErrorException("An error occured while building the mosaic.", e);
		}
		
		ServletHelper.printJSON(response, new MosaicModel(mosaic));
	}
}
