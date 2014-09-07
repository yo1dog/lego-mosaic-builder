package net.awesomebox.legoMosaicBuilder;

import net.awesomebox.legoMosaicBuilder.lego.OrientedLegoBrick;

// represents an instance of a Lego brick used in a mosaic
public final class MosaicBrick
{
	public final OrientedLegoBrick orientedBrick;
	public final int originStudX;
	public final int originStudY;
	public final MosaicBrick splitter;
	
	private boolean removed;
	private boolean level3Optimized;
	
	
	public MosaicBrick(OrientedLegoBrick orientedBrick, int originStudX, int originStudY)
	{
		this(orientedBrick, originStudX, originStudY, null);
	}
	public MosaicBrick(OrientedLegoBrick orientedBrick, int originStudX, int originStudY, MosaicBrick splitter)
	{
		this.orientedBrick = orientedBrick;
		this.originStudX = originStudX;
		this.originStudY = originStudY;
		this.splitter = splitter;
		
		removed = false;
		level3Optimized = false;
	}
	
	
	public void remove()
	{
		removed = true;
	}
	public boolean wasRemoved()
	{
		return removed;
	}
	
	
	public void level3Optimize()
	{
		level3Optimized = true;
	}
	public boolean wasLevel3Optimized()
	{
		return level3Optimized;
	}
	
	
	public String toString()
	{
		return
			super.toString() + "\n" + 
			this.originStudX + ", " + this.originStudY + "\n" +
			this.orientedBrick.orientedStudWidth + " x " + this.orientedBrick.orientedStudHeight + "\n" +
			"splitter: " + String.valueOf(splitter) + "\n" +
			"-------------";
			
	}
}
