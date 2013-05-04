package org.dalton;

import java.awt.Color;
/**
 * DaltonColor is a mutable color class (colors can be changed)
 * @author cforster
 *
 */
public class DaltonColor{
	private Color c;

	public static DaltonColor black = new DaltonColor(Color.black);
	public static DaltonColor white = new DaltonColor(Color.white);
	public static DaltonColor blue = new DaltonColor(Color.blue);
	public static DaltonColor red = new DaltonColor(Color.red);
	public static DaltonColor green = new DaltonColor(Color.green);
	public static DaltonColor cyan = new DaltonColor(Color.cyan);
	public static DaltonColor darkGray = new DaltonColor(Color.darkGray);
	public static DaltonColor gray = new DaltonColor(Color.gray);
	public static DaltonColor lightGray = new DaltonColor(Color.lightGray);
	public static DaltonColor magenta = new DaltonColor(Color.magenta);
	public static DaltonColor orange = new DaltonColor(Color.orange);
	public static DaltonColor pink = new DaltonColor(Color.pink);
	public static DaltonColor yellow = new DaltonColor(Color.yellow);
	
	/**
	 * Construct based on a Color
	 * @param c
	 */
	public DaltonColor(Color c) {
		this.c = c;
	}
	
	/**
	 * Construct based on RGB
	 * @param r red (0-255)
	 * @param g green (0-255)
	 * @param b blue (0-255)
	 */
	public DaltonColor(int r, int g, int b) {
		this.c = new Color(r,g,b);
	}
	
	public DaltonColor(float r, float g, float b) {
		this.c = new Color(r,g,b);
	}

	/**
	 * Construct based on an RGB int
	 * @param RGB 
	 */
	public DaltonColor(int RGB) {
		this.c = new Color(RGB);
	}

	/**
	 * get a Color object based on the DaltonColor
	 * @return the Color
	 */
	public Color getColor() {
		return c;
	}
	
	public int getRed() {return c.getRed(); }
	public int getBlue() {return c.getBlue(); }
	public int getGreen() {return c.getGreen(); }
	public double getHue() {return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[0]; }
	public double getSaturation() {return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[1]; }
	public double getBrightness() {return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[2]; }
	
	/**
	 * modify the hue of the color
	 * @param ratio amount to shift
	 * @return DaltonColor object
	 */
	public DaltonColor modHue(double ratio) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hsb[0]*=ratio;
		hsb[0]%=1.0;
		c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return this;
	}
	
	/**
	 * modify the saturation of the color
	 * @param ratio amount to shift
	 * @return DaltonColor object
	 */
	public DaltonColor modSaturation(double ratio) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hsb[1]*=ratio;
		hsb[1] = Math.max(0, Math.min(1, hsb[1]));
		c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return this;
	}
	
	/**
	 * modify the brightness of the color
	 * @param ratio amount to shift
	 * @return DaltonColor object
	 */
	public DaltonColor modBrightness(double ratio) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hsb[2]*=ratio;
		hsb[2] = Math.max(0, Math.min(1, hsb[2]));
		c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return this;
	}
	
	/**
	 * move the color towards the given color
	 * @param c a color to shift towards
	 * @return DaltonColor object
	 */
	public DaltonColor colorize(DaltonColor c) {
		this.c = new Color((int)Math.sqrt(c.getRed()*this.c.getRed()), 
							(int)Math.sqrt(c.getGreen()*this.c.getGreen()), 
							(int)Math.sqrt(c.getBlue()*this.c.getBlue())); 
		return this;
	}
	
	/**
	 * remove dark areas (send to black)
	 * @param threshhold brightness below which we go to black (between 0 and 1)
	 * @return DaltonColor object
	 */
	public DaltonColor cut(double threshhold) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hsb[2] = hsb[2]<threshhold?0:hsb[2]; //cut values less than threshold to 0;
		c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return this;
	}
	
	/**
	 * make a black and white image
	 * @param threshhold the brighness level to cut at
	 * @return DaltonColor object
	 */
	public DaltonColor mask(double threshhold) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hsb[2] = hsb[2]<threshhold?0:1; //cut values less than threshold to 0;
		hsb[1]=0;
		c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		return this;
	}
	
	/**
	 * invert the color
	 * @return DaltonColor object
	 */
	public DaltonColor invert() {
		this.c = new Color(255-c.getRed(), 
					255-c.getGreen(), 
					255-c.getBlue()); 
		return this;
	}
	

	//color swap
	//sepia


}
