package net.awesomebox.legoMosaicBuilder.lego;

import java.awt.Color;


public class LegoColor
{
	public final int id;
	public final String friendlyName;
	public final int r;
	public final int g;
	public final int b;
	public final Color color;
	
	// IMPORTANT: we assume bricks are ordered from largest to smallest
	transient LegoBrick[] bricks = new LegoBrick[0];
	transient OrientedLegoBrick[][] bricksByStudWidth  = new OrientedLegoBrick[0][0]; // bricksByStudWidth [width  - 1] = list of bricks with the given width
	transient OrientedLegoBrick[][] bricksByStudHeight = new OrientedLegoBrick[0][0]; // bricksByStudHeight[height - 1] = list of bricks with the given height
	transient int[] bricksByStudWidthMaxHeight = new int[0]; // bricksByStudWidthMaxHeight[width  - 1] = max height of the bricks with the given width
	transient int[] bricksByStudHeightMaxWidth = new int[0]; // bricksByStudHeightMaxWidth[height - 1] = max width  of the bricks with the given height
	
	LegoColor(int id, String friendlyName, int r, int g, int b)
	{
		this.id = id;
		this.friendlyName = friendlyName;
		this.r = r;
		this.g = g;
		this.b = b;
		
		color = new Color(r, g, b);
	}
	
	public final LegoBrick[] getBricks()
	{
		return bricks;
	}
	
	public final LegoBrick get1x1Brick()
	{
		// IMPORTANT: we assume the list of bricks for a color is ordered from largest shape to smallest
		// so we assume the last brick in the list is the 1x1
		return bricks[bricks.length - 1];
	}
	
	public final OrientedLegoBrick[] getBricksByStudWidth(int studWidth)
	{
		return bricksByStudWidth[studWidth - 1];
	}
	public final OrientedLegoBrick[] getBricksByStudHeight(int studHeight)
	{
		return bricksByStudHeight[studHeight - 1];
	}
	
