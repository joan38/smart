package fr.umlv.lastproject.smart.layers;

import android.graphics.Color;

/**
 * This class represent the symbology of the Line
 * 
 * @author Fad's
 * 
 */
public class LineSymbology extends Symbology {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_THICKNESS = 5;

	/**
	 * Line default constructor
	 * 
	 * Color : Black / Thickness : 5
	 */
	public LineSymbology() {
		super(Color.BLACK, DEFAULT_THICKNESS);
	}

	/**
	 * Line constructor
	 * 
	 * @param thickness
	 * @param color
	 */
	public LineSymbology(int thickness, int color) {
		super(color, thickness);
	}

}
