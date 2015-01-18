package net.awesomebox.legoMosaicBuilder.builder;

import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;

// represents a brick that has been split and will be replaced by it's split bricks
public final class MosaicSplitBrick
{
	public final MosaicBrick originalMosaicBrick;
	public OrientedLegoBrick negOrientedBrick;
	public OrientedLegoBrick negExtraOrientedBrick;
	public OrientedLegoBrick posOrientedBrick;
	public OrientedLegoBrick posExtraOrientedBrick;
	
	public MosaicSplitBrick(MosaicBrick originalMosaicBrick)
	{
		this.originalMosaicBrick = originalMosaicBrick;
	}
}
