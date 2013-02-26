package fr.umlv.lastproject.smart.layers;

import android.graphics.Color;

/**
 * This class represent the Point symbology
 * 
 * @author Fad's
 * 
 */
public class PointSymbology extends Symbology {

	private static final long serialVersionUID = 1L;
	private int radius;

	private static final int VALUE_RADIUS = 5;

	/**
	 * Point default constructor
	 * 
	 * Color : Black / Radius : 5
	 */
	public PointSymbology() {
		this(VALUE_RADIUS, Color.BLACK);
	}

	/**
	 * Point constructor
	 * 
	 * @param radius
	 * @param color
	 */
	public PointSymbology(int radius, int color) {
		super(color, radius);
		this.radius = radius;
	}

	/**
	 * Function which return the Point radius
	 * 
	 * @return radius
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

}
