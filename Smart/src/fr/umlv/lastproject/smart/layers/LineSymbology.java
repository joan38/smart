package fr.umlv.lastproject.smart.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
	private int thickness;

	/**
	 * Line default constructor
	 * 
	 * Color : Black / Thickness : 5
	 */
	public LineSymbology() {
		super(Color.BLACK);
		this.thickness = 5;
	}

	/**
	 * Line constructor
	 * 
	 * @param thickness
	 * @param color
	 */
	public LineSymbology(int thickness, int color) {
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
	 * Function which set the Line thickness
	 * 
	 * @param thickness
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	@Override
	public Canvas getImage() {
		final Canvas canvas = new Canvas();

		// set drawing colour
		final Paint p = new Paint();
		p.setColor(getColor());

		// draw a line onto the canvas
		canvas.drawLine(0, 0, 16, 16, p);
		return canvas;
	}
}