	public final int getBricksByStudWidthMaxHeight(int studWidth)
	{
		return bricksByStudWidthMaxHeight[studWidth - 1];
	}
	public final int getBricksByStudHeightMaxWidth(int studHeight)
	{
		return bricksByStudHeightMaxWidth[studHeight - 1];
	}
	
	
	
	
	
	
	
	
	
	
	public static final LegoColor Black                 = new LegoColor(26,  "Black",                   0x05, 0x13, 0x1D);
	public static final LegoColor BrightBlue            = new LegoColor(23,  "Bright Blue",             0x00, 0x55, 0xBF);
	public static final LegoColor DarkGreen             = new LegoColor(28,  "Dark Green",              0x25, 0x7A, 0x3E);
	public static final LegoColor BrightBluishGreen     = new LegoColor(107, "Bright Bluish Green",     0x00, 0x83, 0x8F);
	public static final LegoColor BrightRed             = new LegoColor(21,  "Bright Red",              0xC9, 0x1A, 0x09);
	public static final LegoColor BrightPurple          = new LegoColor(221, "Bright Purple",           0xC8, 0x70, 0xA0);
	public static final LegoColor Brown                 = new LegoColor(217, "Brown",                   0x58, 0x39, 0x27);
	public static final LegoColor Grey                  = new LegoColor(2,   "Grey",                    0x9B, 0xA1, 0x9D);
	public static final LegoColor DarkGrey              = new LegoColor(27,  "Dark Grey",               0x6D, 0x6E, 0x5C);
	public static final LegoColor LightBlue             = new LegoColor(45,  "Light Blue",              0xB4, 0xD2, 0xE3);
	public static final LegoColor BrightGreen           = new LegoColor(37,  "Bright Green",            0x4B, 0x9F, 0x4A);
	public static final LegoColor MediumBluishGreen     = new LegoColor(116, "Medium Bluish Green",     0x55, 0xA5, 0xAF);
	public static final LegoColor BrickRed              = new LegoColor(4,   "Brick Red",               0xF2, 0x70, 0x5E);
	public static final LegoColor LightReddishViolet    = new LegoColor(9,   "Light Reddish Violet",    0xFC, 0x97, 0xAC);
	public static final LegoColor BrightYellow          = new LegoColor(24,  "Bright Yellow",           0xF2, 0xCD, 0x37);
	public static final LegoColor White                 = new LegoColor(1,   "White",                   0xFF, 0xFF, 0xFF);
	public static final LegoColor LightGreen            = new LegoColor(6,   "Light Green",             0xC2, 0xDA, 0xB8);
	public static final LegoColor LightYellow           = new LegoColor(3,   "Light Yellow",            0xFB, 0xE6, 0x96);
	public static final LegoColor BrickYellow           = new LegoColor(5,   "Brick Yellow",            0xE4, 0xCD, 0x9E);
	public static final LegoColor LightBluishViolet     = new LegoColor(39,  "Light Bluish Violet",     0xC9, 0xCA, 0xE2);
	public static final LegoColor BrightViolet          = new LegoColor(104, "Bright Violet",           0x81, 0x00, 0x7B);
	public static final LegoColor DarkRoyalBlue         = new LegoColor(196, "Dark Royal Blue",         0x20, 0x32, 0xB0);
	public static final LegoColor BrightOrange          = new LegoColor(106, "Bright Orange",           0xFE, 0x8A, 0x18);
	public static final LegoColor BrightReddishViolet   = new LegoColor(124, "Bright Reddish Violet",   0x92, 0x39, 0x78);
	public static final LegoColor BrightYellowishGreen  = new LegoColor(119, "Bright Yellowish Green",  0xBB, 0xE9, 0x0B);
	public static final LegoColor SandYellow            = new LegoColor(138, "Sand Yellow",             0x95, 0x8A, 0x73);
	public static final LegoColor LightPurple           = new LegoColor(222, "Light Purple",            0xE4, 0xAD, 0xC8);
	public static final LegoColor MediumLavender        = new LegoColor(324, "Medium Lavender",         0xAC, 0x78, 0xBA);
	public static final LegoColor Lavender              = new LegoColor(325, "Lavender",                0xE1, 0xD5, 0xED);
	public static final LegoColor LightYellowishOrange  = new LegoColor(36,  "Light Yellowish Orange",  0xF3, 0xCF, 0x9B);
	public static final LegoColor BrightReddishLilac    = new LegoColor(198, "Bright Reddish Lilac",    0xCD, 0x62, 0x98);
	public static final LegoColor ReddishBrown          = new LegoColor(192, "Reddish Brown",           0x58, 0x2A, 0x12);
	public static final LegoColor MediumStoneGrey       = new LegoColor(194, "Medium Stone Grey",       0xA0, 0xA5, 0xA9);
	public static final LegoColor DarkStoneGrey         = new LegoColor(199, "Dark Stone Grey",         0x6C, 0x6E, 0x68);
	public static final LegoColor MediumBlue            = new LegoColor(102, "Medium Blue",             0x5C, 0x9D, 0xD1);
	public static final LegoColor MediumGreen           = new LegoColor(29,  "Medium Green",            0x73, 0xDC, 0xA1);
	public static final LegoColor LightPink             = new LegoColor(223, "Light Pink",              0xFE, 0xCC, 0xCF);
	public static final LegoColor LightNougat           = new LegoColor(283, "Light Nougat",            0xF6, 0xD7, 0xB3);
	public static final LegoColor DarkOrange            = new LegoColor(38,  "Dark Orange",             0xCC, 0x70, 0x2A);
	public static final LegoColor MediumLilac           = new LegoColor(268, "Medium Lilac",            0x3F, 0x36, 0x91);
	public static final LegoColor MediumNougat          = new LegoColor(312, "Medium Nougat",           0x7C, 0x50, 0x3A);
	public static final LegoColor MediumRoyalBlue       = new LegoColor(195, "Medium Royal Blue",       0x4C, 0x61, 0xDB);
	public static final LegoColor Nougat                = new LegoColor(18,  "Nougat",                  0xD0, 0x91, 0x68);
	public static final LegoColor LightRed              = new LegoColor(100, "Light Red",               0xFE, 0xBA, 0xBD);
	public static final LegoColor BrightBluishViolet    = new LegoColor(110, "Bright Bluish Violet",    0x43, 0x54, 0xA3);
	public static final LegoColor MediumBluishViolet    = new LegoColor(112, "Medium Bluish Violet",    0x68, 0x74, 0xCA);
	public static final LegoColor MediumYellowishGreen  = new LegoColor(115, "Medium Yellowish Green",  0xC7, 0xD2, 0x3C);
	public static final LegoColor LightBluishGreen      = new LegoColor(118, "Light Bluish Green",      0xB3, 0xD7, 0xD1);
	public static final LegoColor LightYellowishGreen   = new LegoColor(120, "Light Yellowish Green",   0xD9, 0xE4, 0xA7);
	public static final LegoColor LightOrange           = new LegoColor(125, "Light Orange",            0xF9, 0xBA, 0x61);
	public static final LegoColor LightStoneGrey        = new LegoColor(208, "Light Stone Grey",        0xE6, 0xE3, 0xE0);
	public static final LegoColor FlameYellowishOrange  = new LegoColor(191, "Flame Yellowish Orange",  0xF8, 0xBB, 0x3D);
	public static final LegoColor LightRoyalBlue        = new LegoColor(212, "Light Royal Blue",        0x86, 0xC1, 0xE1);
	public static final LegoColor Rust                  = new LegoColor(216, "Rust",                    0xB3, 0x10, 0x04);
	public static final LegoColor CoolYellow            = new LegoColor(226, "Cool Yellow",             0xFF, 0xF0, 0x3A);
	public static final LegoColor DoveBlue              = new LegoColor(232, "Dove Blue",               0x56, 0xBE, 0xD6);
	public static final LegoColor EarthBlue             = new LegoColor(140, "Earth Blue",              0x0D, 0x32, 0x5B);
	public static final LegoColor EarthGreen            = new LegoColor(141, "Earth Green",             0x18, 0x46, 0x32);
	public static final LegoColor DarkBrown             = new LegoColor(308, "Dark Brown",              0x35, 0x21, 0x00);
	public static final LegoColor PastelBlue            = new LegoColor(11,  "Pastel Blue",             0x54, 0xA9, 0xC8);
	public static final LegoColor NewDarkRed            = new LegoColor(154, "New Dark Red",            0x72, 0x0E, 0x0F);
	public static final LegoColor DarkAzur              = new LegoColor(321, "Dark Azur",               0x14, 0x98, 0xD7);
	public static final LegoColor MediumAzur            = new LegoColor(322, "Medium Azur",             0x3E, 0xC2, 0xDD);
	public static final LegoColor Aqua                  = new LegoColor(323, "Aqua",                    0xBD, 0xDC, 0xD8);
	public static final LegoColor SpringYellowishGreen  = new LegoColor(326, "Spring Yellowish Green",  0xDF, 0xEE, 0xA5);
	public static final LegoColor OliveGreen            = new LegoColor(330, "Olive Green",             0x9B, 0x9A, 0x5A);
	public static final LegoColor SandRed               = new LegoColor(153, "Sand Red",                0xD6, 0x75, 0x72);
	public static final LegoColor MediumReddishViolet   = new LegoColor(22,  "Medium Reddish Violet",   0xF7, 0x85, 0xB1);
	public static final LegoColor EarthOrange           = new LegoColor(25,  "Earth Orange",            0xFA, 0x9C, 0x1C);
	public static final LegoColor SandViolet            = new LegoColor(136, "Sand Violet",             0x84, 0x5E, 0x84);
	public static final LegoColor SandGreen             = new LegoColor(151, "Sand Green",              0xA0, 0xBC, 0xAC);
	public static final LegoColor SandBlue              = new LegoColor(135, "Sand Blue",               0x59, 0x71, 0x84);
	public static final LegoColor LightOrangeBrown      = new LegoColor(12,  "Light Orange Brown",      0xB6, 0x7B, 0x50);
	public static final LegoColor BrightYellowishOrange = new LegoColor(105, "Bright Yellowish Orange", 0xFF, 0xA7, 0x0B);
	public static final LegoColor LightGrey             = new LegoColor(103, "Light Grey",              0xE6, 0xE3, 0xDA);
	
