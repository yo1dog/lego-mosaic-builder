package net.awesomebox.legoMosaicBuilder.lego;

import java.awt.Color;

public final class LegoShape 
{
	public final int id;
	public final String name;
	public final String friendlyName;
	public final int studWidth;
	public final int studHeight;
	
	public final Color debugColor;
	
	transient LegoBrick[] bricks = new LegoBrick[0];
	
	LegoShape(int id, String name, String friendlyName, int studWidth, int studHeight, Color debugColor)
	{
		this.id           = id;
		this.name         = name;
		this.friendlyName = friendlyName;
		this.studWidth    = studWidth;
		this.studHeight   = studHeight;
		
		this.debugColor = debugColor;
	}
	
	public final LegoBrick[] getBricks()
	{
		return bricks;
	}
	
	
	
	
	
	
	
	
	
	
	
	// IMPORTANT: we assume the list of bricks for a color is ordered from largest shape to smallest
	public static final LegoShape _2x6 = new LegoShape(0,    "brick 2x6", "2x6", 2,6, new Color(255, 0,   0  ));
	public static final LegoShape _1x6 = new LegoShape(0,    "brick 1x6", "1x6", 1,6, new Color(255, 127, 0  ));
	public static final LegoShape _2x4 = new LegoShape(3001, "brick 2x4", "2x4", 2,4, new Color(255, 255, 0  ));
	public static final LegoShape _1x4 = new LegoShape(0,    "brick 1x4", "1x4", 1,4, new Color(0,   255, 0  ));
	public static final LegoShape _2x3 = new LegoShape(3002, "brick 2x3", "2x3", 2,3, new Color(0,   255, 127));
	public static final LegoShape _1x3 = new LegoShape(0,    "brick 1x3", "1x3", 1,3, new Color(0,   255, 255));
	public static final LegoShape _2x2 = new LegoShape(3003, "brick 2x2", "2x2", 2,2, new Color(0,   0,   255));
	public static final LegoShape _1x2 = new LegoShape(3004, "brick 1x2", "1x2", 1,2, new Color(127, 0,   255));
	public static final LegoShape _1x1 = new LegoShape(3005, "brick 1x1", "1x1", 1,1, new Color(255, 0,   255));
	
	public static final LegoShape[] allShapes =
	{
		_2x6, _1x6, _2x4, _1x4, _2x3, _1x3, _2x2, _1x2, _1x1
	};
}
