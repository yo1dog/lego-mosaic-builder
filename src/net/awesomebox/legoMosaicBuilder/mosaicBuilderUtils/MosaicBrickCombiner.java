package net.awesomebox.legoMosaicBuilder.mosaicBuilderUtils;

import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.builder.MosaicBrick;

public final class MosaicBrickCombiner
{
	public static final boolean combine(MosaicBrick mosaicBrick, MosaicBrick[][] mosaic, int mosaicStudWidth, int mosaicStudHeight, ArrayList<MosaicBrick> mosaicBricks, ArrayList<MosaicBrick> bricksToOptimize)
	{
		boolean result = MosaicBrickCombinerHorizontal.combine(mosaicBrick, mosaic, mosaicStudWidth, mosaicBricks, bricksToOptimize);
		
		if (result)
			return true;
		
		result = MosaicBrickCombinerVertical.combine(mosaicBrick, mosaic, mosaicStudHeight, mosaicBricks, bricksToOptimize);
		
		return result;
	}
}
