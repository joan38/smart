package fr.umlv.lastproject.smart.layers;

import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

/**
 * This class represent the symbology of the polygon
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

	/**
	 * Polygon default constructor
	 * 
	 * Color : Black / Thickness : 5
	 */
	public PolygonSymbology() {
		super(Color.BLACK);
		this.thickness = 5;
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

	@Override
	public Pair<Integer, GeometryType> getImage() {
		return new Pair<Integer, Geometry.GeometryType>(getColor(), GeometryType.LINE);
	}


}
