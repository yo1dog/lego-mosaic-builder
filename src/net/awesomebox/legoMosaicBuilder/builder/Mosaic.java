package net.awesomebox.legoMosaicBuilder.builder;

public final class Mosaic
{
	public final int studWidth;
	public final int studHeight;
	public final MosaicBrick[] mosaicBricks;
	
	public Mosaic(MosaicBrick[] mosaicBricks, int studWidth, int studHeight)
	{
		this.studWidth = studWidth;
		this.studHeight = studHeight;
		this.mosaicBricks = mosaicBricks;
	}
}
