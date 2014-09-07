package net.awesomebox.legoMosaicBuilder.mosaicOptimizers;

import java.util.ArrayList;
import java.util.Arrays;

import net.awesomebox.legoMosaicBuilder.MosaicBrick;
import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;
import net.awesomebox.legoMosaicBuilder.mosaicBuilderUtils.MosaicBrickCombiner;

//=============================================================================
// =============================================================================
//
// Optimize Level 3
//
// =============================================================================
// 
// Switches brick placement and checks for possible combinations with the new
// order.
// 
// We iterate through each brick and check if there are any other bricks that
// the current brick could switch with. For example. If a 1x3 and a 1x2 were
// used to fill a 1x5 space, we should switch their order and check if there are
// combinations possible in the new order.
// 
// =============================================================================
// =============================================================================
public final class MosaicOptimizerLevel3
{
	public static final void optimize(MosaicBrick[][] mosaic, int mosaicStudWidth, int mosaicStudHeight, ArrayList<MosaicBrick> mosaicBricks)
	{
		// create a list of the bricks to optimize
		ArrayList<MosaicBrick> bricksToOptimize = new ArrayList<MosaicBrick>(mosaicBricks);
		
		for (int mosaicBrickIndex = 0; mosaicBrickIndex < bricksToOptimize.size(); ++mosaicBrickIndex)
		{
			MosaicBrick mosaicBrick = bricksToOptimize.get(mosaicBrickIndex);
			
			if (mosaicBrick.wasRemoved() || mosaicBrick.wasLevel3Optimized())
				continue;
			
			
			// get the number of studWidths 
			// find the sequence of bricks that this brick can swap places with
			// keep track of the counts of each stud width
			int maxStudWidth = mosaicBrick.orientedBrick.brick.color.getBricksByStudHeightMaxWidth(mosaicBrick.orientedBrick.orientedStudHeight);
			
			int[] studWidthCounts = new int[maxStudWidth]; // studWidthCounts[studWidth - 1] = number of bricks in the sequence with the given studWidth
			
			// increment the starting brick's stud width
			studWidthCounts[mosaicBrick.orientedBrick.orientedStudWidth] = 1;
			
			// keep track if all the bricks are the same length
			boolean allBricksSameLength = true;
			
			
			// find the start of the sequence
			int startStudX = mosaicBrick.originStudX;
			while (startStudX > 0)
			{
				// get the previous brick
				MosaicBrick previousBrick = mosaic[startStudX - 1][mosaicBrick.originStudY];
				
				// for bricks to be in a sequence:
				//  their top's must be aligned
				//  they must have the same height
				//  they must be the same color
				if (previousBrick.originStudY                      != mosaicBrick.originStudY ||
					previousBrick.orientedBrick.orientedStudHeight != mosaicBrick.orientedBrick.orientedStudHeight ||
					previousBrick.orientedBrick.brick.color        != mosaicBrick.orientedBrick.brick.color)
					break;
				
				// set the start to the previous brick's origin
				startStudX = previousBrick.originStudX;
				
				// increment the count for the previous brick's width
				++studWidthCounts[previousBrick.orientedBrick.orientedStudWidth];
				
				// check if the previous brick's width is the same as the starting brick's
				if (previousBrick.orientedBrick.orientedStudWidth != mosaicBrick.orientedBrick.orientedStudWidth)
					allBricksSameLength = false;
			}
			
			
			// find the end of the sequence
			int endStudX = mosaicBrick.originStudX + mosaicBrick.orientedBrick.orientedStudWidth;
			while (endStudX < mosaicStudWidth)
			{
				// get the next brick
				MosaicBrick nextBrick = mosaic[endStudX][mosaicBrick.originStudY];
				
				// for bricks to be in a sequence:
				//  their top's must be aligned
				//  they must have the same height
				//  they must be the same color
				if (nextBrick.originStudY                      != mosaicBrick.originStudY ||
					nextBrick.orientedBrick.orientedStudHeight != mosaicBrick.orientedBrick.orientedStudHeight ||
					nextBrick.orientedBrick.brick.color        != mosaicBrick.orientedBrick.brick.color)
					break;
				
				// set the end to the next brick's end
				endStudX = nextBrick.originStudX + nextBrick.orientedBrick.orientedStudWidth;
				
				// increment the count for the next brick's width
				++studWidthCounts[nextBrick.orientedBrick.orientedStudWidth];
				
				// check if the next brick's width is the same as the starting brick's
				if (nextBrick.orientedBrick.orientedStudWidth != mosaicBrick.orientedBrick.orientedStudWidth)
					allBricksSameLength = false;
			}
			
			
			// if all the bricks in the sequence are the same width there is no point in swapping
			if (allBricksSameLength)
				continue;
			
			
			ArrayList<Integer> studXOffsets = new ArrayList<Integer>();
			
			// for each stud width in the sequence...
			for (int i = 0; i < studWidthCounts.length; ++i)
			{
				if (studWidthCounts[i] < 1)
					continue;
				
				// check all possible places a brick with the current width can be placed by checking all possible offsets
				// for example, if a sequence is made up of 4 bricks with widths of 2, 3, 6, and 6
				// the possible sequence positions : offsets for (2) are
				// sequence          : offset
				// (2) ...           : 0
				// 3, (2) ...        : 3
				// 6, (2) ...        : 8
				// 3, 6, (2) ...     : 9
				// 6, 6, (2) ...     : 12
				// 3, 6, 6, (2) ...  : 15
				// 
				// the possible sequence positions : offsets for (6) are
				// sequence          : offset
				// (6) ...           : 0
				// 2, (6) ...        : 2
				// 3, (6) ...        : 3
				// 2, 3, (6) ...     : 5
				// 6, (6) ...        : 6
				// 2, 6, (6) ...     : 8
				// 3, 6, (6) ...     : 9
				// 2, 3, 6, (6) ...  : 11
				//
				// we will check for combinations for a brick with the current width at each of these offsets
				
				// account for current width
				// we can use one less than the total count for the current width
				// for example, if we are placing a width of 6, and there are four bricks with widths of 6 in the sequence,
				// we can only have three bricks with a width of 6 in front of the brick we are placing
				--studWidthCounts[i];
				
				permute(studWidthCounts, studXOffsets, 0, i);
				
				// add back the count we accounted for earlier
				++studWidthCounts[i];
			}
		}
	}
	
	
	private static final void permute(int[] studWidthCounts, ArrayList<Integer> studXOffsets, int previousStudXOffset, int widthIndex)
	{
		int width = widthIndex + 1;
		int numWidths = studWidthCounts[widthIndex];
		
		if (numWidths == 0)
			return;
		
		int studXOffset = previousStudXOffset;
		for (int c = 0; c < numWidths; ++c)
		{
			// add the offset for this width
			studXOffset += width;
			addStuddXOffset(studXOffsets, studXOffset);
			
			// permute for each width after this one
			for (int i = widthIndex + 1; i < studWidthCounts.length; ++i)
				permute(studWidthCounts, studXOffsets, studXOffset, i);
		}
	}
	
	
	private static final void addStuddXOffset(ArrayList<Integer> studXOffsets, int studXOffset)
	{
		// make sure the offset does not already exist
		boolean exists = false;
		for (Integer existingStudXOffset : studXOffsets)
		{
			if (existingStudXOffset == studXOffset)
			{
				exists = true;
				break;
			}
		}
		
		if (!exists)
			studXOffsets.add(studXOffset);
	}
	
	
	
	
	
	// test
	public static void main(String[] args)
	{
		int[] studWidthCounts = {0, 1, 1, 0, 0, 2};
		
		for (int i = 0; i < studWidthCounts.length; ++i)
		{
			if (studWidthCounts[i] == 0)
				continue;
			
			ArrayList<Integer> studXOffsets = new ArrayList<Integer>();
			addStuddXOffset(studXOffsets, 0);
			
			--studWidthCounts[i];
			
			for (int j = 0; j < studWidthCounts.length; ++j)
				permute(studWidthCounts, studXOffsets, 0, j);
			
			++studWidthCounts[i];
			
			System.out.println("----- " + (i + 1) + " -----");
			for (int j = 0; j < studXOffsets.size(); ++j)
				System.out.println(studXOffsets.get(j));
		}
	}
}
