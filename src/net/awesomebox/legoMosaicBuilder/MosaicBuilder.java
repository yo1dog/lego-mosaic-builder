package net.awesomebox.legoMosaicBuilder;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.lego.LegoBrick;
import net.awesomebox.legoMosaicBuilder.lego.LegoColor;
import net.awesomebox.legoMosaicBuilder.lego.LegoShape;
import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;



public final class MosaicBuilder
{
	public final static Mosaic buildMosaic(BufferedImage originalImage, int mosaicStudWidth, int mosaicStudHeight, LegoShape baseShape, boolean baseShapeRotated) throws MosaicBuilderException
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
		
		
		
		
		
		
		
		
		// =============================================================================
		// =============================================================================
		//
		// Optimize Level 1
		//
		// =============================================================================
		// 
		// Replaces 1x1 bricks with larger bricks.
		//
		// We start with the largest brick, placing as many of them as will fit over the
		// 1x1s of the same color. We then do the same with the brick rotated. We
		// continue this pattern with all the available bricks of a color down to the
		// smallest brick.
		//
		// =============================================================================
		// =============================================================================
		
		
		
		// optimize level 1
		// for each color used
		for (LegoColor color : colorsUsed)
		{
			LegoBrick color1x1Brick = color.get1x1Brick();
			
			// replace the 1x1 bricks with the largest bricks first, then the smallest
			// IMPORTANT: we assume the list of bricks for a color is ordered from largest shape to smallest
			for (LegoBrick brick : color.getBricks())
			{
				// don't go lower than 1x1
				if (brick.shape == LegoShape._1x1)
					break;
				
				
				// IMPORTANT: we assume the height of the shape is greater than the width (non-rotated)
				// iterate based on the longest length of the shape
				// for example, if the shape is not rotated (height > width):
				//   we move top->bottom then left->right looking for the origin (starting) stud, then
				//   we move left->right then top->bottom when we are checking if the brick can fit.
				//   This is so we can know how many studs we can advance if the shape does or does not
				//   fit.
				// Example:
				//  If we are trying to place a non-rotated 2x6 starting at studs 0,0
				//  (O is a valid stud, X is invalid)
				//
				//  O O
				//  O O
				//  O O
				//  O X
				//  O O
				//  O O
				//
				//  We will check
				//  0,0 0,1
				//  1,0 1,1
				//  2,0 2,1
				//  3,0 3,1 <- invalid space, won't fit
				//
				//  If we find that rows 0-2 are clear, but row 3 has an offending stud preventing
				//  us from placing the brick, we know that we can not fit a 2x6 starting at rows
				//  0-3 because they will all collide with the offending stud on row 3. Therefore,
				//  our next check should start at row 4.
				
				// find places where this brick's shape can fit in the current color
				
				int brickShapeStudWidth;
				int brickShapeStudHeight;
				
				
				// non-rotated
				brickShapeStudWidth = brick.shape.studWidth;
				brickShapeStudHeight = brick.shape.studHeight;
				
				int studXLimit = mosaicStudWidth  - (brickShapeStudWidth  - 1);
				int studYLimit = mosaicStudHeight - (brickShapeStudHeight - 1);
				
				for (int studX = 0; studX < studXLimit; ++studX)
				{
					for (int studY = 0; studY < studYLimit; ++studY)
					{
						boolean willFit = true;
						
						int invalidStudOffsetY = 0;
						for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
						{
							for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
							{
								// check if this stud contains a 1x1 of the correct color
								if (mosaic[studX + studOffsetX][studY + studOffsetY].orientedBrick.brick != color1x1Brick)
								{
									willFit = false;
									break;
								}
							}
							
							if (!willFit)
							{
								invalidStudOffsetY = studOffsetY;
								break;
							}
						}
						
						if (willFit)
						{
							// create the mosaic brick
							MosaicBrick mosaicBrick = new MosaicBrick(brick.orientedBrickNonRotated, studX, studY);
							
							// place the brick onto the mosaic, replacing the 1x1s
							for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
							{
								for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
									mosaic[studX + studOffsetX][studY + studOffsetY] = mosaicBrick;
							}
							
							// advance the length of the shape
							studY += brickShapeStudHeight - 1;
						}
						else
						{
							// skip forward as there is no need to test the studs in between.
							studY += invalidStudOffsetY;
						}
					}
				}
				
				
				// rotated
				// only if not square
				if (brick.shape.studWidth != brick.shape.studHeight)
				{
					brickShapeStudWidth = brick.shape.studHeight;
					brickShapeStudHeight = brick.shape.studWidth;
					
					studXLimit = mosaicStudWidth  - (brickShapeStudWidth  - 1);
					studYLimit = mosaicStudHeight - (brickShapeStudHeight - 1);
					
					for (int studY = 0; studY < studYLimit; ++studY)
					{
						for (int studX = 0; studX < studXLimit; ++studX)
						{
							boolean willFit = true;
							
							int invalidStudOffsetX = 0;
							for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
							{
								for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
								{
									// check if this stud contains a 1x1 of the correct color
									if (mosaic[studX + studOffsetX][studY + studOffsetY].orientedBrick.brick != color1x1Brick)
									{
										willFit = false;
										break;
									}
								}
								
								if (!willFit)
								{
									invalidStudOffsetX = studOffsetX;
									break;
								}
							}
							
							if (willFit)
							{
								// create the mosaic brick
								MosaicBrick mosaicBrick = new MosaicBrick(brick.orientedBrickRotated, studX, studY);
								
								// place the brick onto the mosaic, replacing the 1x1s
								for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
								{
									for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
										mosaic[studX + studOffsetX][studY + studOffsetY] = mosaicBrick;
								}
								
								// advance the length of the shape
								studX += brickShapeStudWidth - 1;
							}
							else
							{
								// skip forward as there is no need to test the studs in between.
								studX += invalidStudOffsetX;
							}
						}
					}
				}
			}
		}
		
		
		// normalize the list of mosaic bricks
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
		// =============================================================================
		//
		// Optimize Level 2
		//
		// =============================================================================
		// 
		// Combines and splits bricks for a more efficient brick placement.
		//
		// We iterate through each brick and check if there are any other bricks that
		// the current brick could combine with. If there is one, we split the bricks
		// in between if necessary and combine the bricks. We keep track of which bricks
		// split which and we prevent a brick from splitting the brick who split them.
		// This keeps us out of infinite loops.
		//
		// =============================================================================
		// =============================================================================
		
		
		
