package net.awesomebox.legoMosaicBuilder;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.lego.LegoBrick;
import net.awesomebox.legoMosaicBuilder.lego.LegoColor;
import net.awesomebox.legoMosaicBuilder.lego.LegoShape;
import net.awesomebox.legoMosaicBuilder.mosaicOptimizers.MosaicOptimizerLevel1;
import net.awesomebox.legoMosaicBuilder.mosaicOptimizers.MosaicOptimizerLevel2;
import net.awesomebox.legoMosaicBuilder.mosaicOptimizers.MosaicOptimizerLevel3;



public final class MosaicBuilder
{
	public final static Mosaic buildMosaic(BufferedImage originalImage, int mosaicStudWidth, int mosaicStudHeight, LegoShape baseShape, boolean baseShapeRotated, int optimizationLevel) throws MosaicBuilderException
	{
		// =============================================================================
		// =============================================================================
		//
		// Prepare Image
		//
		// =============================================================================
		// 
		// Resizes the given image to the given dimensions and converts to the proper
		// image format. Calculates the GCF between the base shape's width and height so
		// it can resize the image appropriately and calculate how many pixels will be
		// used per base brick.
		//
		// For example, if the base shape is 2x2, (GCF 1x1) we will half the width and
		// height of the image and treat each pixel as a single 2x2 brick. If the base
		// shape is 2x4 (GCF 1x2), we will half the width and height and combine every 2
		// pixels into a single 2x4 brick.
		//
		// =============================================================================
		// =============================================================================
		
		
		
		// DEBUG: don't resize
		mosaicStudWidth = originalImage.getWidth();
		mosaicStudHeight = originalImage.getHeight();
		
		
		LegoBrick[] baseShapeBricks = baseShape.getBricks();
		
		boolean normalized1x1; // if the normalized base shape is a 1x1
		int baseShapePixelWidth;  // number of pixels to use per brick horizontally
		int baseShapePixelHeight; // number of pixels to use per brick vertically
		int imagePixelWidth;
		int imagePixelHeight;
		int mosaicBrickWidth;  // number of base shaped bricks wide the mosaic will be
		int mosaicBrickHeight; // number of base shaped bricks tall the mosaic will be
		
		// if the width and height are equal, we know we have a 1x1 normalized shape
		if (baseShape.studWidth == baseShape.studHeight)
		{
			// use 1 pixel per brick
			normalized1x1 = true;
			baseShapePixelWidth  = 1;
			baseShapePixelHeight = 1;
			imagePixelWidth  = mosaicStudWidth;
			imagePixelHeight = mosaicStudHeight;
			mosaicBrickWidth  = mosaicStudWidth;
			mosaicBrickHeight = mosaicStudHeight;
		}
		else
		{
			normalized1x1 = false;
			
			// find the GCF between the stud width and height using Euclid's Algorithm
			int _a = baseShape.studWidth;
			int _b = baseShape.studHeight;
			
			while (_b > 0)
			{
				int temp = _b;
				_b = _a % _b;
				_a = temp;
			}
			
			int gcf = _a;
			
			// make sure the given mosaic dimensions are divisible by our normalized base shape width and height
			if (mosaicStudWidth % gcf > 0) {
				throw new MosaicBuilderException("Invalid width.");
			}
			if (mosaicStudHeight % gcf > 0) {
				throw new MosaicBuilderException("Invalid height.");
			}
			
			// scale the image
			imagePixelWidth  = mosaicStudWidth / gcf;
			imagePixelHeight = mosaicStudHeight / gcf;
			
			// calculate the number of pixels we will use per brick
			baseShapePixelWidth  = baseShape.studWidth  / gcf;
			baseShapePixelHeight = baseShape.studHeight / gcf;
			
			// rotate if needed
			if (baseShapeRotated)
			{
				int temp = baseShapePixelWidth;
				baseShapePixelWidth = baseShapePixelHeight;
				baseShapePixelHeight = temp;
			}
			
			// calculate the number of bricks we will use
			mosaicBrickWidth  = imagePixelWidth  / baseShapePixelWidth;
			mosaicBrickHeight = imagePixelHeight / baseShapePixelHeight;
		}
		
		// stud size of the base shape
		int baseShapeStudWidth;
		int baseShapeStudHeight;
		
		if (baseShapeRotated)
		{
			baseShapeStudWidth  = baseShape.studHeight;
			baseShapeStudHeight = baseShape.studWidth;
		}
		else
		{
			baseShapeStudWidth  = baseShape.studWidth;
			baseShapeStudHeight = baseShape.studHeight;
		}
		
		
		
		// resize image
		BufferedImage image = new BufferedImage(imagePixelWidth, imagePixelHeight, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = image.createGraphics();
		graphics.drawImage(originalImage, 0, 0, imagePixelWidth, imagePixelHeight, null);
		graphics.dispose();
		
		// get pixel data from image
		int[] pixelData = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		
		
		
		
		
		
		
		// =============================================================================
		// =============================================================================
		//
		// Create Base Mosaic
		//
		// =============================================================================
		// 
		// Creates a Mosaic using bricks of the base shape.
		//
		// =============================================================================
		// =============================================================================
		
		
		
		// create mosaic using base shape
		MosaicBrick[][] mosaic = new MosaicBrick[mosaicStudWidth][mosaicStudHeight];
		
		// keep track of colors used in the mosaic
		ArrayList<LegoColor> colorsUsed = new ArrayList<LegoColor>();
		
		for (int brickY = 0; brickY < mosaicBrickHeight; ++brickY)
		{
			for (int brickX = 0; brickX < mosaicBrickWidth; ++brickX)
			{
				int pixelX = brickX * baseShapePixelWidth;
				int pixelY = brickY * baseShapePixelHeight;
				
				int averageR;
				int averageG;
				int averageB;
				
				// average all pixels covered by the base shape together
				if (normalized1x1)
				{
					int pixel = pixelData[pixelY * imagePixelWidth + pixelX];
					
					averageR = (pixel >> 16) & 0b00000000000000000000000011111111;
					averageG = (pixel >> 8)  & 0b00000000000000000000000011111111;
					averageB = (pixel)       & 0b00000000000000000000000011111111;
				}
				else
				{
					int totalR = 0;
					int totalG = 0;
					int totalB = 0;
					
					for (int pixelOffsetY = 0; pixelOffsetY < baseShapePixelHeight; ++pixelOffsetY)
					{
						for (int pixelOffsetX = 0; pixelOffsetX < baseShapePixelWidth; ++pixelOffsetX)
						{
							int pixel = pixelData[(pixelY + pixelOffsetY) * imagePixelWidth + pixelX + pixelOffsetX];
							
							totalR += (pixel >> 16) & 0b00000000000000000000000011111111;
							totalG += (pixel >> 8)  & 0b00000000000000000000000011111111;
							totalB += (pixel)       & 0b00000000000000000000000011111111;
						}
					}
					
					int numPixels = baseShapePixelWidth * baseShapePixelHeight;
					averageR = (int)((double)totalR / numPixels);
					averageG = (int)((double)totalG / numPixels);
					averageB = (int)((double)totalB / numPixels);
				}
				
				
				// find the brick with the color that most closely matches
				int closestColorIndex = -1;
				double closestColorDifference = -1.0d;
				
				for (int j = 0; j < baseShapeBricks.length; ++j)
				{
					LegoColor color = baseShapeBricks[j].color;
					
					// the difference between two colors is really a 3D problem.
					// each color value (red, green, and blue) is an axis (X, Y, Z). A
					// color is a 3D position on these axis. So the distance between two
					// colors is the distance between their 3D points. So we use the
					// distance formula:
					// sqrt((X2-X1)^2, (Y2-Y1)^2, (Z2-Z1)^2)
					double difference = Math.sqrt((color.r - averageR)*(color.r - averageR) + (color.g - averageG)*(color.g - averageG) + (color.b - averageB)*(color.b - averageB));
					
					if (closestColorIndex == -1 || difference < closestColorDifference)
					{
						closestColorIndex = j;
						closestColorDifference = difference;
					}
				}
				
				LegoBrick brick = baseShapeBricks[closestColorIndex];
				LegoColor color = brick.color;
				
				
				// cover the area taken up by the base brick shape with 1x1 bricks
				int studX = brickX * baseShapeStudWidth;
				int studY = brickY * baseShapeStudHeight;
				
				LegoBrick color1x1Brick = color.get1x1Brick();
				
				for (int studOffsetY = 0; studOffsetY < baseShapeStudHeight; ++studOffsetY)
				{
					for (int studOffsetX = 0; studOffsetX < baseShapeStudWidth; ++studOffsetX)
						mosaic[studX + studOffsetX][studY + studOffsetY] = new MosaicBrick(color1x1Brick.orientedBrickNonRotated, studX + studOffsetX, studY + studOffsetY);
				}
				
				
				// record the color used
				boolean duplicate = false;
				for (int j = 0; j < colorsUsed.size(); ++j)
				{
					if (colorsUsed.get(j) == color)
					{
						duplicate = true;
						break;
					}
				}
				
				if (!duplicate)
					colorsUsed.add(color);
			}
		}
		
		
		// return now if we are not optimizing
		if (optimizationLevel < 1)
		{
			MosaicBrick[] mosaicBricks = new MosaicBrick[mosaicStudWidth * mosaicStudHeight];
			
			for (int y = 0; y < mosaicStudWidth; ++y)
			{
				for (int x = 0; x < mosaicStudWidth; ++x)
					mosaicBricks[y * mosaicStudWidth + x] = mosaic[x][y];
			}
			
			return new Mosaic(mosaicBricks);
		}
		
		
		
		
		
		
		// =============================================================================
		// =============================================================================
		//
		// Optimize Mosaic
		//
		// =============================================================================
		// 
		// Optimizes the mosaic to use as few bricks as possible.
		// 
		// =============================================================================
		// =============================================================================
		
		// =============================================================================
		// Optimize Level 1
		//
		// Replaces 1x1 bricks with larger bricks.
		// =============================================================================
		
		MosaicOptimizerLevel1.optimize(colorsUsed, mosaic, mosaicStudWidth, mosaicStudHeight);
		
		
		// create a normalized list of the mosaic bricks
		ArrayList<MosaicBrick> mosaicBricks = new ArrayList<MosaicBrick>();
		
		for (int studY = 0; studY < mosaicBrickHeight; ++studY)
		{
			for (int studX = 0; studX < mosaicBrickWidth; ++studX)
			{
				MosaicBrick mosaicBrick = mosaic[studX][studY];
				
				if (!mosaicBricks.contains(mosaicBrick))
					mosaicBricks.add(mosaicBrick);
			}
		}
		
		
		// =============================================================================
		// Optimize Level 2
		// 
		// Combines and splits bricks for a more efficient brick placement.
		// =============================================================================
		
		if (optimizationLevel < 2)
			return new Mosaic(mosaicBricks.toArray(new MosaicBrick[mosaicBricks.size()]));
		
		MosaicOptimizerLevel2.optimize(mosaic, mosaicStudWidth, mosaicStudHeight, mosaicBricks);
		
		
		// =============================================================================
		// Optimize Level 3
		//
		// Switches brick placement and checks for possible combinations with the new
		// order.
		// =============================================================================
		
		if (optimizationLevel < 3)
			return new Mosaic(mosaicBricks.toArray(new MosaicBrick[mosaicBricks.size()]));
		
		MosaicOptimizerLevel3.optimize(mosaic, mosaicStudWidth, mosaicStudHeight, mosaicBricks);
		
		return new Mosaic(mosaicBricks.toArray(new MosaicBrick[mosaicBricks.size()]));
	}
	
	
	
	
	
	
	public final static class MosaicBuilderException extends Exception
	{
		private static final long serialVersionUID = 1L;
		
		MosaicBuilderException(String message)
		{
			super(message);
		}
	}
}
