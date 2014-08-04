package net.awesomebox.legoMosaicBuilder.lego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class LegoConnector
{
	// DEBUG: hard coded bricks
	public static final void init()
	{
		// generate bricks for every shape in every color
		for (int i = 0; i < LegoShape.allShapes.length; ++i)
		{
			LegoShape.allShapes[i].bricks = new LegoBrick[LegoColor.allColors.length];
			
			for (int j = 0; j < LegoColor.allColors.length; ++j)
				LegoShape.allShapes[i].bricks[j] = new LegoBrick(0, LegoShape.allShapes[i], LegoColor.allColors[j]);
		}
		
		for (int i = 0; i < LegoColor.allColors.length; ++i)
		{
			LegoColor.allColors[i].bricks = new LegoBrick[LegoShape.allShapes.length];
			LegoColor.allColors[i].bricksByStudWidth  = new OrientedLegoBrick[6][6];
			LegoColor.allColors[i].bricksByStudHeight = new OrientedLegoBrick[6][6];
			LegoColor.allColors[i].bricksByStudWidthMaxHeight = new int[6];
			LegoColor.allColors[i].bricksByStudHeightMaxWidth = new int[6];
			
			for (int j = 0; j < LegoShape.allShapes.length; ++j)
			{
				LegoBrick brick = LegoShape.allShapes[j].bricks[i];
				LegoColor.allColors[i].bricks[j] = brick;
				
				
				OrientedLegoBrick orientedLegoBrick = brick.orientedBrickNonRotated;
				
				LegoColor.allColors[i].bricksByStudWidth [orientedLegoBrick.orientedStudWidth  - 1][orientedLegoBrick.orientedStudHeight - 1] = orientedLegoBrick;
				LegoColor.allColors[i].bricksByStudHeight[orientedLegoBrick.orientedStudHeight - 1][orientedLegoBrick.orientedStudWidth  - 1] = orientedLegoBrick;
				
				if (orientedLegoBrick.orientedStudHeight > LegoColor.allColors[i].bricksByStudWidthMaxHeight[orientedLegoBrick.orientedStudWidth - 1])
					LegoColor.allColors[i].bricksByStudWidthMaxHeight[orientedLegoBrick.orientedStudWidth - 1] = orientedLegoBrick.orientedStudHeight;
				
				if (orientedLegoBrick.orientedStudWidth > LegoColor.allColors[i].bricksByStudHeightMaxWidth[orientedLegoBrick.orientedStudHeight - 1])
					LegoColor.allColors[i].bricksByStudHeightMaxWidth[orientedLegoBrick.orientedStudHeight - 1] = orientedLegoBrick.orientedStudWidth;
				
				
				// rotated
				// don't override a non-rotated
				orientedLegoBrick = brick.orientedBrickRotated;
				
				if (LegoColor.allColors[i].bricksByStudWidth [orientedLegoBrick.orientedStudWidth - 1][orientedLegoBrick.orientedStudHeight - 1] == null)
					LegoColor.allColors[i].bricksByStudWidth [orientedLegoBrick.orientedStudWidth - 1][orientedLegoBrick.orientedStudHeight - 1] = orientedLegoBrick;
				
				if (LegoColor.allColors[i].bricksByStudHeight[orientedLegoBrick.orientedStudHeight - 1][orientedLegoBrick.orientedStudWidth - 1] == null)
					LegoColor.allColors[i].bricksByStudHeight[orientedLegoBrick.orientedStudHeight - 1][orientedLegoBrick.orientedStudWidth - 1] = orientedLegoBrick;
				
				if (orientedLegoBrick.orientedStudHeight > LegoColor.allColors[i].bricksByStudWidthMaxHeight[orientedLegoBrick.orientedStudWidth - 1])
					LegoColor.allColors[i].bricksByStudWidthMaxHeight[orientedLegoBrick.orientedStudWidth - 1] = orientedLegoBrick.orientedStudHeight;
				
				if (orientedLegoBrick.orientedStudWidth > LegoColor.allColors[i].bricksByStudHeightMaxWidth[orientedLegoBrick.orientedStudHeight - 1])
					LegoColor.allColors[i].bricksByStudHeightMaxWidth[orientedLegoBrick.orientedStudHeight - 1] = orientedLegoBrick.orientedStudWidth;
			}
		}
		
		/*_2x4.bricks = new LegoBrick[]
		{
			new LegoBrick(300101,  _2x4, LegoColor.White),
			new LegoBrick(300121,  _2x4, LegoColor.BrightRed),
			new LegoBrick(300123,  _2x4, LegoColor.BrightBlue),
			new LegoBrick(300124,  _2x4, LegoColor.BrightYellow),
			new LegoBrick(300126,  _2x4, LegoColor.Black),
			new LegoBrick(4106356, _2x4, LegoColor.DarkGreen),
			new LegoBrick(4114319, _2x4, LegoColor.BrickYellow),
			new LegoBrick(4153827, _2x4, LegoColor.BrightOrange),
			new LegoBrick(4165967, _2x4, LegoColor.BrightYellowishGreen),
			new LegoBrick(4211085, _2x4, LegoColor.DarkStoneGrey),
			new LegoBrick(4211201, _2x4, LegoColor.ReddishBrown),
			new LegoBrick(4211385, _2x4, LegoColor.MediumStoneGrey),
			new LegoBrick(4520632, _2x4, LegoColor.LightPurple),
			new LegoBrick(4655172, _2x4, LegoColor.DarkAzur),
			new LegoBrick(4655173, _2x4, LegoColor.MediumLavender)
		};
		
		_2x3.bricks = new LegoBrick[]
		{
			new LegoBrick(300201,  _2x3, LegoColor.White),
			new LegoBrick(300221,  _2x3, LegoColor.BrightRed),
			new LegoBrick(300223,  _2x3, LegoColor.BrightBlue),
			new LegoBrick(300224,  _2x3, LegoColor.BrightYellow),
			new LegoBrick(300226,  _2x3, LegoColor.Black),
			new LegoBrick(4109674, _2x3, LegoColor.DarkGreen),
			new LegoBrick(4153826, _2x3, LegoColor.BrightOrange),
			new LegoBrick(4159739, _2x3, LegoColor.BrickYellow),
			new LegoBrick(4211105, _2x3, LegoColor.DarkStoneGrey),
			new LegoBrick(4211386, _2x3, LegoColor.MediumStoneGrey),
			new LegoBrick(4216668, _2x3, LegoColor.ReddishBrown),
			new LegoBrick(4220631, _2x3, LegoColor.BrightYellowishGreen),
			new LegoBrick(4518892, _2x3, LegoColor.LightPurple)
		};
		
		_2x2.bricks = new LegoBrick[]
		{
			new LegoBrick(300301,  _2x2, LegoColor.White),
			new LegoBrick(300321,  _2x2, LegoColor.BrightRed),
			new LegoBrick(300323,  _2x2, LegoColor.BrightBlue),
			new LegoBrick(300324,  _2x2, LegoColor.BrightYellow),
			new LegoBrick(300326,  _2x2, LegoColor.Black),
			new LegoBrick(300328,  _2x2, LegoColor.DarkGreen),
			new LegoBrick(4114306, _2x2, LegoColor.BrickYellow),
			new LegoBrick(4153825, _2x2, LegoColor.BrightOrange),
			new LegoBrick(4211060, _2x2, LegoColor.DarkStoneGrey),
			new LegoBrick(4211210, _2x2, LegoColor.ReddishBrown),
			new LegoBrick(4211387, _2x2, LegoColor.MediumStoneGrey),
			new LegoBrick(4220632, _2x2, LegoColor.BrightYellowishGreen),
			new LegoBrick(4255416, _2x2, LegoColor.SandYellow),
			new LegoBrick(4296785, _2x2, LegoColor.EarthBlue),
			new LegoBrick(4517992, _2x2, LegoColor.BrightPurple),
			new LegoBrick(4550359, _2x2, LegoColor.LightPurple),
			new LegoBrick(4653960, _2x2, LegoColor.MediumLilac),
			new LegoBrick(4653970, _2x2, LegoColor.MediumAzur)
		};
		
		_1x2.bricks = new LegoBrick[]
		{
			new LegoBrick(300401,  _1x2, LegoColor.White),
			new LegoBrick(300421,  _1x2, LegoColor.BrightRed),
			new LegoBrick(300423,  _1x2, LegoColor.BrightBlue),
			new LegoBrick(300424,  _1x2, LegoColor.BrightYellow),
			new LegoBrick(300426,  _1x2, LegoColor.Black),
			new LegoBrick(4107736, _1x2, LegoColor.DarkGreen),
			new LegoBrick(4109995, _1x2, LegoColor.BrickYellow),
			new LegoBrick(4121739, _1x2, LegoColor.BrightOrange),
			new LegoBrick(4164022, _1x2, LegoColor.BrightYellowishGreen),
			new LegoBrick(4179833, _1x2, LegoColor.MediumBlue),
			new LegoBrick(4211088, _1x2, LegoColor.DarkStoneGrey),
			new LegoBrick(4211149, _1x2, LegoColor.ReddishBrown),
			new LegoBrick(4211388, _1x2, LegoColor.MediumStoneGrey),
			new LegoBrick(4245570, _1x2, LegoColor.EarthGreen),
			new LegoBrick(4249891, _1x2, LegoColor.EarthBlue),
			new LegoBrick(4517993, _1x2, LegoColor.LightPurple),
			new LegoBrick(4519195, _1x2, LegoColor.BrightReddishViolet),
			new LegoBrick(4521914, _1x2, LegoColor.SandYellow),
			new LegoBrick(4539102, _1x2, LegoColor.NewDarkRed),
			new LegoBrick(4621545, _1x2, LegoColor.BrightPurple),
			new LegoBrick(4623598, _1x2, LegoColor.MediumLavender),
			new LegoBrick(6003003, _1x2, LegoColor.FlameYellowishOrange),
			new LegoBrick(6004943, _1x2, LegoColor.DarkAzur),
			new LegoBrick(6022083, _1x2, LegoColor.CoolYellow)
		};
		
		_1x1.bricks = new LegoBrick[]
		{
			new LegoBrick(300501,  _1x1, LegoColor.White),
			new LegoBrick(300521,  _1x1, LegoColor.BrightRed),
			new LegoBrick(300523,  _1x1, LegoColor.BrightBlue),
			new LegoBrick(300524,  _1x1, LegoColor.BrightYellow),
			new LegoBrick(300526,  _1x1, LegoColor.Black),
			new LegoBrick(300528,  _1x1, LegoColor.DarkGreen),
			new LegoBrick(4113915, _1x1, LegoColor.BrickYellow),
			new LegoBrick(4173805, _1x1, LegoColor.BrightOrange),
			new LegoBrick(4179830, _1x1, LegoColor.MediumBlue),
			new LegoBrick(4211098, _1x1, LegoColor.DarkStoneGrey),
			new LegoBrick(4211242, _1x1, LegoColor.ReddishBrown),
			new LegoBrick(4211389, _1x1, LegoColor.MediumStoneGrey),
			new LegoBrick(4220634, _1x1, LegoColor.BrightYellowishGreen),
			new LegoBrick(4286050, _1x1, LegoColor.LightPurple),
			new LegoBrick(4651903, _1x1, LegoColor.MediumLavender),
			new LegoBrick(6022035, _1x1, LegoColor.BrightReddishViolet)
		};*/
	}
	
	
	
	// DEBUG: should be private
	public static final LegoBrick[] getBricksForShape(LegoShape shape) throws LegoRequestException
	{
		ArrayList<LegoBrick> legoBricks = new ArrayList<LegoBrick>();
		
		String response;
		try
		{
			response = makeRequest("GET", createBrickListURL(shape));
		}
		catch (IOException e)
		{
			throw new LegoRequestException("Error getting bricks for shape " + shape.name + " (" + shape.id + ").", e);
		}
		
		
		// parse out brick IDs
		ArrayList<Integer> brickIDs = new ArrayList<Integer>();
		
		// <a href="javascript:getBrick(300521)" class="pab">Brick 1X1</a>
		String prefix1 = "getBrick(";
		String suffix1 = ")";
		String prefix2 = "\" class=\"pab\">";
		String suffix2 = "</a>";
		
		int index = 0;
		int index2;
		while ((index = response.indexOf(prefix1, index)) > -1)
		{
			// parse brick Type ID
			index += prefix1.length();
			index2 = response.indexOf(suffix1, index);
			
			if (index2 == -1)
				continue;
			
			String brickIDStr = response.substring(index, index2);
			
			
			// parse brick name
			index = index2 + suffix1.length();
			index2 = response.indexOf(prefix2, index);
			
			// part2Prefix should be right after part1Suffix
			if (index2 != index)
				continue;
			
			index = index2 + prefix2.length();
			index2 = response.indexOf(suffix2, index);
			
			if (index2 == -1)
				continue;
			
			String brickName = response.substring(index, index2).toLowerCase();
			
			
			// advance index
			index = index2 + suffix2.length();
			
			
			// make sure the brick name matches the shape name, otherwise we have a special brick
			if (!brickName.equals(shape.name))
				continue;
			
			
			// parse the brickID as an integer
			int brickID;
			try
			{
				brickID = Integer.parseInt(brickIDStr);
			}
			catch (NumberFormatException e)
			{
				throw new LegoRequestException("Error parsing brick ID: \"" + brickIDStr + "\" as an integer.", e);
			}
			
			
			// don't add if duplicate
			boolean duplicate = false;
			for (int existingBrickID : brickIDs)
			{
				if (existingBrickID == brickID)
				{
					duplicate = true;
					break;
				}
			}
			
			if (duplicate)
				continue;
			
			
			// found a brick!
			brickIDs.add(brickID);
		}
		
		
		// get details for each brick
		for (int brickID : brickIDs)
		{
			// get the brick details
			try
			{
				response = makeRequest("GET", createBrickURL(brickID));
			}
			catch (IOException e)
			{
				throw new LegoRequestException("Error getting brick details for brick ID: \"" + brickID + "\".", e);
			}
			
			
			// parse the color ID
			String prefix = "getBricks(6,";
			String suffix = ")";
			
			index = response.indexOf(prefix);
			if (index == -1)
				continue;
			
			index += prefix.length();
			index2 = response.indexOf(suffix, index);
			
			if (index2 == -1)
				continue;
			
			String colorIDStr = response.substring(index, index2);
			int colorID;
			try
			{
				colorID = Integer.parseInt(colorIDStr);
			}
			catch (NumberFormatException e)
			{
				throw new LegoRequestException("Error parsing color ID: \"" + colorIDStr + "\" as an integer for brick with ID: \"" + brickID + "\".", e);
			}
			
			
			// find the color
			boolean colorFound = false;
			for (LegoColor color : LegoColor.allColors)
			{
				if (color.id == colorID)
				{
					// add the brick
					legoBricks.add(new LegoBrick(brickID, shape, color));
					colorFound = true;
					break;
				}
			}
			
			if (!colorFound)
				System.err.println("WARNING: Found unsupported color ID: \"" + colorID + "\".");
		}
		
		
		LegoBrick[] legoBricksArray = new LegoBrick[legoBricks.size()];
		return legoBricks.toArray(legoBricksArray);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static final class HTTPInvalidResponseException extends IOException
	{
		private static final long serialVersionUID = 1L;

		private HTTPInvalidResponseException(String message)
		{
			super (message);
		}
	}
	
	public static final class LegoRequestException extends Exception
	{
		private static final long serialVersionUID = 1L;

		private LegoRequestException(String message, Throwable t)
		{
			super (message, t);
		}
	}
	
	
	
	private static final URL createBrickListURL(LegoShape shape) throws MalformedURLException
	{
		return new URL("http://customization.lego.com/en-US/pab/service/getBricks.aspx?st=4&pn=0&ps=100&cat=US&sv=" + shape.id);
	}
	
	private static final URL createBrickURL(int brickID) throws MalformedURLException
	{
		return new URL("http://customization.lego.com/en-US/pab/service/getBrick.aspx?itemid=" + brickID);
	}
	
	
	
	
	private static final String makeRequest(String method, URL url) throws IOException
	{
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestMethod(method);
		
		int responseCode = con.getResponseCode();
		if (responseCode != 200)
			throw new HTTPInvalidResponseException("Recieved a non 200 response code: " + responseCode);
		
		// read response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		
		try
		{
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			
		}
		finally
		{
			in.close();
		}
		
		return response.toString();
	}
}
