package org.dalton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

/**
 * DaltonDraw is a simplified class for drawing into a frame using java.
 * @author cforster
 *
 *
 *  TODO: let repaint be refresh, with optional delay parameter for animations
 *
 */
public class DaltonDraw {

	//for testing
	public static void main(String[] args) {
		DaltonDraw dd = new DaltonDraw();
		while(true)
		{
			for (int i = 0; i < 100; i++) {
				dd.clear();
				dd.drawImage("src/images/Hello_Kitty_Pink.jpg", 100, 100, 30, 30);
				dd.drawRect(20, 20, 10+i, 10+i, 0, Color.red);
				dd.drawString("Hello Kitty", 200, 200, 20, Color.green);
				dd.render(100);
			}
		}
	}

	private DaltonComponent c = new DaltonComponent();
	public ApplicationFrame frame;
	public static int frameSize = 600;
	CountDownLatch latch = null;


	private static final long serialVersionUID = 1L;
	private List<Drawable> drawList = new ArrayList<Drawable>();
	private static Map<String, BufferedImage> memImages = new HashMap<String, BufferedImage>();

	public void render(int backoff) {
		synchronized(this) { latch = new CountDownLatch(1); }
		c.repaint();
		try {
			Thread.sleep(backoff);
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized(this) { latch = null; }
	}
	
	public void render() {
		render(10);
	}

	/*
	 * interface elements:
	 *
	public String listen(String name) {

	}*/

	public void clear() {
		drawList.clear();
	}

	@SuppressWarnings("serial")
	class DaltonComponent extends Component {

		public void paint(Graphics g) {
			//declarations:
			Graphics2D g2 = (Graphics2D)g;		
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			for(Drawable drawme : drawList) {
				drawme.draw(g2);
			}
			synchronized(this) { if(latch!=null) latch.countDown(); }
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
	public void drawShape(Shape s, double r, Color c) {
		drawList.add(new DaltonShape(s, r, c));
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
	public void drawTri(double w, double h, double x, double y, double r, Color c) {
		Path2D.Double tri = new Path2D.Double();
		tri.moveTo(x, y+h);
		tri.lineTo(x+w, y+h);
		tri.lineTo(x+(w/2), y);
		this.drawShape(tri, r, c);
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
	public void drawRect(double w, double h, double x, double y, double r, Color c) {
		Rectangle2D.Double s = new Rectangle2D.Double(x, y, w, h);
		this.drawShape(s, r, c);
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
	public void drawEllipse(double w, double h, double x, double y, double r, Color c) {
		Ellipse2D.Double s = new Ellipse2D.Double(x, y, w, h);
		this.drawShape(s, r, c);
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
	public void drawString(String t, int x, int y, int s, Color c) {
		drawList.add(new DaltonString(t, x, y, s, c));
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
		drawList.add(new DaltonImage (i, (double)w, (double)h, x, y));
	}

	/**
	 * get the color of a given location in an image, assuming that image is spread over the frame.
	 * <p>
	 * ex
	 * <pre>
	 * Color mycolor = DaltonDraw.getPixel("raphael.jpg", 50, 50);
	 * </pre>
	 * @param filename the file name of the image
	 * @param x the x coordinate of the frame
	 * @param y the y coordinate of the frame
	 * @return the color of that pixel
	 */
	public static Color getPixel(String filename, int x, int y) {

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

			return(new Color(bi.getRGB(x_scale, y_scale))); 
		} catch (IOException e) {
			System.err.println("Image " + filename + " could not be loaded");
			return Color.black;
		} catch( ArrayIndexOutOfBoundsException e ) {
			System.err.println("that (x,y) is outside of the bounds of the frame");
			return Color.black;
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
		frame.add(c);
		frame.setVisible(true);
	}

	interface Drawable { 
		void draw(Graphics2D g2);
	}

	class DaltonShape implements Drawable {
		Shape s;
		double r;
		Color c;

		public DaltonShape(Shape s, double r, Color c) {
			this.s = s;
			this.r = r;
			this.c = c;
		}

		@Override
		public void draw(Graphics2D g2) {
			int x = s.getBounds().x + (s.getBounds().width/2);
			int y =s.getBounds().y + (s.getBounds().height/2);
			g2.rotate(Math.toRadians(r), x, y);
			g2.setColor(c);
			g2.fill(s);
			g2.rotate(-Math.toRadians(r), x, y);

		}
	}

	class DaltonString implements Drawable {
		int x, y;
		String s;
		Color c;
		int size;

		public DaltonString(String s, int x, int y, int size, Color c) {
			this.x = x;
			this.y = y;
			this.s = s;
			this.c = c;
			this.size = size;
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setFont(new Font("Serif", Font.PLAIN, size));
			g2.setColor(c);
			g2.drawString(s, x, y);
		}	
	}

	class DaltonButton implements Drawable {
		String name;
		int x, y;
		double w, h;

		public DaltonButton(String name, double w, double h, int x, int y) {
			this.name = name;
			this.w = w;
			this.h = h;
			this.x = x;
			this.y = y;
		}

		@Override
		public void draw(Graphics2D g2) {
			// TODO Auto-generated method stub

		}

	}

	class DaltonImage implements Drawable {
		String filename;
		int x, y;
		double w, h;

		public DaltonImage(String filename, double w, double h, int x, int y) {
			this.filename = filename;
			this.w =w;
			this.h=h;
			this.x=x;
			this.y=y;
		}

		@Override
		public void draw(Graphics2D g2) {
			try {
				if(!memImages.containsKey(filename)) {
					memImages.put(filename, ImageIO.read(new File(filename)));
				}
				BufferedImage i = memImages.get(filename);

				g2.translate(x, y);
				g2.scale(w/(double)i.getWidth(), 
						h/(double)i.getHeight());
				g2.drawImage(i, 0, 0, null);
				g2.scale((double)i.getWidth()/w, 
						(double)i.getHeight()/h);
				g2.translate(-x, -y);
			} catch (IOException e) {
				System.err.println("Image " + filename + " could not be shown");
			}
		}

	}



	class ApplicationFrame extends Frame {
		private static final long serialVersionUID = 1L;
		public static final int FRAMESIZE = 600;

		public ApplicationFrame() { this("ApplicationFrame v1.0"); }

		public ApplicationFrame(String title) {
			super(title);
			createUI();
			this.setVisible(true);

			//this doesn't work after jar--JUnique is terrible, find a better solution.
			//		//code to close old windows:
			//		final String appId = "ApplicationFrame";
			//		JUnique.sendMessage(appId, "close frame");
			//		JUnique.releaseLock(appId);
			//		
			//		try {
			//			Thread.sleep(2000); // JUnique takes forever to release the lock 
			//								// and is not threadsafe and has no synchronization hooks
			//								// VERY TERRIBLE!
			//			JUnique.acquireLock(appId, new MessageHandler() {
			//				public String handle(String message) {
			//					if(message.contains("close frame")) {
			//						dispose();
			//						System.err.println("closed old frame");
			//						return "successfully closed frame";
			//					}
			//					else return "a mysterious message: " + message;
			//				}
			//			});
			//		} catch (AlreadyLockedException e) {
			//			System.out.println("should never be here");
			//		} catch (InterruptedException e) {
			//			e.printStackTrace();
			//		}
		}

		protected void createUI()
		{
			setSize(FRAMESIZE, FRAMESIZE);
			center();

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
					System.exit(0);

				}
			});
		}

		public void center()
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension frameSize = getSize();
			int x = (screenSize.width - frameSize.width)/ 2;
			int y = (screenSize.height - frameSize.height) / 2;
			setLocation(x,y);
		}
	}
}