	public static final LegoColor[] allColors =
	{
		Black, BrightBlue, DarkGreen, BrightBluishGreen, BrightRed, BrightPurple, Brown, Grey,
		DarkGrey, LightBlue, BrightGreen, MediumBluishGreen, BrickRed, LightReddishViolet,
		BrightYellow, White, LightGreen, LightYellow, BrickYellow, LightBluishViolet, BrightViolet,
		DarkRoyalBlue, BrightOrange, BrightReddishViolet, BrightYellowishGreen, SandYellow,
		LightPurple, MediumLavender, Lavender, LightYellowishOrange, BrightReddishLilac,
		ReddishBrown, MediumStoneGrey, DarkStoneGrey, MediumBlue, MediumGreen, LightPink,
		LightNougat, DarkOrange, MediumLilac, MediumNougat, MediumRoyalBlue, Nougat, LightRed,
		BrightBluishViolet, MediumBluishViolet, MediumYellowishGreen, LightBluishGreen,
		LightYellowishGreen, LightOrange, LightStoneGrey, FlameYellowishOrange, LightRoyalBlue,
		Rust, CoolYellow, DoveBlue, EarthBlue, EarthGreen, DarkBrown, PastelBlue, NewDarkRed,
		DarkAzur, MediumAzur, Aqua, SpringYellowishGreen, OliveGreen, SandRed, MediumReddishViolet,
		EarthOrange, SandViolet, SandGreen, SandBlue, LightOrangeBrown, BrightYellowishOrange,
		LightGrey
	};
}