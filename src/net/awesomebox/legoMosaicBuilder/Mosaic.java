package net.awesomebox.legoMosaicBuilder;

public final class Mosaic
{
	private MosaicBrick[] mosaicBricks;
	
	public Mosaic(MosaicBrick[] mosaicBricks)
	{
		this.mosaicBricks = mosaicBricks;
	}
	
	public MosaicBrick[] getMosaicBricks()
	{
		return mosaicBricks;
	}
}
