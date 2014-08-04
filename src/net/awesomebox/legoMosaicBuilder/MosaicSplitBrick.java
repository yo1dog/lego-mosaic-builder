package net.awesomebox.legoMosaicBuilder;

import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;

// represents a brick that has been split and will be replaced by it's split bricks
public final class MosaicSplitBrick
{
	public final MosaicBrick originalMosaicBrick;
	public OrientedLegoBrick topOrientedBrick;
	public OrientedLegoBrick topExtraOrientedBrick;
	public OrientedLegoBrick bottomOrientedBrick;
	public OrientedLegoBrick bottomExtraOrientedBrick;
	
	public MosaicSplitBrick(MosaicBrick originalMosaicBrick)
	{
		this.originalMosaicBrick = originalMosaicBrick;
	}
}
