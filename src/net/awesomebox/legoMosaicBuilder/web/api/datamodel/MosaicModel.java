package net.awesomebox.legoMosaicBuilder.web.api.datamodel;

import net.awesomebox.legoMosaicBuilder.builder.Mosaic;

public class MosaicModel
{
	public final int studWidth;
	public final int studHeight;
	public final MosaicBrickModel[] mosaicBricks;
	
	public MosaicModel(Mosaic mosaic)
	{
		studWidth  = mosaic.studWidth;
		studHeight = mosaic.studHeight;
		
		mosaicBricks = new MosaicBrickModel[mosaic.mosaicBricks.length];
		
		for (int i = 0; i < mosaic.mosaicBricks.length; ++i)
			mosaicBricks[i] = new MosaicBrickModel(mosaic.mosaicBricks[i]);
	}
}
