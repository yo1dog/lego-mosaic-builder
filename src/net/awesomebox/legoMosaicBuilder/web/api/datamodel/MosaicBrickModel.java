package net.awesomebox.legoMosaicBuilder.web.api.datamodel;

import net.awesomebox.legoMosaicBuilder.builder.MosaicBrick;

public class MosaicBrickModel
{
	public final int     originStudX;
	public final int     originStudY;
	public final boolean rotated;
	public final int     brickID;
	
	public final int     studWidth;
	public final int     studHeight;
	
	public final String  color;
	
	public MosaicBrickModel(MosaicBrick mosaicBrick)
	{
		originStudX = mosaicBrick.originStudX;
		originStudY = mosaicBrick.originStudY;
		rotated     = mosaicBrick.orientedBrick.rotated;
		brickID     = mosaicBrick.orientedBrick.brick.id;
		
		studWidth   = mosaicBrick.orientedBrick.brick.shape.studWidth;
		studHeight  = mosaicBrick.orientedBrick.brick.shape.studHeight;
		
		color = mosaicBrick.orientedBrick.brick.color.colorHexString;
	}
}
