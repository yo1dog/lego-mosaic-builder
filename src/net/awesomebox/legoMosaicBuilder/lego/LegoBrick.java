package net.awesomebox.legoMosaicBuilder.lego;


public class LegoBrick
{
	public final int id;
	public final LegoShape shape;
	public final LegoColor color;
	
	public final transient OrientedLegoBrick orientedBrickNonRotated;
	public final transient OrientedLegoBrick orientedBrickRotated;
	
	LegoBrick(int id, LegoShape shape, LegoColor color)
	{
		this.id = id;
		this.shape = shape;
		this.color = color;
		
		orientedBrickNonRotated = new OrientedLegoBrick(this, false);
		orientedBrickRotated    = new OrientedLegoBrick(this, true);
	}
}
