package net.awesomebox.legoMosaicBuilder.lego;


public class OrientedLegoBrick
{
	public final LegoBrick brick;
	public final boolean rotated;
	
	public final transient int orientedStudWidth;
	public final transient int orientedStudHeight;
	
	OrientedLegoBrick(LegoBrick brick, boolean rotated)
	{
		this.brick = brick;
		this.rotated = rotated;
		
		if (rotated)
		{
			orientedStudWidth  = brick.shape.studHeight;
			orientedStudHeight = brick.shape.studWidth;
		}
		else
		{
			orientedStudWidth  = brick.shape.studWidth;
			orientedStudHeight = brick.shape.studHeight;
		}
	}
}
