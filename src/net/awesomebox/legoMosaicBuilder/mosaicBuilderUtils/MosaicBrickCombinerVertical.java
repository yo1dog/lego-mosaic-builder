package net.awesomebox.legoMosaicBuilder.mosaicBuilderUtils;

import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.MosaicBrick;
import net.awesomebox.legoMosaicBuilder.MosaicSplitBrick;
import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;

abstract class MosaicBrickCombinerVertical
{
	public final static boolean combine(MosaicBrick mosaicBrick, MosaicBrick[][] mosaic, int mosaicStudHeight, ArrayList<MosaicBrick> mosaicBricks, ArrayList<MosaicBrick> bricksToOptimize)
	{
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
				
				
				// when splitting bricks, don't create more bricks
				// start with -1 because we are combining two bricks into one (removing one brick)
				int netNumBricks = -1;
				
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
					
					
					// we first remove this brick
					--netNumBricks;
					
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
					
					
					if (splitBrick.negOrientedBrick != null)
						++netNumBricks;
					if (splitBrick.negExtraOrientedBrick != null)
						++netNumBricks;
					if (splitBrick.posOrientedBrick != null)
						++netNumBricks;
					if (splitBrick.posExtraOrientedBrick != null)
						++netNumBricks;
					
					if (netNumBricks > 0)
					{
						// splitting these bricks will require creating more bricks
						canSplitAllBricks = false;
						break;
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
				
				// optimize the combined brick again
				bricksToOptimize.add(combinedMosaicBrick);
				
				// add the brick
				mosaicBricks.add(combinedMosaicBrick);
				System.out.println("Adding combined: " + combinedMosaicBrick);
				return true;
			}
		}
		
		return false;
	}
}
