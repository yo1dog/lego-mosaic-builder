package net.awesomebox.legoMosaicBuilder.mosaicOptimizers;

import java.util.ArrayList;

import net.awesomebox.legoMosaicBuilder.MosaicBrick;
import net.awesomebox.legoMosaicBuilder.lego.LegoBrick;
import net.awesomebox.legoMosaicBuilder.lego.LegoColor;
import net.awesomebox.legoMosaicBuilder.lego.LegoShape;

//=============================================================================
// =============================================================================
//
// Optimize Level 1
//
// =============================================================================
// 
// Replaces 1x1 bricks with larger bricks.
//
// We start with the largest brick, placing as many of them as will fit over the
// 1x1s of the same color. We then do the same with the brick rotated. We
// continue this pattern with all the available bricks of a color down to the
// smallest brick.
//
// =============================================================================
// =============================================================================

public final class MosaicOptimizerLevel1
{
	public static final void optimize(ArrayList<LegoColor> colorsUsed,  MosaicBrick[][] mosaic, int mosaicStudWidth, int mosaicStudHeight)
	{
		// for each color used
		for (LegoColor color : colorsUsed)
		{
			LegoBrick color1x1Brick = color.get1x1Brick();
			
			// replace the 1x1 bricks with the largest bricks first, then the smallest
			// IMPORTANT: we assume the list of bricks for a color is ordered from largest shape to smallest
			for (LegoBrick brick : color.getBricks())
			{
				// don't go lower than 1x1
				if (brick.shape == LegoShape._1x1)
					break;
				
				
				// IMPORTANT: we assume the height of the shape is greater than the width (non-rotated)
				// iterate based on the longest length of the shape
				// for example, if the shape is not rotated (height > width):
				//   we move top->bottom then left->right looking for the origin (starting) stud, then
				//   we move left->right then top->bottom when we are checking if the brick can fit.
				//   This is so we can know how many studs we can advance if the shape does or does not
				//   fit.
				// Example:
				//  If we are trying to place a non-rotated 2x6 starting at studs 0,0
				//  (O is a valid stud, X is invalid)
				//
				//  O O
				//  O O
				//  O O
				//  O X
				//  O O
				//  O O
				//
				//  We will check
				//  0,0 0,1
				//  1,0 1,1
				//  2,0 2,1
				//  3,0 3,1 <- invalid space, won't fit
				//
				//  If we find that rows 0-2 are clear, but row 3 has an offending stud preventing
				//  us from placing the brick, we know that we can not fit a 2x6 starting at rows
				//  0-3 because they will all collide with the offending stud on row 3. Therefore,
				//  our next check should start at row 4.
				
				// find places where this brick's shape can fit in the current color
				
				int brickShapeStudWidth;
				int brickShapeStudHeight;
				
				
				// non-rotated
				brickShapeStudWidth = brick.shape.studWidth;
				brickShapeStudHeight = brick.shape.studHeight;
				
				int studXLimit = mosaicStudWidth  - (brickShapeStudWidth  - 1);
				int studYLimit = mosaicStudHeight - (brickShapeStudHeight - 1);
				
				for (int studX = 0; studX < studXLimit; ++studX)
				{
					for (int studY = 0; studY < studYLimit; ++studY)
					{
						boolean willFit = true;
						
						int invalidStudOffsetY = 0;
						for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
						{
							for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
							{
								// check if this stud contains a 1x1 of the correct color
								if (mosaic[studX + studOffsetX][studY + studOffsetY].orientedBrick.brick != color1x1Brick)
								{
									willFit = false;
									break;
								}
							}
							
							if (!willFit)
							{
								invalidStudOffsetY = studOffsetY;
								break;
							}
						}
						
						if (willFit)
						{
							// create the mosaic brick
							MosaicBrick mosaicBrick = new MosaicBrick(brick.orientedBrickNonRotated, studX, studY);
							
							// place the brick onto the mosaic, replacing the 1x1s
							for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
							{
								for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
									mosaic[studX + studOffsetX][studY + studOffsetY] = mosaicBrick;
							}
							
							// advance the length of the shape
							studY += brickShapeStudHeight - 1;
						}
						else
						{
							// skip forward as there is no need to test the studs in between.
							studY += invalidStudOffsetY;
						}
					}
				}
				
				
				// rotated
				// only if not square
				if (brick.shape.studWidth == brick.shape.studHeight)
					continue;
				
				brickShapeStudWidth = brick.shape.studHeight;
				brickShapeStudHeight = brick.shape.studWidth;
				
				studXLimit = mosaicStudWidth  - (brickShapeStudWidth  - 1);
				studYLimit = mosaicStudHeight - (brickShapeStudHeight - 1);
				
				for (int studY = 0; studY < studYLimit; ++studY)
				{
					for (int studX = 0; studX < studXLimit; ++studX)
					{
						boolean willFit = true;
						
						int invalidStudOffsetX = 0;
						for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
						{
							for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
							{
								// check if this stud contains a 1x1 of the correct color
								if (mosaic[studX + studOffsetX][studY + studOffsetY].orientedBrick.brick != color1x1Brick)
								{
									willFit = false;
									break;
								}
							}
							
							if (!willFit)
							{
								invalidStudOffsetX = studOffsetX;
								break;
							}
						}
						
						if (willFit)
						{
							// create the mosaic brick
							MosaicBrick mosaicBrick = new MosaicBrick(brick.orientedBrickRotated, studX, studY);
							
							// place the brick onto the mosaic, replacing the 1x1s
							for (int studOffsetY = 0; studOffsetY < brickShapeStudHeight; ++studOffsetY)
							{
								for (int studOffsetX = 0; studOffsetX < brickShapeStudWidth; ++studOffsetX)
									mosaic[studX + studOffsetX][studY + studOffsetY] = mosaicBrick;
							}
							
							// advance the length of the shape
							studX += brickShapeStudWidth - 1;
						}
						else
						{
							// skip forward as there is no need to test the studs in between.
							studX += invalidStudOffsetX;
						}
					}
				}
			}
		}
	}
}
