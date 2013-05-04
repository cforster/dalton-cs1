package org.dalton;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * DaltonDraw is a simplified class for drawing into a frame using java.
 * @author cforster
 *
 *
 *	TODO: remove DaltonColor
 *  TODO: let repaint be refresh, with optional delay parameter for animations
 *
 */
public class DaltonDraw extends Component {
	public ApplicationFrame frame;
	public static int frameSize = 600;
	
	private static final long serialVersionUID = 1L;
	private List<Object[]> shapeList = new ArrayList<Object[]>();
	private List<Object[]> stringList = new ArrayList<Object[]>();
	private List<Object[]> imageList = new ArrayList<Object[]>();
	private static Map<String, BufferedImage> memImages = new HashMap<String, BufferedImage>();
	
	public void paint(Graphics g) {
		//declarations:
		Graphics2D g2 = (Graphics2D)g;		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for(Object[] drawme : shapeList) {
			Shape s = (Shape)drawme[0];
			int x = s.getBounds().x + (s.getBounds().width/2);
			int y =s.getBounds().y + (s.getBounds().height/2);
			g2.rotate(Math.toRadians((Double)drawme[1]), x, y);
			g2.setColor(((DaltonColor)drawme[2]).getColor());
			g2.fill((Shape)drawme[0]);
			g2.rotate(-Math.toRadians((Double)drawme[1]), x, y);
		}
		for(Object[] writeme: stringList) {
			g2.setFont(new Font("Serif", Font.PLAIN, (Integer)writeme[3]));
			g2.setColor(((DaltonColor)writeme[4]).getColor());
			g2.drawString((String)writeme[0], (Integer)writeme[1], (Integer)writeme[2]);
		}
		for(Object[] imageme: imageList) {
			try {
				String filename  = (String)imageme[0];
				BufferedImage i = ImageIO.read(new File(filename));
				
				g2.translate((Integer)imageme[3], (Integer)imageme[4]);
				g2.scale((Double)imageme[1]/(double)i.getWidth(), 
						(Double)imageme[2]/(double)i.getHeight());
				g2.drawImage(i, 0, 0, null);
				g2.scale((double)i.getWidth()/(Double)imageme[1], 
						(double)i.getHeight()/(Double)imageme[2]);
				g2.translate(-(Integer)imageme[3], -(Integer)imageme[4]);
			} catch (IOException e) {
				System.err.println("Image " + imageme[0] + " could not be shown");
			}
		}
	}

	/**
	 * draw a generic shape into the frame
	 * <p>
	 * A good way to make a shape is using Path2D:
	 * <pre>
	 *  Path2D.Double tri = new Path2D.Double();
	 *  tri.moveTo(50, 50);
	 *  tri.lineTo(50, 100);
	 *  tri.lineTo(100, 50);
	 *  drawShape(tri, 0, Color.black);
	 * </pre>
	 * @param s the shape to draw
	 * @param r the rotation of the shape
	 * @param c the color of the shape
	 */
	public void drawShape(Shape s, double r, DaltonColor c) {
		shapeList.add(new Object[] {s, r, c});
	}
	
	/**
	 * draw a triangle
	 * <p>
	 * Example:
	 * <pre>
	 * dd.drawTri(600, 600, 0, 0, 0, Color.blue);
	 * </pre>
	 * 
	 * @param w width of the triangle
	 * @param h height of the triangle
	 * @param x upper left corner of the triangle (x coordinate)
	 * @param y upper left corner of the triangle (y coordinate)
	 * @param r the rotation of the triangle
	 * @param c the color of the triangle
	 */
	public void drawTri(double w, double h, double x, double y, double r, DaltonColor c) {
		Path2D.Double tri = new Path2D.Double();
		tri.moveTo(x, y+h);
		tri.lineTo(x+w, y+h);
		tri.lineTo(x+(w/2), y);
		shapeList.add(new Object[] {tri, r, c});
	}
	/**
	 * draw a rectangle
	 * <p>
	 * Example
	 * <pre>
	 * dd.drawRect(30, 30, 0, 0, 0, Color.black);
	 * </pre>
	 * @param w width of the rectangle
	 * @param h height of the rectangle
	 * @param x upper left corner of the rectangle (x coordinate)
	 * @param y upper left corner of the rectangle (y coordinate)
	 * @param r the rotation of the rectangle
	 * @param c the color of the rectangle
	 */
	public void drawRect(double w, double h, double x, double y, double r, DaltonColor c) {
		Rectangle2D.Double s = new Rectangle2D.Double(x, y, w, h);
		shapeList.add(new Object[] {s, r, c});
	}

