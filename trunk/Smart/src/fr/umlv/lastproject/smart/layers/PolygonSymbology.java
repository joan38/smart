package fr.umlv.lastproject.smart.layers;

import android.graphics.Color;

/**
 * This class represent the symbology of the polygon
 * 
 * 
 * @author Fad's
 * 
 */
public class PolygonSymbology extends Symbology {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int thickness;

	private static final int DEFAULT_THICKNESS = 5;

	/**
	 * Polygon default constructor
	 * 
	 * Color : Black / Thickness : 5
	 */
	public PolygonSymbology() {
		this(DEFAULT_THICKNESS, Color.BLACK);
	}

	/**
	 * Polygon constructor
	 * 
	 * @param thickness
	 * @param color
	 */
	public PolygonSymbology(int thickness, int color) {
		super(color);
		this.thickness = thickness;
	}

	/**
	 * 
	 * @return the line thickness
	 */
	public int getThickness() {
		return this.thickness;
	}

	/**
	 * Function which set the Polygon thickness
	 * 
	 * @param thickness
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

}
