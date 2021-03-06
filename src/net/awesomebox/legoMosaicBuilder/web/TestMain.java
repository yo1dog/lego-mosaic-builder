package net.awesomebox.legoMosaicBuilder.web;

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

import net.awesomebox.legoMosaicBuilder.builder.Mosaic;
import net.awesomebox.legoMosaicBuilder.builder.MosaicBrick;
import net.awesomebox.legoMosaicBuilder.builder.MosaicBuilder;
import net.awesomebox.legoMosaicBuilder.builder.MosaicBuilder.MosaicBuilderException;
import net.awesomebox.legoMosaicBuilder.lego.LegoColor;
import net.awesomebox.legoMosaicBuilder.lego.LegoConnector;
import net.awesomebox.legoMosaicBuilder.lego.LegoShape;

public class TestMain
{
	private static final LegoShape BASE_SHAPE = LegoShape._1x1;
	private static final int IMAGE_SIZE = 20;
	
	static Mosaic mosaic;
	static int studPixelSize;
	static int gap = 1;
	static MosaicBrick selectedMosaicBrick;
	static CustomPanel panel;
	static JFrame frame;
	
	public static void main(String[] args) throws MosaicBuilderException, IOException
	{
		LegoConnector.init();
		
		String imageFilename = System.getProperty("user.dir") + "\\res\\testCase1.bmp";
		System.out.println(imageFilename);
		BufferedImage image = ImageIO.read(new File(imageFilename));
		
		mosaic = MosaicBuilder.buildMosaic(image, IMAGE_SIZE, IMAGE_SIZE, BASE_SHAPE, false, 2);
		System.out.println("Bricks: " + mosaic.mosaicBricks.length);
		
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
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int mouseX = e.getX();
				int mouseY = e.getY();
				
				selectedMosaicBrick = null;
				
				System.out.println("==========================");
				
				for (int i = 0; i < mosaic.mosaicBricks.length; ++i)
				{
					MosaicBrick mosaicBrick = mosaic.mosaicBricks[i];
					
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
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
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
		
		CustomPanel() {}

		@Override
		public void paint(Graphics g)
		{
			TestMain.studPixelSize = Math.max(1, (Math.min(this.getWidth(), this.getHeight()) - IMAGE_SIZE * gap) / IMAGE_SIZE);
			
			for (int i = 0; i < mosaic.mosaicBricks.length; ++i)
			{
				MosaicBrick mosaicBrick = mosaic.mosaicBricks[i];
				
				int x = mosaicBrick.originStudX * (studPixelSize + gap);
				int y = mosaicBrick.originStudY * (studPixelSize + gap);
				int w = mosaicBrick.orientedBrick.orientedStudWidth  * studPixelSize + (mosaicBrick.orientedBrick.orientedStudWidth  - 1) * gap;
				int h = mosaicBrick.orientedBrick.orientedStudHeight * studPixelSize + (mosaicBrick.orientedBrick.orientedStudHeight - 1) * gap;
				
				g.setColor(mosaicBrick.orientedBrick.brick.color.color);
				g.setColor(mosaicBrick.orientedBrick.brick.color == LegoColor.Black? mosaicBrick.orientedBrick.brick.shape.debugColor : mosaicBrick.orientedBrick.brick.color.color);
				g.fillRect(x, y, w, h);
				
				if (mosaicBrick == selectedMosaicBrick)
				{
					g.setColor(Color.BLACK);
					g.drawRect(x, y, w - 2, h - 2);
				}
			}
			
			for (int i = 0; i < IMAGE_SIZE; ++i)
			{
				int p = i * (studPixelSize + gap) - gap/2 - 1;
				int l = IMAGE_SIZE * (studPixelSize + gap);
				
				g.setColor(new Color(0, 0, 0, 64));
				g.drawLine(0, p, l, p);
				g.drawLine(p, 0, p, l);
				
				g.setColor(Color.black);
				g.drawString(Integer.toString(i), p + (studPixelSize + gap) / 2, 10);
				g.drawString(Integer.toString(i), 0, p + (studPixelSize + gap) / 2 + 10);
			}
			
			g.setColor(Color.BLACK);
			for (int i = 0; i < mosaic.mosaicBricks.length; ++i)
			{
				MosaicBrick mosaicBrick = mosaic.mosaicBricks[i];
				
				int x = mosaicBrick.originStudX * (studPixelSize + gap);
				int y = mosaicBrick.originStudY * (studPixelSize + gap);
				int w = mosaicBrick.orientedBrick.orientedStudWidth  * studPixelSize + (mosaicBrick.orientedBrick.orientedStudWidth  - 1) * gap;
				int h = mosaicBrick.orientedBrick.orientedStudHeight * studPixelSize + (mosaicBrick.orientedBrick.orientedStudHeight - 1) * gap;
				
				g.drawRect(x - 1, y - 1, w, h);
			}
		}
	}
}
