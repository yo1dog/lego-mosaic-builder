package net.awesomebox.legoMosaicBuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.awesomebox.legoMosaicBuilder.MosaicBuilder.MosaicBuilderException;
import net.awesomebox.legoMosaicBuilder.lego.LegoColor;
import net.awesomebox.legoMosaicBuilder.lego.LegoConnector;
import net.awesomebox.legoMosaicBuilder.lego.LegoShape;

public class TestMain
{
	private static final LegoShape BASE_SHAPE = LegoShape._1x1;
	private static final int IMAGE_SIZE = 20;
	
	private static Mosaic mosaic;
	private static int studPixelSize;
	private static int gap = 5;
	private static MosaicBrick selectedMosaicBrick;
	private static CustomPanel panel;
	private static JFrame frame;
	
	public static void main(String[] args) throws MosaicBuilderException, IOException
	{
		LegoConnector.init();
		
		String imageFilename = System.getProperty("user.dir") + "\\res\\testCase2.bmp";
		System.out.println(imageFilename);
		BufferedImage image = ImageIO.read(new File(imageFilename));
		
		mosaic = MosaicBuilder.buildMosaic(image, IMAGE_SIZE, IMAGE_SIZE, BASE_SHAPE, false);
		
		
		// create the frame
		frame = new JFrame("LegoMosaic");
		
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// create the panel
		panel = new CustomPanel();
		panel.setPreferredSize(new Dimension(800, 800));
		panel.setBackground(Color.WHITE);
		panel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				MosaicBrick[] mosaicBricks = mosaic.getMosaicBricks();
				int mouseX = e.getX();
				int mouseY = e.getY();
				
				selectedMosaicBrick = null;
				
				System.out.println("==========================");
				
				for (int i = 0; i < mosaicBricks.length; ++i)
				{
					MosaicBrick mosaicBrick = mosaicBricks[i];
					
					int x1 = mosaicBrick.originStudX * (studPixelSize + gap);
					int y1 = mosaicBrick.originStudY * (studPixelSize + gap);
					int x2 = x1 + (mosaicBrick.orientedBrick.orientedStudWidth  * studPixelSize + (mosaicBrick.orientedBrick.orientedStudWidth  - 1) * gap);
					int y2 = y1 + (mosaicBrick.orientedBrick.orientedStudHeight * studPixelSize + (mosaicBrick.orientedBrick.orientedStudHeight - 1) * gap);
					
					if (mouseX > x1 && mouseX <= x2 && mouseY > y1 && mouseY <= y2)
					{
						selectedMosaicBrick = mosaicBrick;
						
						System.out.println("--------------");
						System.out.println(mosaicBrick);
						break;
					}
				}
				
				frame.repaint();
			}
			
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		});
		frame.getContentPane().add(panel, java.awt.BorderLayout.CENTER);
		
		// finalize and display
		frame.pack();
		frame.setVisible(true);
	}
	
	private static final class CustomPanel extends JPanel
	{
		private static final long serialVersionUID = 1l;
		
		public void paint(Graphics g)
		{
			studPixelSize = Math.max(1, (Math.min(this.getWidth(), this.getHeight()) - IMAGE_SIZE * gap) / IMAGE_SIZE);
			
			MosaicBrick[] mosaicBricks = mosaic.getMosaicBricks();
			for (int i = 0; i < mosaicBricks.length; ++i)
			{
				MosaicBrick mosaicBrick = mosaicBricks[i];
				
				int x = mosaicBrick.originStudX * (studPixelSize + gap);
				int y = mosaicBrick.originStudY * (studPixelSize + gap);
				int w = mosaicBrick.orientedBrick.orientedStudWidth  * studPixelSize + (mosaicBrick.orientedBrick.orientedStudWidth  - 1) * gap;
				int h = mosaicBrick.orientedBrick.orientedStudHeight * studPixelSize + (mosaicBrick.orientedBrick.orientedStudHeight - 1) * gap;
				
				//g.setColor(mosaicBrick.brick.color.color);
				g.setColor(mosaicBrick.orientedBrick.brick.color == LegoColor.Black? mosaicBrick.orientedBrick.brick.shape.debugColor : mosaicBrick.orientedBrick.brick.color.color);
				g.fillRect(x, y, w, h);
				
				if (mosaicBrick == selectedMosaicBrick)
				{
					g.setColor(Color.BLACK);
					g.drawRect(x, y, w - 1, h - 1);
				}
			}
			
			g.setColor(new Color(0, 0, 0, 64));
			for (int i = 0; i < IMAGE_SIZE; ++i)
			{
				int p = i * (studPixelSize + gap) - gap/2 - 1;
				int l = IMAGE_SIZE * (studPixelSize + gap);
				
				g.drawLine(0, p, l, p);
				g.drawLine(p, 0, p, l);
				g.drawString(Integer.toString(i), p + (studPixelSize + gap) / 2, 10);
				g.drawString(Integer.toString(i), 0, p + (studPixelSize + gap) / 2 + 10);
			}
			
			/*for (int i = 0; i < mosaicBricks.length; ++i)
			{
				MosaicBrick mosaicBrick = mosaicBricks[i];
				
				int x = mosaicBrick.originStudX * (studPixelSize + gap);
				int y = mosaicBrick.originStudY * (studPixelSize + gap);
				int w = mosaicBrick.orientedBrick.orientedStudWidth  * studPixelSize + (mosaicBrick.orientedBrick.orientedStudWidth  - 1) * gap;
				int h = mosaicBrick.orientedBrick.orientedStudHeight * studPixelSize + (mosaicBrick.orientedBrick.orientedStudHeight - 1) * gap;
				
				g.setColor(Color.BLACK);
				g.drawRect(x - 1, y - 1, w, h);
			}*/
		}
	}
}
