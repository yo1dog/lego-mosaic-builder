package net.awesomebox.legoMosaicBuilder.mosaicBuilderUtils;

import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.MosaicBrick;
import net.awesomebox.legoMosaicBuilder.MosaicSplitBrick;
import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;

final class MosaicBrickCombinerHorizontal
{
	final static boolean combine(MosaicBrick mosaicBrick, MosaicBrick[][] mosaic, int mosaicStudWidth, ArrayList<MosaicBrick> mosaicBricks, ArrayList<MosaicBrick> bricksToOptimize)
	{
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
					
					// get the available heights for the brick we are splitting
					OrientedLegoBrick[] brickToSplitOrientedBrickHeights = splitBrick.originalMosaicBrick.orientedBrick.brick.color.getBricksByStudWidth(splitBrick.originalMosaicBrick.orientedBrick.orientedStudWidth);
					
					
					// we first remove this brick
					--netNumBricks;
					
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