		// optimize level 2
		// combine and split bricks
		ArrayList<MosaicBrick> bricksToOptimize = new ArrayList<MosaicBrick>(mosaicBricks);
		
		for (int mosaicBrickIndex = 0; mosaicBrickIndex < bricksToOptimize.size(); ++mosaicBrickIndex)
		{
			MosaicBrick mosaicBrick = bricksToOptimize.get(mosaicBrickIndex);
			
			if (mosaicBrick.wasRemoved())
				continue;
			
			// check if there are any mosaic bricks that this mosaic brick can combine with
			
			
			
			
			
			
			// --------------------------------------------------------------------
			// Stretch Width
			
			
			
			
			
			// get the available widths for the brick's height
			// example:
			//  if the brick is a 1x2, the available widths are 1x2, 2x2, 3x2, 4x2, 6x2
			//  if the brick is a 2x4, the available widths are 1x4, 2x4
			OrientedLegoBrick[] orientedBrickWidths = mosaicBrick.orientedBrick.brick.color.getBricksByStudHeight(mosaicBrick.orientedBrick.orientedStudHeight);
			int orientedBrickMaxWidth = mosaicBrick.orientedBrick.brick.color.getBricksByStudHeightMaxWidth(mosaicBrick.orientedBrick.orientedStudHeight);
			
			
			// start from one greater than the current width
			// studXOffset = width - 1;
			// width = orientedStudWidth + 1;
			// studXOffset = orientedStudWidth + 1 - 1;
			// studXOffset = orientedStudWidth;
			for (int checkStudOffsetX = mosaicBrick.orientedBrick.orientedStudWidth; checkStudOffsetX < orientedBrickMaxWidth; ++checkStudOffsetX)
			{
				int checkStudX = mosaicBrick.originStudX + checkStudOffsetX;
				
				// if we hit the edge, we can't extend further
				if (checkStudX >= mosaicStudWidth)
					break;
				
				// make sure they are the correct color
				boolean correctColor = true;
				for (int studOffsetY = 0; studOffsetY < mosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
				{
					if (mosaic[checkStudX][mosaicBrick.originStudY + studOffsetY].orientedBrick.brick.color != mosaicBrick.orientedBrick.brick.color)
					{
						correctColor = false;
						break;
					}
				}
				
				// if there is a stud with a different color brick on it, we can't extend any further
				if (!correctColor)
					break;
				
				
				// check if there is a brick we can combine with
				MosaicBrick combiningMosaicBrick = mosaic[checkStudX][mosaicBrick.originStudY];
				
				// check if
				//   both bricks have the same Y origin (tops are aligned)
				//   both bricks have the same height
				if (combiningMosaicBrick.originStudY == mosaicBrick.originStudY &&
					combiningMosaicBrick.orientedBrick.orientedStudHeight == mosaicBrick.orientedBrick.orientedStudHeight)
				{
					// check if there exists a brick that would cover the distance from the current brick's start
					// to the second brick's end
					int combiningBrickStudWidth = (combiningMosaicBrick.originStudX + combiningMosaicBrick.orientedBrick.orientedStudWidth) - mosaicBrick.originStudX;
					
					// make sure the width would not be longer than our max
					if (combiningBrickStudWidth > orientedBrickMaxWidth)
						continue;
					
					OrientedLegoBrick combiningOrientedBrick = orientedBrickWidths[combiningBrickStudWidth - 1];
					
					// if there is not a brick with the needed size to combine the bricks, we cannot combine
					if (combiningOrientedBrick == null)
						continue;
					
					
					// we should be able to combine these bricks as long as we can split the bricks in between
					// create the combined brick
					MosaicBrick combinedMosaicBrick = new MosaicBrick(combiningOrientedBrick, mosaicBrick.originStudX, mosaicBrick.originStudY);
					
					int betweenBricksStudWidth = combiningMosaicBrick.originStudX - (mosaicBrick.originStudX + mosaicBrick.orientedBrick.orientedStudWidth);
					
					
					// create a list of all the bricks that need to be split
					MosaicSplitBrick[] splitBricks = new MosaicSplitBrick[betweenBricksStudWidth * mosaicBrick.orientedBrick.orientedStudHeight];
					int numSplitBricks = 0;
					
					for (int studOffsetY = 0; studOffsetY < mosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
					{
						for (int studOffsetX = 0; studOffsetX < betweenBricksStudWidth; ++studOffsetX)
						{
							MosaicBrick brickToSplit = mosaic[mosaicBrick.originStudX + mosaicBrick.orientedBrick.orientedStudWidth + studOffsetX][mosaicBrick.originStudY + studOffsetY];
							
							// check if we already have this brick in our list
							boolean duplicate = false;
							for (int i = 0; i < numSplitBricks; ++i)
							{
								if (splitBricks[i].originalMosaicBrick == brickToSplit)
								{
									duplicate = true;
									break;
								}
							}
							
							if (!duplicate)
								splitBricks[numSplitBricks++] = new MosaicSplitBrick(brickToSplit);
						}
					}
					
					
					// make sure we can split all the bricks in between
					boolean canSplitAllBricks = true;
					for (int i = 0; i < numSplitBricks; ++i)
					{
						MosaicSplitBrick splitBrick = splitBricks[i];
						
						// if the current brick or the combining brick has been split by this brick, we cannot split it
						if (mosaicBrick.splitter == splitBrick.originalMosaicBrick || combiningMosaicBrick.splitter == splitBrick.originalMosaicBrick)
						{
							canSplitAllBricks = false;
							break;
						}
						
						// get the available heights for the brick we are splitting
						OrientedLegoBrick[] brickToSplitOrientedBrickHeights = splitBrick.originalMosaicBrick.orientedBrick.brick.color.getBricksByStudWidth(splitBrick.originalMosaicBrick.orientedBrick.orientedStudWidth);
						
						
						// split top
						int splitTopBrickHeight = combinedMosaicBrick.originStudY - splitBrick.originalMosaicBrick.originStudY;
						
						// if the top of the brick we are splitting does not start above the combined brick's top, it will be removed
						if (splitTopBrickHeight > 0)
						{
							// make sure we have a brick or two bricks that can fit the split size
							splitBrick.negOrientedBrick = brickToSplitOrientedBrickHeights[splitTopBrickHeight - 1];
							
							if (splitBrick.negOrientedBrick == null)
							{
								// there is no single brick that will fit the size
								// try combinations of two bricks
								// only need to try half the combinations as the second half is the first repeated but backwards
								// example:
								//  splitTopBrickHeight = 5;
								//  we have heights of 1,2,3,4,6
								//  possible combinations are 5,0 4,1 3,2 2,3 1,4 0,5
								//  skip 0 and try combinations 1 and 2
								int numCombinations = (int)(splitTopBrickHeight * 0.5f + 0.5f); // rounded up
								
								boolean foundCombo = false;
								for (int diff = 1; diff < numCombinations; ++diff)
								{
									splitBrick.negOrientedBrick      = brickToSplitOrientedBrickHeights[splitTopBrickHeight - numCombinations - 1];
									splitBrick.negExtraOrientedBrick = brickToSplitOrientedBrickHeights[numCombinations - 1];
									
									if (splitBrick.negOrientedBrick != null && splitBrick.negExtraOrientedBrick != null)
									{
										// found a combination
										foundCombo = true;
										break;
									}
								}
								
								if (!foundCombo)
								{
									// there is not a brick with the needed size to create the top split brick
									canSplitAllBricks = false;
									break;
								}
							}
						}
						
						
						// split bottom
						int splitBottomBrickHeight = (splitBrick.originalMosaicBrick.originStudY + splitBrick.originalMosaicBrick.orientedBrick.orientedStudHeight) - (combinedMosaicBrick.originStudY + combinedMosaicBrick.orientedBrick.orientedStudHeight);
						
						// if the bottom of the brick we are splitting does not end bellow the combined brick's bottom, it will be removed
						if (splitBottomBrickHeight > 0)
						{
							// make sure we have a brick that fits the split size
							splitBrick.posOrientedBrick = brickToSplitOrientedBrickHeights[splitBottomBrickHeight - 1];
							
							if (splitBrick.negOrientedBrick == null)
							{
								// there is no single brick that will fit the size
								// try combinations of two bricks
								// only need to try half the combinations as the second half is the first repeated but backwards
								// example:
								//  splitTopBrickHeight = 5;
								//  we have heights of 1,2,3,4,6
								//  possible combinations are 5,0 4,1 3,2 2,3 1,4 0,5
								//  skip 0 and try combinations 1 and 2
								int numCombinations = (int)(splitBottomBrickHeight * 0.5f + 0.5f); // rounded up
								
								boolean foundCombo = false;
								for (int diff = 1; diff < numCombinations; ++diff)
								{
									splitBrick.posOrientedBrick      = brickToSplitOrientedBrickHeights[splitBottomBrickHeight - numCombinations - 1];
									splitBrick.posExtraOrientedBrick = brickToSplitOrientedBrickHeights[numCombinations - 1];
									
									if (splitBrick.posOrientedBrick != null && splitBrick.posExtraOrientedBrick != null)
									{
										// found a combination
										foundCombo = true;
										break;
									}
								}
								
								if (!foundCombo)
								{
									// there is not a brick with the needed size to create the bottom split brick
									canSplitAllBricks = false;
									break;
								}
							}
						}
					}
					
					// if we could not split all the bricks in between, then we cannot combine the bricks
					// we also can not extend any further
					if (!canSplitAllBricks)
						break;
					
					
					// we can combine the bricks!
					System.out.println("================H==================");
					System.out.println("===================================");
					System.out.println(mosaicBrick);
					System.out.println("===================================");
					
					// split the bricks
					for (int i = 0; i < numSplitBricks; ++i)
					{
						MosaicSplitBrick splitBrick = splitBricks[i];
						
						// remove the original brick
						splitBrick.originalMosaicBrick.remove();
						mosaicBricks.remove(splitBrick.originalMosaicBrick);
						System.out.println("Removing original: " + splitBrick.originalMosaicBrick);
						
						
						// place top
						if (splitBrick.negOrientedBrick != null)
						{
							MosaicBrick topMosaicBrick = new MosaicBrick(
									splitBrick.negOrientedBrick,
									splitBrick.originalMosaicBrick.originStudX,
									splitBrick.originalMosaicBrick.originStudY,
									combinedMosaicBrick); // keep track of who split who
							
							// replace the studs with the new mosaic brick
							for (int studOffsetX = 0; studOffsetX < topMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
							{
								for (int studOffsetY = 0; studOffsetY < topMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
									mosaic[topMosaicBrick.originStudX + studOffsetX][topMosaicBrick.originStudY + studOffsetY] = topMosaicBrick;
							}
							
							// optimize this brick again
							bricksToOptimize.add(topMosaicBrick);
							
							// add the brick
							mosaicBricks.add(topMosaicBrick);
							System.out.println("Adding top: " + topMosaicBrick);
							
							
							
							// place top extra
							if (splitBrick.negExtraOrientedBrick != null)
							{
								MosaicBrick topExtraMosaicBrick = new MosaicBrick(
										splitBrick.negExtraOrientedBrick,
										splitBrick.originalMosaicBrick.originStudX,
										splitBrick.originalMosaicBrick.originStudY + splitBrick.negOrientedBrick.orientedStudHeight,
										combinedMosaicBrick); // keep track of who split who
								
								// replace the studs with the new mosaic brick
								for (int studOffsetX = 0; studOffsetX < topExtraMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
								{
									for (int studOffsetY = 0; studOffsetY < topExtraMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
										mosaic[topExtraMosaicBrick.originStudX + studOffsetX][topExtraMosaicBrick.originStudY + studOffsetY] = topExtraMosaicBrick;
								}
								
								// optimize this brick again
								bricksToOptimize.add(topExtraMosaicBrick);
								
								// add the brick
								mosaicBricks.add(topExtraMosaicBrick);
								System.out.println("Adding top extra: " + topExtraMosaicBrick);
							}
						}
						
						
						
						// place bottom
						if (splitBrick.posOrientedBrick != null)
						{
							MosaicBrick bottomMosaicBrick = new MosaicBrick(
									splitBrick.posOrientedBrick,
									splitBrick.originalMosaicBrick.originStudX,
									combinedMosaicBrick.originStudY + combinedMosaicBrick.orientedBrick.orientedStudHeight,
									combinedMosaicBrick); // keep track of who split who
							
							// replace the studs with the new mosaic brick
							for (int studOffsetX = 0; studOffsetX < bottomMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
							{
								for (int studOffsetY = 0; studOffsetY < bottomMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
									mosaic[bottomMosaicBrick.originStudX + studOffsetX][bottomMosaicBrick.originStudY + studOffsetY] = bottomMosaicBrick;
							}
							
							// optimize this brick again
							bricksToOptimize.add(bottomMosaicBrick);
							
							// add the brick
							mosaicBricks.add(bottomMosaicBrick);
							System.out.println("Adding bottom: " + bottomMosaicBrick);
							
							
							
							// place bottom extra
							if (splitBrick.posExtraOrientedBrick != null)
							{
								MosaicBrick bottomExtraMosaicBrick = new MosaicBrick(
										splitBrick.posExtraOrientedBrick,
										splitBrick.originalMosaicBrick.originStudX,
										combinedMosaicBrick.originStudY + combinedMosaicBrick.orientedBrick.orientedStudHeight + splitBrick.posOrientedBrick.orientedStudHeight,
										combinedMosaicBrick); // keep track of who split who
								
								// replace the studs with the new mosaic brick
								for (int studOffsetX = 0; studOffsetX < bottomExtraMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
								{
									for (int studOffsetY = 0; studOffsetY < bottomExtraMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
										mosaic[bottomExtraMosaicBrick.originStudX + studOffsetX][bottomExtraMosaicBrick.originStudY + studOffsetY] = bottomExtraMosaicBrick;
								}
								
								// optimize this brick again
								bricksToOptimize.add(bottomExtraMosaicBrick);
								
								// add the brick
								mosaicBricks.add(bottomExtraMosaicBrick);
								System.out.println("Adding bottom extra: " + bottomExtraMosaicBrick);
							}
						}
					}
					
					
					// combine the bricks
					// remove the originals
					mosaicBrick.remove();
					combiningMosaicBrick.remove();
					
					mosaicBricks.remove(mosaicBrick);
					mosaicBricks.remove(combiningMosaicBrick);
					System.out.println("Removing current: " + mosaicBrick);
					System.out.println("Removing combining: " + combiningMosaicBrick);
					
					
					// place the combined brick
					for (int studOffsetY = 0; studOffsetY < combinedMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
					{
						for (int studOffsetX = 0; studOffsetX < combinedMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
							mosaic[combinedMosaicBrick.originStudX + studOffsetX][combinedMosaicBrick.originStudY + studOffsetY] = combinedMosaicBrick;
					}
					
					// optimize this brick again
					bricksToOptimize.add(combinedMosaicBrick);
					
					// add the brick
					mosaicBricks.add(combinedMosaicBrick);
					System.out.println("Adding combined: " + combinedMosaicBrick);
					break;
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			// --------------------------------------------------------------------
			// Stretch Height
			
			
			
			
			
			// get the available heights for the brick's width
			// example:
			//  if the brick is a 1x2, the available heights are 1x1, 1x2, 1x3, 1x4, 1x6
			//  if the brick is a 2x4, the available heights are 2x1, 2x2, 2x3, 2x4, 2x6
			OrientedLegoBrick[] orientedBrickHeights = mosaicBrick.orientedBrick.brick.color.getBricksByStudWidth(mosaicBrick.orientedBrick.orientedStudWidth);
			int orientedBrickMaxHeight = mosaicBrick.orientedBrick.brick.color.getBricksByStudWidthMaxHeight(mosaicBrick.orientedBrick.orientedStudWidth);
			
			
			// start from one greater than the current height
			// studYOffset = height - 1;
			// height = orientedStudHeight + 1;
			// studYOffset = orientedStudHeight + 1 - 1;
			// studYOffset = orientedStudHeight;
			for (int checkStudOffsetY = mosaicBrick.orientedBrick.orientedStudHeight; checkStudOffsetY < orientedBrickMaxHeight; ++checkStudOffsetY)
			{
				int checkStudY = mosaicBrick.originStudY + checkStudOffsetY;
				
				// if we hit the edge, we can't extend further
				if (checkStudY >= mosaicStudHeight)
					break;
				
				// make sure they are the correct color
				boolean correctColor = true;
				for (int studOffsetX = 0; studOffsetX < mosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
				{
					if (mosaic[mosaicBrick.originStudX + studOffsetX][checkStudY].orientedBrick.brick.color != mosaicBrick.orientedBrick.brick.color)
					{
						correctColor = false;
						break;
					}
				}
				
				// if there is a stud with a different color brick on it, we can't extend any further
				if (!correctColor)
					break;
				
				
				// check if there is a brick we can combine with
				MosaicBrick combiningMosaicBrick = mosaic[mosaicBrick.originStudX][checkStudY];
				
				// check if
				//   both bricks have the same X origin (left sides are aligned)
				//   both bricks have the same width
				if (combiningMosaicBrick.originStudX == mosaicBrick.originStudX &&
					combiningMosaicBrick.orientedBrick.orientedStudWidth == mosaicBrick.orientedBrick.orientedStudWidth)
				{
					// check if there exists a brick that would cover the distance from the current brick's start
					// to the second brick's end
					int combiningBrickStudHeight = (combiningMosaicBrick.originStudY + combiningMosaicBrick.orientedBrick.orientedStudHeight) - mosaicBrick.originStudY;
					
					// make sure the width would not be longer than our max
					if (combiningBrickStudHeight > orientedBrickMaxHeight)
						continue;
					
					OrientedLegoBrick combiningOrientedBrick = orientedBrickHeights[combiningBrickStudHeight - 1];
					
					// if there is not a brick with the needed size to combine the bricks, we cannot combine
					if (combiningOrientedBrick == null)
						continue;
					
					
					// we should be able to combine these bricks as long as we can split the bricks in between
					// create the combined brick
					MosaicBrick combinedMosaicBrick = new MosaicBrick(combiningOrientedBrick, mosaicBrick.originStudX, mosaicBrick.originStudY);
					
					int betweenBricksStudHeight = combiningMosaicBrick.originStudY - (mosaicBrick.originStudY + mosaicBrick.orientedBrick.orientedStudHeight);
					
					
					// create a list of all the bricks that need to be split
					MosaicSplitBrick[] splitBricks = new MosaicSplitBrick[betweenBricksStudHeight * mosaicBrick.orientedBrick.orientedStudWidth];
					int numSplitBricks = 0;
					
					for (int studOffsetX = 0; studOffsetX < mosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
					{
						for (int studOffsetY = 0; studOffsetY < betweenBricksStudHeight; ++studOffsetY)
						{
							MosaicBrick brickToSplit = mosaic[mosaicBrick.originStudX + studOffsetX][mosaicBrick.originStudY + mosaicBrick.orientedBrick.orientedStudHeight + studOffsetY];
							
							// check if we already have this brick in our list
							boolean duplicate = false;
							for (int i = 0; i < numSplitBricks; ++i)
							{
								if (splitBricks[i].originalMosaicBrick == brickToSplit)
								{
									duplicate = true;
									break;
								}
							}
							
							if (!duplicate)
								splitBricks[numSplitBricks++] = new MosaicSplitBrick(brickToSplit);
						}
					}
					
					
					// make sure we can split all the bricks in between
					boolean canSplitAllBricks = true;
					for (int i = 0; i < numSplitBricks; ++i)
					{
						MosaicSplitBrick splitBrick = splitBricks[i];
						
						// if the current brick or the combining brick has been split by this brick, we cannot split it
						if (mosaicBrick.splitter == splitBrick.originalMosaicBrick || combiningMosaicBrick.splitter == splitBrick.originalMosaicBrick)
						{
							canSplitAllBricks = false;
							break;
						}
						
						// get the available widths for the brick we are splitting
						OrientedLegoBrick[] brickToSplitOrientedBrickWidths = splitBrick.originalMosaicBrick.orientedBrick.brick.color.getBricksByStudHeight(splitBrick.originalMosaicBrick.orientedBrick.orientedStudHeight);
						
						
						// split left
						int splitLeftBrickWidth = combinedMosaicBrick.originStudX - splitBrick.originalMosaicBrick.originStudX;
						
						// if the left side of the brick we are splitting does not start to the left of the combined brick's left side, it will be removed
						if (splitLeftBrickWidth > 0)
						{
							// make sure we have a brick or two bricks that can fit the split size
							splitBrick.negOrientedBrick = brickToSplitOrientedBrickWidths[splitLeftBrickWidth - 1];
							
							if (splitBrick.negOrientedBrick == null)
							{
								// there is no single brick that will fit the size
								// try combinations of two bricks
								// only need to try half the combinations as the second half is the first repeated but backwards
								// example:
								//  splitTopBrickWidth = 5;
								//  we have widths of 1,2,3,4,6
								//  possible combinations are 5,0 4,1 3,2 2,3 1,4 0,5
								//  skip 0 and try combinations 1 and 2
								int numCombinations = (int)(splitLeftBrickWidth * 0.5f + 0.5f); // rounded up
								
								boolean foundCombo = false;
								for (int diff = 1; diff < numCombinations; ++diff)
								{
									splitBrick.negOrientedBrick      = brickToSplitOrientedBrickWidths[splitLeftBrickWidth - numCombinations - 1];
									splitBrick.negExtraOrientedBrick = brickToSplitOrientedBrickWidths[numCombinations - 1];
									
									if (splitBrick.negOrientedBrick != null && splitBrick.negExtraOrientedBrick != null)
									{
										// found a combination
										foundCombo = true;
										break;
									}
								}
								
								if (!foundCombo)
								{
									// there is not a brick with the needed size to create the top split brick
									canSplitAllBricks = false;
									break;
								}
							}
						}
						
						
						// split right
						int splitRightBrickWidth = (splitBrick.originalMosaicBrick.originStudX + splitBrick.originalMosaicBrick.orientedBrick.orientedStudWidth) - (combinedMosaicBrick.originStudX + combinedMosaicBrick.orientedBrick.orientedStudWidth);
						
						// if the bottom of the brick we are splitting does not end bellow the combined brick's bottom, it will be removed
						if (splitRightBrickWidth > 0)
						{
							// make sure we have a brick that fits the split size
							splitBrick.posOrientedBrick = brickToSplitOrientedBrickWidths[splitRightBrickWidth - 1];
							
							if (splitBrick.negOrientedBrick == null)
							{
								// there is no single brick that will fit the size
								// try combinations of two bricks
								// only need to try half the combinations as the second half is the first repeated but backwards
								// example:
								//  splitTopBrickWidth = 5;
								//  we have widths of 1,2,3,4,6
								//  possible combinations are 5,0 4,1 3,2 2,3 1,4 0,5
								//  skip 0 and try combinations 1 and 2
								int numCombinations = (int)(splitRightBrickWidth * 0.5f + 0.5f); // rounded up
								
								boolean foundCombo = false;
								for (int diff = 1; diff < numCombinations; ++diff)
								{
									splitBrick.posOrientedBrick      = brickToSplitOrientedBrickWidths[splitRightBrickWidth - numCombinations - 1];
									splitBrick.posExtraOrientedBrick = brickToSplitOrientedBrickWidths[numCombinations - 1];
									
									if (splitBrick.posOrientedBrick != null && splitBrick.posExtraOrientedBrick != null)
									{
										// found a combination
										foundCombo = true;
										break;
									}
								}
								
								if (!foundCombo)
								{
									// there is not a brick with the needed size to create the bottom split brick
									canSplitAllBricks = false;
									break;
								}
							}
						}
					}
					
					// if we could not split all the bricks in between, then we cannot combine the bricks
					// we also can not extend any further
					if (!canSplitAllBricks)
						break;
					
					
					// we can combine the bricks!
					System.out.println("================V==================");
					System.out.println("===================================");
					System.out.println(mosaicBrick);
					System.out.println("===================================");
					
					// split the bricks
					for (int i = 0; i < numSplitBricks; ++i)
					{
						MosaicSplitBrick splitBrick = splitBricks[i];
						
						// remove the original brick
						splitBrick.originalMosaicBrick.remove();
						mosaicBricks.remove(splitBrick.originalMosaicBrick);
						System.out.println("Removing original: " + splitBrick.originalMosaicBrick);
						
						
						// place left
						if (splitBrick.negOrientedBrick != null)
						{
							MosaicBrick leftMosaicBrick = new MosaicBrick(
									splitBrick.negOrientedBrick,
									splitBrick.originalMosaicBrick.originStudX,
									splitBrick.originalMosaicBrick.originStudY,
									combinedMosaicBrick); // keep track of who split who
							
							// replace the studs with the new mosaic brick
							for (int studOffsetY = 0; studOffsetY < leftMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
							{
								for (int studOffsetX = 0; studOffsetX < leftMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
									mosaic[leftMosaicBrick.originStudX + studOffsetX][leftMosaicBrick.originStudY + studOffsetY] = leftMosaicBrick;
							}
							
							// optimize this brick again
							bricksToOptimize.add(leftMosaicBrick);
							
							// add the brick
							mosaicBricks.add(leftMosaicBrick);
							System.out.println("Adding left: " + leftMosaicBrick);
							
							
							
							// place left extra
							if (splitBrick.negExtraOrientedBrick != null)
							{
								MosaicBrick leftExtraMosaicBrick = new MosaicBrick(
										splitBrick.negExtraOrientedBrick,
										splitBrick.originalMosaicBrick.originStudX + splitBrick.negOrientedBrick.orientedStudWidth,
										splitBrick.originalMosaicBrick.originStudY,
										combinedMosaicBrick); // keep track of who split who
								
								// replace the studs with the new mosaic brick
								for (int studOffsetY = 0; studOffsetY < leftExtraMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
								{
									for (int studOffsetX = 0; studOffsetX < leftExtraMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
										mosaic[leftExtraMosaicBrick.originStudX + studOffsetX][leftExtraMosaicBrick.originStudY + studOffsetY] = leftExtraMosaicBrick;
								}
								
								// optimize this brick again
								bricksToOptimize.add(leftExtraMosaicBrick);
								
								// add the brick
								mosaicBricks.add(leftExtraMosaicBrick);
								System.out.println("Adding left extra: " + leftExtraMosaicBrick);
							}
						}
						
						
						
						// place right
						if (splitBrick.posOrientedBrick != null)
						{
							MosaicBrick rightMosaicBrick = new MosaicBrick(
									splitBrick.posOrientedBrick,
									combinedMosaicBrick.originStudX + combinedMosaicBrick.orientedBrick.orientedStudWidth,
									splitBrick.originalMosaicBrick.originStudY,
									combinedMosaicBrick); // keep track of who split who
							
							// replace the studs with the new mosaic brick
							for (int studOffsetY = 0; studOffsetY < rightMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
							{
								for (int studOffsetX = 0; studOffsetX < rightMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
									mosaic[rightMosaicBrick.originStudX + studOffsetX][rightMosaicBrick.originStudY + studOffsetY] = rightMosaicBrick;
							}
							
							// optimize this brick again
							bricksToOptimize.add(rightMosaicBrick);
							
							// add the brick
							mosaicBricks.add(rightMosaicBrick);
							System.out.println("Adding right: " + rightMosaicBrick);
							
							
							
							// place right extra
							if (splitBrick.posExtraOrientedBrick != null)
							{
								MosaicBrick rightExtraMosaicBrick = new MosaicBrick(
										splitBrick.posExtraOrientedBrick,
										combinedMosaicBrick.originStudX + combinedMosaicBrick.orientedBrick.orientedStudWidth + splitBrick.posOrientedBrick.orientedStudWidth,
										splitBrick.originalMosaicBrick.originStudY,
										combinedMosaicBrick); // keep track of who split who
								
								// replace the studs with the new mosaic brick
								for (int studOffsetY = 0; studOffsetY < rightExtraMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
								{
									for (int studOffsetX = 0; studOffsetX < rightExtraMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
										mosaic[rightExtraMosaicBrick.originStudX + studOffsetX][rightExtraMosaicBrick.originStudY + studOffsetY] = rightExtraMosaicBrick;
								}
								
								// optimize this brick again
								bricksToOptimize.add(rightExtraMosaicBrick);
								
								// add the brick
								mosaicBricks.add(rightExtraMosaicBrick);
								System.out.println("Adding right extra: " + rightExtraMosaicBrick);
							}
						}
					}
					
					
					// combine the bricks
					// remove the originals
					mosaicBrick.remove();
					combiningMosaicBrick.remove();
					
					mosaicBricks.remove(mosaicBrick);
					mosaicBricks.remove(combiningMosaicBrick);
					System.out.println("Removing current: " + mosaicBrick);
					System.out.println("Removing combining: " + combiningMosaicBrick);
					
					
					// place the combined brick
					for (int studOffsetX = 0; studOffsetX < combinedMosaicBrick.orientedBrick.orientedStudWidth; ++studOffsetX)
					{
						for (int studOffsetY = 0; studOffsetY < combinedMosaicBrick.orientedBrick.orientedStudHeight; ++studOffsetY)
							mosaic[combinedMosaicBrick.originStudX + studOffsetX][combinedMosaicBrick.originStudY + studOffsetY] = combinedMosaicBrick;
					}
					
					// optimize this brick again
					bricksToOptimize.add(combinedMosaicBrick);
					
					// add the brick
					mosaicBricks.add(combinedMosaicBrick);
					System.out.println("Adding combined: " + combinedMosaicBrick);
					break;
				}
			}
		}
		
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
