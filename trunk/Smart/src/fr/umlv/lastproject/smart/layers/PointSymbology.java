package fr.umlv.lastproject.smart.layers;

import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

/**
 * This class represent the Point symbology
 * 
 * @author Fad's
 * 
 */
public class PointSymbology extends Symbology {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int radius;

	/**
	 * Point default constructor
	 * 
	 * Color : Black / Radius : 5
	 */
	public PointSymbology() {
		super(Color.BLACK);
		this.radius = 5;
	}

	/**
	 * Point constructor
	 * 
	 * @param radius
	 * @param color
	 */
	public PointSymbology(int radius, int color) {
		super(color);
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
	 * Function which set the Point radius
	 * 
	 * @param radius
	 */
	public void setRadio(int radius) {
		this.radius = radius;
	}

	@Override
	public Pair<Integer, GeometryType> getImage() {
		return new Pair<Integer, Geometry.GeometryType>(getColor(), GeometryType.LINE);
	}


}