	/**
	 * draw an ellipse
	 * <p>
	 * Example
	 * <pre>
	 * dd.drawElipse(30, 30, 0, 0, 0, Color.green);
	 * </pre>
	 * @param w width of the ellipse
	 * @param h height of the ellipse
	 * @param x upper left corner of the ellipse (x coordinate)
	 * @param y upper left corner of the ellipse (y coordinate)
	 * @param r the rotation of the ellipse
	 * @param c the color of the ellipse
	 */
	public void drawEllipse(double w, double h, double x, double y, double r, DaltonColor c) {
		Ellipse2D.Double s = new Ellipse2D.Double(x, y, w, h);
		shapeList.add(new Object[] {s, r, c});
	}

	/**
	 * draw a string into the frame
	 * <p>
	 * ex:
	 * <pre>
	 * dd.drawString("hello world", 200, 200, 24, Color.black);
	 * </pre>
	 * @param t string to print
	 * @param x lower left corner of the text (x coordinate)
	 * @param y lower left corner of the text (y coordinate)
	 * @param s size of the font
	 * @param c color of the text
	 */
	public void drawString(String t, int x, int y, int s, DaltonColor c) {
		stringList.add(new Object[] {t, x, y, s, c});
	}

	/**
	 * draw an image into the frame
	 * <p>
	 * the file must be in the folder that you are working from<br>
	 * ex:
	 * <pre>
	 * dd.drawImage("flower.jpg", 30, 30, 60, 60);
	 * </pre>
	 * @param i the file name of the image
	 * @param w the width of the image
	 * @param h the height of the image
	 * @param x the upper left corner of the image (x coordinate)
	 * @param y the upper left corner of the image (y coordinate)
	 */
	public void drawImage(String i, int w, int h, int x, int y) {
		imageList.add(new Object[] {i, (double)w, (double)h, x, y});
	}

	/**
	 * get the color of a given location in an image, assuming that image is spread over the frame.
	 * <p>
	 * ex
	 * <pre>
	 * DaltonColor mycolor = DaltonDraw.getPixel("raphael.jpg", 50, 50);
	 * </pre>
	 * @param filename the file name of the image
	 * @param x the x coordinate of the frame
	 * @param y the y coordinate of the frame
	 * @return the color of that pixel
	 */
	public static DaltonColor getPixel(String filename, int x, int y) {
		
		try {
			BufferedImage bi;
			if(!memImages.containsKey(filename)) {
				bi = ImageIO.read(new File(filename));//.getScaledInstance(600, 600, Image.SCALE_DEFAULT);
				memImages.put(filename, bi);
			}
			else {
				bi = memImages.get(filename);
			}
			int x_scale = (int)((double)x * ((double)bi.getWidth()/(double)ApplicationFrame.FRAMESIZE));
			int y_scale = (int)((double)y * ((double)bi.getHeight()/(double)ApplicationFrame.FRAMESIZE));
			//System.err.println(x_scale + "|" + y_scale);
			
			return(new DaltonColor(bi.getRGB(x_scale, y_scale))); 
		} catch (IOException e) {
			System.err.println("Image " + filename + " could not be loaded");
			return DaltonColor.black;
		} catch( ArrayIndexOutOfBoundsException e ) {
			System.err.println("that (x,y) is outside of the bounds of the frame");
			return DaltonColor.black;
		}
	}
	
	/**
	 * default constructor for the DaltonDraw object
	 */
	public DaltonDraw() { this("Default Title"); }
	
	/**
	 * constructor for the DaltonDraw object
	 * @param title the frame title
	 */
	public DaltonDraw(String title) {
		frame = new ApplicationFrame(title);  //
		frame.add(this);
		frame.setVisible(true);
	}

}
