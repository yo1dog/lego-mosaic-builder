package net.awesomebox.legoMosaicBuilder.mosaicOptimizers;

import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.MosaicBrick;
import net.awesomebox.legoMosaicBuilder.mosaicBuilderUtils.MosaicBrickCombiner;

//=============================================================================
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

public final class MosaicOptimizerLevel2
{
	public static final void optimize(MosaicBrick[][] mosaic, int mosaicStudWidth, int mosaicStudHeight, ArrayList<MosaicBrick> mosaicBricks)
	{
		// create a list of the bricks to optimize
		ArrayList<MosaicBrick> bricksToOptimize = new ArrayList<MosaicBrick>(mosaicBricks);
		
		for (int mosaicBrickIndex = 0; mosaicBrickIndex < bricksToOptimize.size(); ++mosaicBrickIndex)
		{
			MosaicBrick mosaicBrick = bricksToOptimize.get(mosaicBrickIndex);
			
			if (mosaicBrick.wasRemoved())
				continue;
			
			MosaicBrickCombiner.combine(mosaicBrick, mosaic, mosaicStudWidth, mosaicStudHeight, mosaicBricks, bricksToOptimize);
		}
	}
}
