package fr.umlv.lastproject.smart.layers;

import java.io.Serializable;

import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Pair;

/**
 * This class represent the geometry symbology
 * 
 * @author Fad's
 * 
 */
public abstract class Symbology implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int color;

	public Symbology(int color) {
		this.color = color;
	}

	public void setColor(int color) {
		this.color = color;		
		
	}

	public int getColor() {
		
		return this.color;
	}
	
	public abstract Pair<Integer, GeometryType> getImage();
}
